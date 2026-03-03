package dev.jackOtsig;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.Frozen;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.modules.entity.component.Invulnerable;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.teleport.Teleport;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.jackOtsig.map.MapManager;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Central state machine for the Hunger Games.
 *
 * Owns all subsystem managers and drives the per-second tick loop.
 * HungerGames.setup() calls {@link #onSecondTick()} every second via scheduler.
 */
public class GameManager {

    private final BarrierManager barrierManager;
    private final ScoreboardManager scoreboardManager;
    private final VoteManager voteManager;
    private final MapManager mapManager;

    /** All players currently registered in this game instance, keyed by ECS reference. */
    private final Map<Ref<EntityStore>, PlayerData> players = new ConcurrentHashMap<>();

    // volatile: read from the ECS world thread (BlockBreakSystem), written from scheduler thread.
    private volatile GameState state = GameState.WAITING;
    private final AtomicInteger aliveCount = new AtomicInteger(0);

    /**
     * Set when an admin uses /forcenext to advance through stages.
     * Suppresses the 1-player auto-end check so admins can test without the game ending.
     * Cleared on resetGame().
     */
    private volatile boolean adminMode = false;

    // Counters used inside tick methods
    private int votingSecondsLeft  = 0;
    private int preStartSecondsLeft = 0;
    private int activeSeconds      = 0;
    private int endedSecondsLeft   = 0;

    /**
     * Cached EntityStore obtained from WorldInitSystem (or PlayerDeathSystem) on
     * first entity addition. Used to schedule ECS operations from the scheduler thread.
     * Volatile for visibility across threads.
     */
    private volatile EntityStore entityStore;

    public GameManager() {
        this.barrierManager    = new BarrierManager();
        this.scoreboardManager = new ScoreboardManager();
        this.voteManager       = new VoteManager(this);
        this.mapManager        = new MapManager();
    }

    /**
     * Called by WorldInitSystem / PlayerDeathSystem when the EntityStore is first available.
     * Idempotent — only stores on the first call.
     */
    public void initEntityStore(EntityStore entityStore) {
        if (this.entityStore == null) {
            this.entityStore = entityStore;
        }
    }

    // ── Tick dispatch ─────────────────────────────────────────────────────────

    /** Called by the scheduler every second. */
    public void onSecondTick() {
        switch (state) {
            case VOTING    -> tickVoting();
            case PRE_START -> tickPreStart();
            case ACTIVE    -> tickActive();
            case ENDED     -> tickEnded();
            default        -> {} // WAITING: nothing to tick
        }
    }

    // ── State transitions ─────────────────────────────────────────────────────

    /**
     * Admin shortcut — immediately begins PRE_START regardless of player count.
     * Requires at least 2 players and WAITING state.
     *
     * @return null on success, or an error message to show the caller
     */
    public String forceStart() {
        if (state != GameState.WAITING) {
            return "Cannot force-start: game is already in " + state + " state.";
        }
        if (players.size() < 2) {
            return "Cannot force-start: need at least 2 players (have " + players.size() + ").";
        }
        HungerGames.LOGGER.atInfo().log("Force-start triggered by admin");
        transitionToPreStart();
        return null;
    }

    /**
     * Admin command — advances the game one stage forward, bypassing all normal requirements.
     * Sets adminMode=true so the 1-player win condition is suppressed.
     *
     * Cycle: WAITING → PRE_START → ACTIVE → WAITING (reset)
     *
     * @return a status string to send back to the caller (never null)
     */
    public String forceNextStage() {
        GameState before = state;
        switch (state) {
            case WAITING, VOTING -> {
                adminMode = true;
                transitionToPreStart();
            }
            case PRE_START -> transitionToActive();
            case ACTIVE, ENDED -> resetGame();
        }
        HungerGames.LOGGER.atInfo().log("Admin forced stage: " + before + " → " + state
                + (adminMode ? " [admin mode]" : ""));
        return "Advanced: " + before + " → " + state + (adminMode ? " §7(admin mode on)" : "");
    }

    public boolean isAdminMode() { return adminMode; }

    /** Called when >50% of players have voted /votestart. */
    public void onVoteThresholdReached() {
        if (state != GameState.WAITING) return;
        state = GameState.VOTING;
        votingSecondsLeft = GameConstants.VOTE_COUNTDOWN_SECONDS;
        broadcast("Vote passed! Game starting in " + votingSecondsLeft + " seconds!");
        HungerGames.LOGGER.atInfo().log("Vote threshold reached — entering VOTING");
    }

    private void transitionToPreStart() {
        state = GameState.PRE_START;
        preStartSecondsLeft = GameConstants.PRE_START_FREEZE_SECONDS;
        GameConstants.pickRandomCenter();
        mapManager.generateMap();
        mapManager.placeSpawnPositions(players.values(), entityStore);
        mapManager.spawnCornucopiaChests(entityStore);
        mapManager.spawnFieldChests(entityStore);
        freezeAllPlayers(true);
        broadcast("All players have been teleported! Game begins in "
                + preStartSecondsLeft + " seconds!");
        HungerGames.LOGGER.atInfo().log("Entering PRE_START");
    }

    private void transitionToActive() {
        state = GameState.ACTIVE;
        activeSeconds = 0;
        barrierManager.reset();
        // HUD setup and unfreeze both run on the world thread inside freezeAllPlayers(false).
        freezeAllPlayers(false);
        broadcast("The Hunger Games have begun! May the odds be ever in your favor.");
        HungerGames.LOGGER.atInfo().log("Entering ACTIVE with " + aliveCount.get() + " players");
    }

    private void transitionToEnded(PlayerData winner, Store<EntityStore> store) {
        state = GameState.ENDED;
        endedSecondsLeft = GameConstants.END_RESET_DELAY_SECONDS;

        String winnerName = null;
        if (winner != null) {
            winnerName = winner.getDisplayName();
            // Make the winner invulnerable so they can't be hurt during the end phase.
            store.addComponent(winner.getPlayer().getReference(),
                    Invulnerable.getComponentType(), Invulnerable.INSTANCE);
            broadcast("§6" + winnerName + " §ewins the Hunger Games with "
                    + winner.getKills() + " kill(s)!");
        } else {
            broadcast("Everyone is dead. No winner this round.");
        }
        scoreboardManager.showWinner(winnerName);
        HungerGames.LOGGER.atInfo().log("Entering ENDED — winner: "
                + (winner != null ? winner.getDisplayName() : "none"));
    }

    // ── Per-state tick logic ──────────────────────────────────────────────────

    private void tickVoting() {
        votingSecondsLeft--;
        if (votingSecondsLeft <= 0) {
            transitionToPreStart();
        } else if (votingSecondsLeft <= 10 || votingSecondsLeft % 10 == 0) {
            broadcast("Game starting in " + votingSecondsLeft + " seconds!");
        }
    }

    private void tickPreStart() {
        preStartSecondsLeft--;
        if (preStartSecondsLeft <= 0) {
            transitionToActive();
        } else if (preStartSecondsLeft <= 5) {
            broadcast("" + preStartSecondsLeft + "...");
        }
    }

    private void tickActive() {
        activeSeconds++;
        Collection<PlayerData> alive = getAlivePlayers();
        barrierManager.onSecondTick(alive, entityStore);
        scoreboardManager.onSecondTick(activeSeconds, aliveCount.get(), players.values());
    }

    private void tickEnded() {
        endedSecondsLeft--;
        if (endedSecondsLeft <= 0) {
            resetGame();
        }
    }

    // ── Player lifecycle ──────────────────────────────────────────────────────

    /** Called by PlayerJoinHandler when a player connects. */
    public void onPlayerJoin(Player player) {
        if (state != GameState.WAITING) {
            // Late-joining players go into Creative mode via world.execute() so ECS ops
            // run on the world thread. We schedule using the cached EntityStore.
            EntityStore es = this.entityStore;
            if (es != null) {
                Ref<EntityStore> ref = player.getReference();
                es.getWorld().execute(() -> {
                    Store<EntityStore> store = es.getStore();
                    Player.setGameMode(ref, GameMode.Creative, store);
                    teleportToCornucopia(ref, store);
                });
            }
            return;
        }
        Ref<EntityStore> id = player.getReference();
        if (players.containsKey(id)) return;

        PlayerData pd = new PlayerData(player);
        players.put(id, pd);
        aliveCount.incrementAndGet();

        // Set Adventure mode, make invulnerable, and teleport to the cornucopia.
        EntityStore es = this.entityStore;
        if (es != null) {
            Ref<EntityStore> ref = player.getReference();
            es.getWorld().execute(() -> {
                Store<EntityStore> store = es.getStore();
                Player.setGameMode(ref, GameMode.Adventure, store);
                store.addComponent(ref, Invulnerable.getComponentType(), Invulnerable.INSTANCE);
                teleportToCornucopia(ref, store);
            });
        }
        broadcast(player.getDisplayName() + " joined! ("
                + players.size() + "/" + GameConstants.MAX_PLAYERS + ")");

        if (players.size() >= GameConstants.MAX_PLAYERS) {
            transitionToPreStart();
        }
    }

    /**
     * Called by PlayerDeathSystem when a player's DeathComponent is added.
     * Runs on the ECS world thread — store operations are safe to call directly.
     *
     * @param victim    the player who died
     * @param killer    the player who delivered the killing blow (may be null)
     * @param victimRef the ECS ref of the victim
     * @param store     the current EntityStore accessor
     */
    public void onPlayerDeath(Player victim, Player killer,
                               Ref<EntityStore> victimRef, Store<EntityStore> store) {
        PlayerData victimData = players.get(victim.getReference());
        if (victimData == null || !victimData.isAlive()) return;

        victimData.setAlive(false);
        victimData.resetSecondsOutsideBorder();
        aliveCount.decrementAndGet();
        barrierManager.onPlayerEliminated();
        scoreboardManager.removePlayer(victimRef);

        if (killer != null) {
            PlayerData killerData = players.get(killer.getReference());
            if (killerData != null) {
                killerData.incrementKills();
                broadcast(killerData.getDisplayName() + " eliminated "
                        + victimData.getDisplayName() + "! ("
                        + aliveCount.get() + " players remaining)");
            }
        } else {
            broadcast(victimData.getDisplayName() + " was eliminated! ("
                    + aliveCount.get() + " players remaining)");
        }

        setSpectator(victimRef, store);
        checkWinCondition(store);
    }

    // ── Win condition ─────────────────────────────────────────────────────────

    private void checkWinCondition(Store<EntityStore> store) {
        if (state != GameState.ACTIVE) return;
        if (adminMode) return; // admin is testing — don't auto-end on low player count

        int alive = aliveCount.get();
        if (alive == 1) {
            PlayerData winner = getAlivePlayers().stream().findFirst().orElse(null);
            transitionToEnded(winner, store);
        } else if (alive == 0) {
            transitionToEnded(null, store);
        }
    }

    // ── Reset ─────────────────────────────────────────────────────────────────

    /** Clears all state and returns to WAITING. */
    public void resetGame() {
        // Restore all players to Adventure mode so they can participate in the next round.
        // Capture refs before clearing the map so the lambda has a stable snapshot.
        EntityStore es = this.entityStore;
        if (es != null) {
            List<Ref<EntityStore>> refs = players.values().stream()
                    .map(pd -> pd.getPlayer().getReference())
                    .toList();
            es.getWorld().execute(() -> {
                Store<EntityStore> store = es.getStore();
                for (Ref<EntityStore> ref : refs) {
                    Player.setGameMode(ref, GameMode.Adventure, store);
                    // Re-apply lobby protection: Invulnerable so players can't be harmed.
                    store.removeComponentIfExists(ref, Invulnerable.getComponentType());
                    store.addComponent(ref, Invulnerable.getComponentType(), Invulnerable.INSTANCE);
                    teleportToCornucopia(ref, store);
                }
            });
        }
        players.clear();
        aliveCount.set(0);
        activeSeconds     = 0;
        votingSecondsLeft = 0;
        preStartSecondsLeft = 0;
        endedSecondsLeft  = 0;
        voteManager.reset();
        barrierManager.reset();
        scoreboardManager.clearScoreboard();
        adminMode = false;
        state = GameState.WAITING;
        HungerGames.LOGGER.atInfo().log("Game reset — returning to WAITING");
    }

    // ── Accessors used by sub-managers ────────────────────────────────────────

    public GameState getState()               { return state; }
    public int       getPlayerCount()         { return players.size(); }
    public VoteManager getVoteManager()       { return voteManager; }
    public EntityStore getEntityStore()       { return entityStore; }

    /** Returns a multi-line status string suitable for an admin command. */
    public String getStatus() {
        StringBuilder sb = new StringBuilder();
        sb.append("§e=== Hunger Games Status ===\n");
        sb.append("§7State: §f").append(state).append("\n");
        sb.append("§7Players: §f").append(players.size()).append(" / ")
          .append(GameConstants.MAX_PLAYERS);

        switch (state) {
            case WAITING -> {
                int votes = voteManager.getVoteCount();
                int needed = (int) Math.ceil(players.size() * GameConstants.VOTE_START_THRESHOLD);
                sb.append("\n§7Votes: §f").append(votes).append(" / ").append(needed);
            }
            case VOTING ->
                sb.append("\n§7Voting ends in: §f").append(votingSecondsLeft).append("s");
            case PRE_START ->
                sb.append("\n§7Game starts in: §f").append(preStartSecondsLeft).append("s");
            case ACTIVE -> {
                int minutes = activeSeconds / 60;
                int seconds = activeSeconds % 60;
                sb.append("\n§7Alive: §f").append(aliveCount.get());
                sb.append("\n§7Time: §f").append(String.format("%02d:%02d", minutes, seconds));
                sb.append("\n§7Border radius: §f")
                  .append(String.format("%.1f", barrierManager.getCurrentRadius())).append(" tiles");
            }
            case ENDED ->
                sb.append("\n§7Resetting in: §f").append(endedSecondsLeft).append("s");
        }

        sb.append("\n§7Map origin: §f(")
          .append(String.format("%.0f, %.0f, %.0f",
                  GameConstants.MAP_ORIGIN_X, GameConstants.MAP_ORIGIN_Y, GameConstants.MAP_ORIGIN_Z))
          .append(")");
        sb.append("\n§7Cornucopia: §f(")
          .append(String.format("%.0f, %.0f, %.0f",
                  GameConstants.CENTER_X, GameConstants.CENTER_Y, GameConstants.CENTER_Z))
          .append(")");
        return sb.toString();
    }

    /** Broadcasts a message to every player currently in the game. */
    public void broadcast(String message) {
        for (PlayerData pd : players.values()) {
            pd.getPlayer().sendMessage(Message.raw(message));
        }
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private Collection<PlayerData> getAlivePlayers() {
        return players.values().stream()
                .filter(PlayerData::isAlive)
                .toList();
    }

    /**
     * Switches a dead player to Creative mode (no Spectator in Hytale) and hides
     * them from all living players.
     * Called from PlayerDeathSystem — already on the world thread, store is valid.
     */
    private void setSpectator(Ref<EntityStore> victimRef, Store<EntityStore> store) {
        // Switch to Creative so the dead player can't interact with the world.
        Player.setGameMode(victimRef, GameMode.Creative, store);

        // Get the victim's UUID to pass to each alive player's HiddenPlayersManager.
        UUID victimUuid = store.getComponent(victimRef, UUIDComponent.getComponentType()).getUuid();
        for (PlayerData pd : players.values()) {
            if (pd.isAlive()) {
                store.getComponent(pd.getPlayer().getReference(), PlayerRef.getComponentType())
                        .getHiddenPlayersManager().hidePlayer(victimUuid);
            }
        }
    }

    /**
     * Freezes or unfreezes all players using the Frozen marker component.
     * When unfreezing (game going ACTIVE), also removes Invulnerable and sets up HUDs.
     * Runs via world.execute() because it's called from the scheduler thread.
     */
    private void freezeAllPlayers(boolean freeze) {
        EntityStore es = this.entityStore;
        if (es == null) return;
        es.getWorld().execute(() -> {
            Store<EntityStore> store = es.getStore();
            for (PlayerData pd : players.values()) {
                Ref<EntityStore> ref = pd.getPlayer().getReference();
                if (freeze) {
                    store.addComponent(ref, Frozen.getComponentType(), Frozen.get());
                } else {
                    String name = pd.getDisplayName();
                    HungerGames.LOGGER.atInfo().log("unfreeze [" + name + "]: removing Frozen");
                    store.removeComponentIfExists(ref, Frozen.getComponentType());
                    HungerGames.LOGGER.atInfo().log("unfreeze [" + name + "]: removing Invulnerable");
                    store.removeComponentIfExists(ref, Invulnerable.getComponentType());
                    HungerGames.LOGGER.atInfo().log("unfreeze [" + name + "]: setting Adventure mode");
                    Player.setGameMode(ref, GameMode.Adventure, store);
                    HungerGames.LOGGER.atInfo().log("unfreeze [" + name + "]: adding HUD");
                    scoreboardManager.addPlayer(pd, store);
                    HungerGames.LOGGER.atInfo().log("unfreeze [" + name + "]: done");
                }
            }
        });
    }

    /**
     * Teleports a player to the cornucopia position.
     * Must be called on the world thread (inside world.execute()).
     */
    private void teleportToCornucopia(Ref<EntityStore> ref, Store<EntityStore> store) {
        store.putComponent(ref, Teleport.getComponentType(),
                Teleport.createForPlayer(
                        new Vector3d(GameConstants.CENTER_X, GameConstants.CENTER_Y, GameConstants.CENTER_Z),
                        new Vector3f(0, 0, 0)));
    }
}
