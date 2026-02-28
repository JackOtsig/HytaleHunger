package dev.jackOtsig;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import dev.jackOtsig.map.MapManager;

import java.nio.charset.StandardCharsets;
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
    private int votingSecondsLeft = 0;
    private int preStartSecondsLeft = 0;
    private int activeSeconds = 0;
    private int endedSecondsLeft = 0;

    public GameManager() {
        this.barrierManager = new BarrierManager();
        this.scoreboardManager = new ScoreboardManager();
        this.voteManager = new VoteManager(this);
        this.mapManager = new MapManager();
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
        mapManager.spawnCornucopiaChests();
        mapManager.spawnFieldChests();
        mapManager.placeSpawnPositions(players.values());
        freezeAllPlayers(true);
        broadcast("All players have been teleported! Game begins in "
                + preStartSecondsLeft + " seconds!");
        HungerGames.LOGGER.atInfo().log("Entering PRE_START");
    }

    private void transitionToActive() {
        state = GameState.ACTIVE;
        activeSeconds = 0;
        freezeAllPlayers(false);
        barrierManager.reset();
        broadcast("The Hunger Games have begun! May the odds be ever in your favor.");
        HungerGames.LOGGER.atInfo().log("Entering ACTIVE with " + aliveCount + " players");
    }

    private void transitionToEnded(PlayerData winner) {
        state = GameState.ENDED;
        endedSecondsLeft = GameConstants.END_RESET_DELAY_SECONDS;
        scoreboardManager.clearScoreboard();

        if (winner != null) {
            String name = winner.getDisplayName();
            broadcast("§6" + name + " §ewins the Hunger Games with "
                    + winner.getKills() + " kill(s)!");
            showWinnerTitle(winner.getPlayer(), name);
        } else {
            broadcast("Everyone is dead. No winner this round.");
        }
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
        barrierManager.onSecondTick(alive);
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
            // TODO: Put late-joining players into spectator mode — Hytale API unknown
            return;
        }
        UUID id = toUuid(player);
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
     * Called by the death event handler when a player dies.
     *
     * @param victim the player who died
     * @param killer the player who delivered the killing blow (may be null)
     */
    public void onPlayerDeath(Player victim, Player killer) {
        PlayerData victimData = players.get(toUuid(victim));
        if (victimData == null || !victimData.isAlive()) return;

        victimData.setAlive(false);
        victimData.resetSecondsOutsideBorder();
        aliveCount--;
        barrierManager.onPlayerEliminated();

        if (killer != null) {
            PlayerData killerData = players.get(toUuid(killer));
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

        setSpectator(victim);
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
        aliveCount = 0;
        activeSeconds = 0;
        votingSecondsLeft = 0;
        preStartSecondsLeft = 0;
        endedSecondsLeft = 0;
        voteManager.reset();
        barrierManager.reset();
        scoreboardManager.clearScoreboard();
        state = GameState.WAITING;
        HungerGames.LOGGER.atInfo().log("Game reset — returning to WAITING");
    }

    // ── Accessors used by sub-managers ────────────────────────────────────────

    public GameState getState() { return state; }
    public int getPlayerCount() { return players.size(); }
    public VoteManager getVoteManager() { return voteManager; }

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

    private void freezeAllPlayers(boolean frozen) {
        for (PlayerData pd : players.values()) {
            // TODO: Freeze / unfreeze player movement — Hytale API unknown
        }
    }

    private void setSpectator(Player player) {
        // TODO: Set player to spectator mode and make invisible — Hytale API unknown
    }

    private void showWinnerTitle(Player winner, String name) {
        // TODO: Show title / popup overlay to all players — Hytale API unknown
    }

    private UUID toUuid(Player player) {
        // TODO: use player.getUniqueId() — Hytale API unknown
        return UUID.nameUUIDFromBytes(
                player.getDisplayName().getBytes(StandardCharsets.UTF_8));
    }
}
