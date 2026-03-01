package dev.jackOtsig;

import dev.jackOtsig.hud.GameHud;

import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Manages per-player {@link GameHud} instances.
 *
 * Each player gets a HUD shown on join and hidden/cleared on game end.
 */
public class ScoreboardManager {

    /** Active HUDs keyed by player UUID. */
    private final Map<UUID, GameHud> huds = new LinkedHashMap<>();

    /**
     * Creates and shows a HUD for the given player.
     * Called from GameManager when a player joins a game session.
     */
    public void addPlayer(PlayerData pd) {
        GameHud hud = new GameHud(pd.getPlayer().getPlayerRef());
        hud.show();
        huds.put(pd.getUuid(), hud);
    }

    /**
     * Removes and stops updating the HUD for a player (e.g. when they are
     * eliminated and switched to spectator).
     */
    public void removePlayer(UUID uuid) {
        huds.remove(uuid);
    }

    /** Called every second during ACTIVE state. */
    public void onSecondTick(int activeSeconds, int aliveCount, Collection<PlayerData> players) {
        String timeStr = formatTime(activeSeconds);
        List<PlayerData> topKillers = players.stream()
                .sorted(Comparator.comparingInt(PlayerData::getKills).reversed())
                .limit(5)
                .collect(Collectors.toList());

        for (GameHud hud : huds.values()) {
            hud.setData(timeStr, aliveCount, topKillers);
            hud.refresh();
        }
    }

    /**
     * Switches all active HUDs to display the winner screen, then clears the map.
     * Pass {@code null} to show a "no winner" message.
     */
    public void showWinner(String winnerName) {
        String display = winnerName != null ? winnerName : "Nobody";
        for (GameHud hud : huds.values()) {
            hud.setWinner(display);
            hud.refresh();
        }
        huds.clear();
    }

    /** Clears all HUDs without showing a winner (e.g. on hard reset). */
    public void clearScoreboard() {
        huds.clear();
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private String formatTime(int totalSeconds) {
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}
