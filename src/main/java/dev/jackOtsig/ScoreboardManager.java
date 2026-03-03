package dev.jackOtsig;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.jackOtsig.hud.GameHud;
import com.hypixel.hytale.logger.HytaleLogger;

import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Manages per-player {@link GameHud} instances.
 *
 * Each player gets a HUD shown on join and hidden/cleared on game end.
 */
public class ScoreboardManager {

    /** Active HUDs keyed by player entity reference. */
    private final Map<Ref<EntityStore>, GameHud> huds = new LinkedHashMap<>();

    /**
     * Creates and shows a HUD for the given player.
     * Must be called from the ECS world thread (inside world.execute()).
     */
    public void addPlayer(PlayerData pd, Store<EntityStore> store) {
        PlayerRef playerRef = store.getComponent(
                pd.getPlayer().getReference(), PlayerRef.getComponentType());
        if (playerRef == null) {
            HungerGames.LOGGER.atWarning().log(
                    "addPlayer: PlayerRef null for " + pd.getDisplayName() + " — HUD skipped");
            return;
        }
        GameHud hud = new GameHud(playerRef);
        hud.show();
        huds.put(pd.getPlayer().getReference(), hud);
    }

    /**
     * Removes and stops updating the HUD for a player (e.g. when they are
     * eliminated and switched to spectator).
     */
    public void removePlayer(Ref<EntityStore> ref) {
        huds.remove(ref);
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
