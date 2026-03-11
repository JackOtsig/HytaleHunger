package dev.jackOtsig.map;

import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import dev.jackOtsig.HungerGames;

import java.util.HashMap;
import java.util.Map;

/**
 * Tracks every block change made during a game and restores the world to its
 * pre-game state at the end of each round.
 *
 * The "first touch wins" approach: only the ORIGINAL state at a position is
 * stored.  Subsequent changes to the same position are ignored so the original
 * is always what gets restored.
 *
 * All methods must be called on the world thread (ECS event callbacks already
 * satisfy this; restore() is called inside world.execute() from GameManager).
 */
public class WorldStateManager {

    /** What a block position looked like before any game touched it. */
    private record BlockRecord(int x, int y, int z, String typeId, int rotation) {}

    /** Packed (x,y,z) → original block record.  Key: x*31^2 + y*31 + z is fragile;
     *  use a simple String key for correctness. */
    private final Map<String, BlockRecord> originalState = new HashMap<>();

    // ── Recording ─────────────────────────────────────────────────────────────

    /** Records that (x,y,z) originally contained typeId/rotation, for explicit callers. */
    public void recordOriginalBlock(int x, int y, int z, String typeId, int rotation) {
        recordOriginal(x, y, z, typeId, rotation);
    }

    private static String key(int x, int y, int z) {
        return x + "," + y + "," + z;
    }

    /**
     * Records the original state of (x,y,z) the first time it is modified.
     * Subsequent calls for the same position are no-ops.
     */
    private void recordOriginal(int x, int y, int z, String typeId, int rotation) {
        originalState.putIfAbsent(key(x, y, z), new BlockRecord(x, y, z, typeId, rotation));
    }

    /**
     * Called from BlockBreakSystem when a player breaks a block during ACTIVE.
     * Reads the rotation from the chunk before it is removed.
     * Must be called on the world thread.
     */
    public void onBlockBroken(World world, int x, int y, int z, String typeId) {
        WorldChunk chunk = world.getChunk(ChunkUtil.indexChunkFromBlock(x, z));
        int rotation = (chunk != null && chunk.getState(x, y, z) != null)
                ? chunk.getState(x, y, z).getRotationIndex() : 0;
        recordOriginal(x, y, z, typeId, rotation);
    }

    /**
     * Called from BlockPlaceSystem when a player places a block during ACTIVE.
     * Records the position as air so it is cleared on restore.
     */
    public void onBlockPlaced(int x, int y, int z) {
        recordOriginal(x, y, z, BlockType.EMPTY_KEY, 0);
    }

    /**
     * Called from MapManager when a chest is spawned.
     * Records the position as air so the chest is removed on restore.
     */
    public void onChestSpawned(int x, int y, int z) {
        recordOriginal(x, y, z, BlockType.EMPTY_KEY, 0);
    }

    // ── Restoration ───────────────────────────────────────────────────────────

    /**
     * Restores all tracked positions to their original state, then clears the
     * tracking map.  Must be called on the world thread.
     */
    public void restore(World world) {
        int count = originalState.size();
        for (BlockRecord rec : originalState.values()) {
            world.setBlock(rec.x(), rec.y(), rec.z(), rec.typeId(), rec.rotation());
        }
        originalState.clear();
        HungerGames.LOGGER.atInfo().log("WorldStateManager: restored " + count + " block(s)");
    }
}
