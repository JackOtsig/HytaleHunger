package dev.jackOtsig;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Manages the in-game HUD: alive count, elapsed time, and kill leaderboard.
 *
 * Actual scoreboard/HUD rendering is stubbed — Hytale scoreboard API unknown.
 */
public class ScoreboardManager {

    /** Called every second during ACTIVE state. */
    public void onSecondTick(int activeSeconds, int aliveCount, Collection<PlayerData> players) {
        String timeStr = formatTime(activeSeconds);
        List<PlayerData> topKillers = players.stream()
                .sorted(Comparator.comparingInt(PlayerData::getKills).reversed())
                .limit(5)
                .collect(Collectors.toList());

        updateScoreboard(timeStr, aliveCount, topKillers);
    }

    /** Clears the scoreboard at the end of a game. */
    public void clearScoreboard() {
        // TODO: Remove HUD / scoreboard from all players — Hytale API unknown
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private void updateScoreboard(String time, int aliveCount, List<PlayerData> topKillers) {
        // TODO: Push HUD lines to players via Hytale scoreboard/HUD API — Hytale API unknown
        // Suggested layout:
        //   "§eHunger Games"
        //   "§7Time: §f" + time
        //   "§7Alive: §f" + aliveCount
        //   "§7── Top Kills ──"
        //   ... topKillers: "§f" + name + " §7(" + kills + ")"
    }

    private String formatTime(int totalSeconds) {
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}
