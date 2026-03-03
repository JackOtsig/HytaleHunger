package dev.jackOtsig;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import java.util.Collection;

/**
 * Manages the in-game HUD.
 * HUD is currently disabled — CustomUI markup causes a client crash.
 * All methods are no-ops until the HUD syntax is fixed.
 */
public class ScoreboardManager {

    public void addPlayer(PlayerData pd, Store<EntityStore> store) {}

    public void removePlayer(Ref<EntityStore> ref) {}

    public void onSecondTick(int activeSeconds, int aliveCount, Collection<PlayerData> players) {}

    public void showWinner(String winnerName) {}

    public void clearScoreboard() {}
}
