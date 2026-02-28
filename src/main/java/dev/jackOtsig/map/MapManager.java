package dev.jackOtsig.map;

import com.hypixel.hytale.server.core.entity.entities.Player;
import dev.jackOtsig.GameConstants;
import dev.jackOtsig.PlayerData;
import dev.jackOtsig.loot.ItemTier;
import dev.jackOtsig.loot.LootTable;

import java.util.Collection;
import java.util.List;
import java.util.Random;

/**
 * Handles terrain generation, spawn-ring placement, and chest spawning.
 *
 * Many methods contain TODO stubs where the Hytale terrain/block/item API
 * is not yet known.
 */
public class MapManager {

    private static final Random RNG = new Random();

    /** World center (tile) coordinates — assumed 0,0 for now. */
    private static final double CENTER_X = 0.0;
    private static final double CENTER_Y = 64.0;  // default ground level
    private static final double CENTER_Z = 0.0;

    private final LootTable lootTable = new LootTable();

    /** Generates the map terrain around the center point. */
    public void generateMap() {
        // TODO: Generate terrain using Hytale world/terrain API — Hytale API unknown
    }

    /**
     * Teleports each player to an evenly-spaced position on the spawn ring,
     * facing the cornucopia at the center.
     */
    public void placeSpawnPositions(Collection<PlayerData> playerDatas) {
        List<PlayerData> list = List.copyOf(playerDatas);
        int count = list.size();
        for (int i = 0; i < count; i++) {
            double angle = (2 * Math.PI * i) / count;
            double x = CENTER_X + GameConstants.SPAWN_RING_RADIUS * Math.sin(angle);
            double z = CENTER_Z + GameConstants.SPAWN_RING_RADIUS * Math.cos(angle);
            // Yaw so the player faces the center
            float yaw = (float) Math.toDegrees(Math.atan2(Math.sin(angle), -Math.cos(angle)));
            teleportPlayer(list.get(i).getPlayer(), x, CENTER_Y, z, yaw);
        }
    }

    /** Spawns CORNUCOPIA_CHEST_COUNT chests in a tight cluster at the center. */
    public void spawnCornucopiaChests() {
        for (int i = 0; i < GameConstants.CORNUCOPIA_CHEST_COUNT; i++) {
            // Small random offset within a 3-tile radius of center
            double ox = (RNG.nextDouble() * 6) - 3;
            double oz = (RNG.nextDouble() * 6) - 3;
            List<String> items = lootTable.rollChest(ItemTier.CORNUCOPIA);
            spawnChest(CENTER_X + ox, CENTER_Y, CENTER_Z + oz, items);
        }
    }

    /** Spawns FIELD_CHEST_COUNT chests randomly across the map. */
    public void spawnFieldChests() {
        double r = GameConstants.INITIAL_BORDER_RADIUS;
        for (int i = 0; i < GameConstants.FIELD_CHEST_COUNT; i++) {
            // Uniform random position within the initial border circle
            double angle = RNG.nextDouble() * 2 * Math.PI;
            double dist  = Math.sqrt(RNG.nextDouble()) * r;  // sqrt for uniform distribution
            double x = CENTER_X + dist * Math.cos(angle);
            double z = CENTER_Z + dist * Math.sin(angle);
            List<String> items = lootTable.rollChest(ItemTier.FIELD);
            spawnChest(x, CENTER_Y, z, items);
        }
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private void teleportPlayer(Player player, double x, double y, double z, float yaw) {
        // TODO: Teleport player to (x, y, z) with yaw — Hytale API unknown
    }

    private void spawnChest(double x, double y, double z, List<String> itemIds) {
        // TODO: Place a chest block at (x, y, z) — Hytale API unknown
        // TODO: Insert itemIds into the chest inventory — Hytale API unknown
    }
}
