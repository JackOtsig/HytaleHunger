package dev.jackOtsig;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.Frozen;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.jackOtsig.map.MapManager;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

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

    /** All players currently registered in this game instance. */
    private final Map<UUID, PlayerData> players = new LinkedHashMap<>();

    private GameState state = GameState.WAITING;
    private int aliveCount = 0;

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
        mapManager.generateMap();
        mapManager.spawnCornucopiaChests(entityStore);
        mapManager.spawnFieldChests(entityStore);
        mapManager.placeSpawnPositions(players.values(), entityStore);
        freezeAllPlayers(true);
        broadcast("All players have been teleported! Game begins in "
                + preStartSecondsLeft + " seconds!");
        HungerGames.LOGGER.atInfo().log("Entering PRE_START");
    }

    private void transitionToActive() {
        state = GameState.ACTIVE;
        activeSeconds = 0;
        barrierManager.reset();

        // Show HUD for all players at game start.
        for (PlayerData pd : players.values()) {
            scoreboardManager.addPlayer(pd);
        }

        freezeAllPlayers(false);
        broadcast("The Hunger Games have begun! May the odds be ever in your favor.");
        HungerGames.LOGGER.atInfo().log("Entering ACTIVE with " + aliveCount + " players");
    }

    private void transitionToEnded(PlayerData winner) {
        state = GameState.ENDED;
        endedSecondsLeft = GameConstants.END_RESET_DELAY_SECONDS;

        String winnerName = null;
        if (winner != null) {
            winnerName = winner.getDisplayName();
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
        scoreboardManager.onSecondTick(activeSeconds, aliveCount, players.values());
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
                Ref<EntityStore> ref = player.getPlayerRef().getReference();
                es.getWorld().execute(() ->
                        Player.setGameMode(ref, GameMode.Creative, es.getStore()));
            }
            return;
        }
        UUID id = player.getUuid();
        if (players.containsKey(id)) return;

        PlayerData pd = new PlayerData(player);
        players.put(id, pd);
        aliveCount++;
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
        PlayerData victimData = players.get(victim.getUuid());
        if (victimData == null || !victimData.isAlive()) return;

        victimData.setAlive(false);
        victimData.resetSecondsOutsideBorder();
        aliveCount--;
        barrierManager.onPlayerEliminated();
        scoreboardManager.removePlayer(victimData.getUuid());

        if (killer != null) {
            PlayerData killerData = players.get(killer.getUuid());
            if (killerData != null) {
                killerData.incrementKills();
                broadcast(killerData.getDisplayName() + " eliminated "
                        + victimData.getDisplayName() + "! ("
                        + aliveCount + " players remaining)");
            }
        } else {
            broadcast(victimData.getDisplayName() + " was eliminated! ("
                    + aliveCount + " players remaining)");
        }

        setSpectator(victim, victimRef, store);
        checkWinCondition();
    }

    // ── Win condition ─────────────────────────────────────────────────────────

    private void checkWinCondition() {
        if (state != GameState.ACTIVE) return;

        if (aliveCount == 1) {
            PlayerData winner = getAlivePlayers().stream().findFirst().orElse(null);
            transitionToEnded(winner);
        } else if (aliveCount == 0) {
            transitionToEnded(null);
        }
    }

    // ── Reset ─────────────────────────────────────────────────────────────────

    /** Clears all state and returns to WAITING. */
    public void resetGame() {
        players.clear();
        aliveCount        = 0;
        activeSeconds     = 0;
        votingSecondsLeft = 0;
        preStartSecondsLeft = 0;
        endedSecondsLeft  = 0;
        voteManager.reset();
        barrierManager.reset();
        scoreboardManager.clearScoreboard();
        state = GameState.WAITING;
        HungerGames.LOGGER.atInfo().log("Game reset — returning to WAITING");
    }

    // ── Accessors used by sub-managers ────────────────────────────────────────

    public GameState getState()               { return state; }
    public int       getPlayerCount()         { return players.size(); }
    public VoteManager getVoteManager()       { return voteManager; }

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
    private void setSpectator(Player victim, Ref<EntityStore> victimRef, Store<EntityStore> store) {
        // Switch to Creative so the dead player can't interact with the world.
        Player.setGameMode(victimRef, GameMode.Creative, store);

        // Hide the dead player from all currently-alive players.
        UUID victimUuid = victim.getUuid();
        for (PlayerData pd : players.values()) {
            if (pd.isAlive()) {
                pd.getPlayer().getPlayerRef().getHiddenPlayersManager().hidePlayer(victimUuid);
            }
        }
    }

    /**
     * Freezes or unfreezes all players using the Frozen marker component.
     * Runs via world.execute() because it's called from the scheduler thread.
     */
    private void freezeAllPlayers(boolean freeze) {
        EntityStore es = this.entityStore;
        if (es == null) return;
        es.getWorld().execute(() -> {
            Store<EntityStore> store = es.getStore();
            for (PlayerData pd : players.values()) {
                Ref<EntityStore> ref = pd.getPlayer().getPlayerRef().getReference();
                if (freeze) {
                    store.addComponent(ref, Frozen.getComponentType(), Frozen.get());
                } else {
                    store.removeComponentIfExists(ref, Frozen.getComponentType());
                }
            }
        });
    }
}
