package dev.jackOtsig.commands;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.jackOtsig.GameManager;
import dev.jackOtsig.HungerGames;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * /scanstructures [radius] — spirals outward chunk by chunk from the world
 * origin, force-generating each chunk and scanning for blocks that are
 * distinctive to the three hand-crafted start structures:
 *
 *   Start_Den  — Deco_Bone_Skulls
 *   Start_Camp — Furniture_Crude_Bed
 *   Start_Mine — Furniture_Crude_Ladder
 *
 * Reports every structure found and its approximate world position.
 * Default radius: 60 chunks (~960 tiles). Max allowed: 200 chunks.
 */
public class ScanStructuresCommand extends AbstractCommand {

    /** Blocks that uniquely identify each start structure type. */
    private static final Map<String, String> SIGNATURE_BLOCKS = new LinkedHashMap<>();
    static {
        SIGNATURE_BLOCKS.put("Deco_Bone_Skulls",      "Start_Den");
        SIGNATURE_BLOCKS.put("Furniture_Crude_Bed",   "Start_Camp");
        SIGNATURE_BLOCKS.put("Furniture_Crude_Ladder","Start_Mine");
    }
    private static final Set<String> TARGET_SET = SIGNATURE_BLOCKS.keySet();

    private static final int DEFAULT_RADIUS = 60;
    private static final int MAX_RADIUS     = 200;
    /** How often to log progress (every N chunks). */
    private static final int PROGRESS_INTERVAL = 200;

    private final GameManager gameManager;

    public ScanStructuresCommand(GameManager gameManager) {
        super("scanstructures", "Scan chunks for start structures — /scanstructures [radius]");
        this.gameManager = gameManager;
    }

    @Override
    protected CompletableFuture<Void> execute(@Nonnull CommandContext context) {
        EntityStore es = gameManager.getEntityStore();
        if (es == null) {
            context.sendMessage(Message.raw("§cWorld not ready yet."));
            return CompletableFuture.completedFuture(null);
        }

        int radius = DEFAULT_RADIUS;
        // getInputString() returns the full input e.g. "scanstructures 80"
        String[] parts = context.getInputString().trim().split("\\s+");
        if (parts.length >= 2) {
            try {
                radius = Math.min(MAX_RADIUS, Math.abs(Integer.parseInt(parts[1])));
            } catch (NumberFormatException ignored) {
                context.sendMessage(Message.raw("§cInvalid radius — using default " + DEFAULT_RADIUS + "."));
            }
        }

        List<int[]> chunks = buildSpiral(radius);
        context.sendMessage(Message.raw(
                "§eScanning §f" + chunks.size() + "§e chunks (radius §f" + radius + "§e) for start structures..."));

        World world = es.getWorld();
        scanNext(world, context, chunks, 0, new ArrayList<>(), new AtomicInteger(0), new AtomicBoolean(false));
        return CompletableFuture.completedFuture(null);
    }

    // ── Async sequential scan ─────────────────────────────────────────────────

    private void scanNext(World world, CommandContext context,
                          List<int[]> chunks, int index,
                          List<String> found, AtomicInteger scanned, AtomicBoolean done) {
        if (done.get()) return;
        if (index >= chunks.size()) {
            context.sendMessage(Message.raw(
                    "§aScan complete — §f" + found.size() + "§a structure(s) found:"));
            for (String loc : found) context.sendMessage(Message.raw("  §f" + loc));
            if (found.isEmpty()) context.sendMessage(Message.raw("  §7(none in scanned area)"));
            return;
        }

        int[] c = chunks.get(index);
        world.getNonTickingChunkAsync(c[0], c[1]).thenAccept(chunk -> {
            int n = scanned.incrementAndGet();
            if (chunk != null) {
                String hit = checkChunk(chunk, c[0], c[1]);
                if (hit != null && !found.contains(hit)) {
                    found.add(hit);
                    context.sendMessage(Message.raw("§a  Found: §f" + hit));
                    HungerGames.LOGGER.atInfo().log("[ScanStructures] " + hit);
                }
            }
            if (n % PROGRESS_INTERVAL == 0) {
                context.sendMessage(Message.raw(
                        "§7  ... §f" + n + "§7/§f" + chunks.size() + "§7 chunks scanned"));
            }
            scanNext(world, context, chunks, index + 1, found, scanned, done);
        }).exceptionally(ex -> {
            HungerGames.LOGGER.atWarning().log("[ScanStructures] chunk " + c[0] + "," + c[1] + " failed: " + ex);
            scanNext(world, context, chunks, index + 1, found, scanned, done);
            return null;
        });
    }

    // ── Block scan ────────────────────────────────────────────────────────────

    /**
     * Scans every block column in the chunk for a signature block.
     * Returns a description string if found, null otherwise.
     * Uses world (absolute) block coordinates as required by WorldChunk API.
     */
    private static String checkChunk(WorldChunk chunk, int cx, int cz) {
        int baseX = cx * 16;
        int baseZ = cz * 16;
        for (int lx = 0; lx < 16; lx++) {
            for (int lz = 0; lz < 16; lz++) {
                int bx = baseX + lx;
                int bz = baseZ + lz;
                int height = chunk.getHeight(bx, bz);
                for (int y = 0; y <= height; y++) {
                    BlockType bt = chunk.getBlockType(bx, y, bz);
                    if (bt == null) continue;
                    String id = bt.getId();
                    if (id == null) continue;
                    String structureName = SIGNATURE_BLOCKS.get(id);
                    if (structureName != null) {
                        return structureName + " @ (" + bx + ", " + y + ", " + bz + ")"
                                + "  [block: " + id + "]";
                    }
                }
            }
        }
        return null;
    }

    // ── Spiral coordinate generator ───────────────────────────────────────────

    /**
     * Generates chunk coordinates in a square spiral outward from (0, 0),
     * so we scan the area closest to the world origin first.
     */
    private static List<int[]> buildSpiral(int radius) {
        int diameter = radius * 2 + 1;
        List<int[]> list = new ArrayList<>(diameter * diameter);
        list.add(new int[]{0, 0});
        for (int r = 1; r <= radius; r++) {
            // top row:    z = -r, x: -r → +r
            for (int x = -r; x <= r; x++)  list.add(new int[]{x, -r});
            // right col:  x = +r, z: -r+1 → +r
            for (int z = -r + 1; z <= r; z++) list.add(new int[]{r, z});
            // bottom row: z = +r, x: +r-1 → -r
            for (int x = r - 1; x >= -r; x--) list.add(new int[]{x, r});
            // left col:   x = -r, z: +r-1 → -r+1
            for (int z = r - 1; z >= -r + 1; z--) list.add(new int[]{-r, z});
        }
        return list;
    }
}
