package dev.jackOtsig.map;

import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.teleport.PendingTeleport;
import com.hypixel.hytale.server.core.modules.entity.teleport.Teleport;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.jackOtsig.GameConstants;
import dev.jackOtsig.PlayerData;
import dev.jackOtsig.loot.ItemTier;
import dev.jackOtsig.loot.LootTable;

import java.util.Collection;
import java.util.List;
import java.util.Random;

/**
 * Handles terrain generation, spawn-ring placement, and chest spawning.
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
     *
     * @param entityStore the world's EntityStore; may be null if the world has not
     *                    started yet, in which case teleports are skipped.
     */
    public void placeSpawnPositions(Collection<PlayerData> playerDatas, EntityStore entityStore) {
        if (entityStore == null) return;
        List<PlayerData> list = List.copyOf(playerDatas);
        int count = list.size();
        Store<EntityStore> store = entityStore.getStore();
        for (int i = 0; i < count; i++) {
            double angle = (2 * Math.PI * i) / count;
            double x = CENTER_X + GameConstants.SPAWN_RING_RADIUS * Math.sin(angle);
            double z = CENTER_Z + GameConstants.SPAWN_RING_RADIUS * Math.cos(angle);
            // Yaw so the player faces the center (y = yaw in Vector3f).
            float yaw = (float) Math.toDegrees(Math.atan2(Math.sin(angle), -Math.cos(angle)));
            teleportPlayer(list.get(i).getPlayer(), x, CENTER_Y, z, yaw, store);
        }
    }

    /** Spawns CORNUCOPIA_CHEST_COUNT chests in a tight cluster at the center. */
    public void spawnCornucopiaChests() {
        for (int i = 0; i < GameConstants.CORNUCOPIA_CHEST_COUNT; i++) {
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
            double angle = RNG.nextDouble() * 2 * Math.PI;
            double dist  = Math.sqrt(RNG.nextDouble()) * r;
            double x = CENTER_X + dist * Math.cos(angle);
            double z = CENTER_Z + dist * Math.sin(angle);
            List<String> items = lootTable.rollChest(ItemTier.FIELD);
            spawnChest(x, CENTER_Y, z, items);
        }
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private void teleportPlayer(Player player, double x, double y, double z, float yaw,
                                 Store<EntityStore> store) {
        // Teleport API:
        //   new Teleport(Vector3d pos, Vector3f rotation) — rotation: x=pitch, y=yaw, z=roll
        //   PendingTeleport.queueTeleport(Teleport) queues it on the entity.
        PendingTeleport pt = store.getComponent(
                player.getPlayerRef().getReference(),
                PendingTeleport.getComponentType());
        if (pt == null) return;
        pt.queueTeleport(new Teleport(
                new Vector3d(x, y, z),
                new Vector3f(0, yaw, 0)));  // pitch=0, yaw=yaw, roll=0
    }

    private void spawnChest(double x, double y, double z, List<String> itemIds) {
        // TODO: Place a chest block at (x, y, z) — Hytale block-placement API unknown
        // TODO: Insert itemIds into the chest inventory — Hytale item API unknown
    }
}
