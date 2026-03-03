package dev.jackOtsig.map;

import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.SimpleItemContainer;
import com.hypixel.hytale.server.core.modules.entity.teleport.PendingTeleport;
import com.hypixel.hytale.server.core.modules.entity.teleport.Teleport;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.meta.state.ItemContainerState;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.jackOtsig.GameConstants;
import dev.jackOtsig.HungerGames;
import dev.jackOtsig.PlayerData;
import dev.jackOtsig.loot.ItemTier;
import dev.jackOtsig.loot.LootTable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

/**
 * Handles terrain generation, spawn-ring placement, and chest spawning.
 */
public class MapManager {

    /** Block type key for field chests (small crude chest). */
    private static final String CHEST_FIELD      = "Furniture_Crude_Chest_Small";
    /** Block type key for cornucopia chests (large epic chest). */
    private static final String CHEST_CORNUCOPIA = "Furniture_Dungeon_Chest_Epic";

    private static final Random RNG = new Random();

    private final LootTable lootTable = new LootTable();

    /** Pre-calculated data for a single chest placement. */
    private record ChestSpec(int bx, int by, int bz, String blockTypeKey, List<String> itemIds) {}

    /**
     * No-op — the map is hand-built in the world editor.
     * Use /setcenter while standing at the cornucopia to set the origin before each game.
     */
    public void generateMap() {}

    /**
     * Teleports each player to an evenly-spaced position on the spawn ring,
     * facing the cornucopia at the center. Runs on the world thread.
     */
    public void placeSpawnPositions(Collection<PlayerData> playerDatas, EntityStore entityStore) {
        if (entityStore == null) return;
        List<PlayerData> list = List.copyOf(playerDatas);
        int count = list.size();
        if (count == 0) return;
        entityStore.getWorld().execute(() -> {
            Store<EntityStore> store = entityStore.getStore();
            for (int i = 0; i < count; i++) {
                double angle = (2 * Math.PI * i) / count;
                double x = GameConstants.CENTER_X + GameConstants.SPAWN_RING_RADIUS * Math.sin(angle);
                double z = GameConstants.CENTER_Z + GameConstants.SPAWN_RING_RADIUS * Math.cos(angle);
                float yaw = (float) Math.toDegrees(Math.atan2(Math.sin(angle), -Math.cos(angle)));
                teleportPlayer(list.get(i).getPlayer(), x, GameConstants.CENTER_Y, z, yaw, store);
            }
        });
    }

    /**
     * Spawns CORNUCOPIA_CHEST_COUNT chests in a tight cluster at the center.
     * All placements run in a single world.execute() call to avoid flooding the world thread.
     */
    public void spawnCornucopiaChests(EntityStore entityStore) {
        if (entityStore == null) return;
        List<ChestSpec> specs = new ArrayList<>(GameConstants.CORNUCOPIA_CHEST_COUNT);
        for (int i = 0; i < GameConstants.CORNUCOPIA_CHEST_COUNT; i++) {
            double ox = (RNG.nextDouble() * 6) - 3;
            double oz = (RNG.nextDouble() * 6) - 3;
            specs.add(new ChestSpec(
                    (int) Math.floor(GameConstants.CENTER_X + ox),
                    (int) Math.floor(GameConstants.CENTER_Y),
                    (int) Math.floor(GameConstants.CENTER_Z + oz),
                    CHEST_CORNUCOPIA,
                    lootTable.rollChest(ItemTier.CORNUCOPIA)));
        }
        entityStore.getWorld().execute(() -> {
            for (ChestSpec spec : specs) placeChest(spec, entityStore.getWorld());
        });
    }

    /**
     * Spawns FIELD_CHEST_COUNT chests randomly across the map.
     * All placements run in a single world.execute() call to avoid flooding the world thread.
     */
    public void spawnFieldChests(EntityStore entityStore) {
        if (entityStore == null) return;
        double r = GameConstants.INITIAL_BORDER_RADIUS;
        List<ChestSpec> specs = new ArrayList<>(GameConstants.FIELD_CHEST_COUNT);
        for (int i = 0; i < GameConstants.FIELD_CHEST_COUNT; i++) {
            double angle = RNG.nextDouble() * 2 * Math.PI;
            double dist  = Math.sqrt(RNG.nextDouble()) * r;
            double x = GameConstants.CENTER_X + dist * Math.cos(angle);
            double z = GameConstants.CENTER_Z + dist * Math.sin(angle);
            specs.add(new ChestSpec(
                    (int) Math.floor(x),
                    (int) Math.floor(GameConstants.CENTER_Y),
                    (int) Math.floor(z),
                    CHEST_FIELD,
                    lootTable.rollChest(ItemTier.FIELD)));
        }
        entityStore.getWorld().execute(() -> {
            for (ChestSpec spec : specs) placeChest(spec, entityStore.getWorld());
        });
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private void teleportPlayer(Player player, double x, double y, double z, float yaw,
                                Store<EntityStore> store) {
        PendingTeleport pt = store.getComponent(
                player.getReference(),
                PendingTeleport.getComponentType());
        if (pt == null) {
            HungerGames.LOGGER.atWarning().log(
                    "teleportPlayer: PendingTeleport is null for " + player.getDisplayName()
                    + " — teleport skipped");
            return;
        }
        pt.queueTeleport(new Teleport(
                new Vector3d(x, y, z),
                new Vector3f(0, yaw, 0)));
    }

    /** Places one chest block and fills it with loot. Must be called on the world thread. */
    private void placeChest(ChestSpec spec, World world) {
        world.setBlock(spec.bx(), spec.by(), spec.bz(), spec.blockTypeKey(), 0);

        if (!(world.getChunk(ChunkUtil.indexChunkFromBlock(spec.bx(), spec.bz()))
                     .getState(spec.bx(), spec.by(), spec.bz()) instanceof ItemContainerState ics)) {
            HungerGames.LOGGER.atWarning().log(
                    "spawnChest: no ItemContainerState at "
                    + spec.bx() + "," + spec.by() + "," + spec.bz()
                    + " for block " + spec.blockTypeKey());
            return;
        }

        List<String> itemIds = spec.itemIds();
        SimpleItemContainer container = new SimpleItemContainer((short) itemIds.size());
        for (int i = 0; i < itemIds.size(); i++) {
            container.setItemStackForSlot((short) i, new ItemStack(itemIds.get(i), 1), false);
        }
        ics.setItemContainer(container);
    }
}
