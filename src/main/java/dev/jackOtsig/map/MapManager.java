package dev.jackOtsig.map;

import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.SimpleItemContainer;
import com.hypixel.hytale.server.core.modules.entity.teleport.PendingTeleport;
import com.hypixel.hytale.server.core.modules.entity.teleport.Teleport;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.meta.state.ItemContainerState;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.jackOtsig.GameConstants;
import dev.jackOtsig.HungerGames;
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

    /** Block type key for field chests (small crude chest). */
    private static final String CHEST_FIELD       = "Furniture_Crude_Chest_Small";
    /** Block type key for cornucopia chests (large epic chest). */
    private static final String CHEST_CORNUCOPIA  = "Furniture_Dungeon_Chest_Epic";

    private static final Random RNG = new Random();

    private final LootTable lootTable = new LootTable();

    /**
     * No-op — the map is hand-built in the world editor.
     * Use /setcenter while standing at the cornucopia to set the origin before each game.
     * All chest placement, spawn ring, and barrier calculations read from
     * {@link GameConstants#CENTER_X}/{@link GameConstants#CENTER_Y}/{@link GameConstants#CENTER_Z}.
     */
    public void generateMap() {}

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
            double x = GameConstants.CENTER_X + GameConstants.SPAWN_RING_RADIUS * Math.sin(angle);
            double z = GameConstants.CENTER_Z + GameConstants.SPAWN_RING_RADIUS * Math.cos(angle);
            // Yaw so the player faces the center (y = yaw in Vector3f).
            float yaw = (float) Math.toDegrees(Math.atan2(Math.sin(angle), -Math.cos(angle)));
            teleportPlayer(list.get(i).getPlayer(), x, GameConstants.CENTER_Y, z, yaw, store);
        }
    }

    /** Spawns CORNUCOPIA_CHEST_COUNT chests in a tight cluster at the center. */
    public void spawnCornucopiaChests(EntityStore entityStore) {
        if (entityStore == null) return;
        for (int i = 0; i < GameConstants.CORNUCOPIA_CHEST_COUNT; i++) {
            double ox = (RNG.nextDouble() * 6) - 3;
            double oz = (RNG.nextDouble() * 6) - 3;
            List<String> items = lootTable.rollChest(ItemTier.CORNUCOPIA);
            spawnChest(GameConstants.CENTER_X + ox, GameConstants.CENTER_Y,
                       GameConstants.CENTER_Z + oz, CHEST_CORNUCOPIA, items, entityStore);
        }
    }

    /** Spawns FIELD_CHEST_COUNT chests randomly across the map. */
    public void spawnFieldChests(EntityStore entityStore) {
        if (entityStore == null) return;
        double r = GameConstants.INITIAL_BORDER_RADIUS;
        for (int i = 0; i < GameConstants.FIELD_CHEST_COUNT; i++) {
            double angle = RNG.nextDouble() * 2 * Math.PI;
            double dist  = Math.sqrt(RNG.nextDouble()) * r;
            double x = GameConstants.CENTER_X + dist * Math.cos(angle);
            double z = GameConstants.CENTER_Z + dist * Math.sin(angle);
            List<String> items = lootTable.rollChest(ItemTier.FIELD);
            spawnChest(x, GameConstants.CENTER_Y, z, CHEST_FIELD, items, entityStore);
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

    /**
     * Places a chest block at (x, y, z) and fills it with the given items.
     * Block ops run on the world thread via {@code world.execute()}.
     *
     * @param blockTypeKey {@link #CHEST_FIELD} or {@link #CHEST_CORNUCOPIA}
     * @param itemIds      item ID strings from {@code LootTable.rollChest()}
     */
    private void spawnChest(double x, double y, double z,
                             String blockTypeKey, List<String> itemIds,
                             EntityStore entityStore) {
        int bx = (int) Math.floor(x);
        int by = (int) Math.floor(y);
        int bz = (int) Math.floor(z);

        entityStore.getWorld().execute(() -> {
            World world = entityStore.getWorld();

            // Place the chest block (rotation 0 = default facing).
            world.setBlock(bx, by, bz, blockTypeKey, 0);

            // Retrieve the block state — true: initialise/load if not yet present.
            if (!(world.getState(bx, by, bz, true) instanceof ItemContainerState ics)) {
                HungerGames.LOGGER.atWarning().log(
                        "spawnChest: no ItemContainerState at " + bx + "," + by + "," + bz
                        + " for block " + blockTypeKey);
                return;
            }

            // Build a fresh container and fill each slot.
            SimpleItemContainer container = new SimpleItemContainer((short) itemIds.size());
            for (int i = 0; i < itemIds.size(); i++) {
                container.setItemStackForSlot((short) i, new ItemStack(itemIds.get(i), 1), false);
            }
            ics.setItemContainer(container);
        });
    }
}
