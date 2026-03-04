package dev.jackOtsig.map;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.SimpleItemContainer;
import com.hypixel.hytale.server.core.modules.entity.teleport.Teleport;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
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

    /** Pre-calculated data for a single chest placement. Y is resolved at placement time. */
    private record ChestSpec(int bx, int bz, String blockTypeKey, List<String> itemIds) {}

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
            World world = entityStore.getWorld();
            for (int i = 0; i < count; i++) {
                double angle = (2 * Math.PI * i) / count;
                double x = GameConstants.CENTER_X + GameConstants.SPAWN_RING_RADIUS * Math.sin(angle);
                double z = GameConstants.CENTER_Z + GameConstants.SPAWN_RING_RADIUS * Math.cos(angle);
                float yaw = (float) Math.toDegrees(Math.atan2(Math.sin(angle), -Math.cos(angle)));
                double y = findSurfaceY(world, (int) Math.floor(x), (int) Math.floor(z));
                PlayerData pd = list.get(i);
                pd.setFrozenPos(new Vector3d(x, y, z), yaw);
                teleportPlayer(pd.getPlayer(), x, y, z, yaw, store);
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
        Ref<EntityStore> ref = player.getReference();
        store.putComponent(ref, Teleport.getComponentType(),
                new Teleport(new Vector3d(x, y, z), new Vector3f(0, yaw, 0)));
    }

    /**
     * Returns the Y coordinate a player should stand at for the given world-space (bx, bz).
     * Uses the chunk heightmap (+1 so the player is above the top solid block).
     * Falls back to CENTER_Y if the chunk is not loaded.
     */
    public static double findSurfaceY(World world, int bx, int bz) {
        WorldChunk chunk = world.getChunk(ChunkUtil.indexChunkFromBlock(bx, bz));
        return chunk != null ? chunk.getHeight(bx, bz) + 1.0 : GameConstants.CENTER_Y;
    }

    /** Places one chest block and fills it with loot. Must be called on the world thread. */
    private void placeChest(ChestSpec spec, World world) {
        int by = (int) findSurfaceY(world, spec.bx(), spec.bz());
        world.setBlock(spec.bx(), by, spec.bz(), spec.blockTypeKey(), 0);

        if (!(world.getChunk(ChunkUtil.indexChunkFromBlock(spec.bx(), spec.bz()))
                     .getState(spec.bx(), by, spec.bz()) instanceof ItemContainerState ics)) {
            HungerGames.LOGGER.atWarning().log(
                    "spawnChest: no ItemContainerState at "
                    + spec.bx() + "," + by + "," + spec.bz()
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
