package dev.jackOtsig.commands;

import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.jackOtsig.GameManager;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * /whereami — scans blocks in a 30x30x30 box around the player and reports
 * which start structure type (if any) they are standing in, based on
 * exclusive block signatures extracted from the prefab JSON files.
 *
 * Signature blocks (exclusive to each type, verified from prefab data):
 *   Start_Camp — Furniture_Crude_Bed, Wood_Hardwood_Stairs, Rock_Stone_Mossy
 *   Start_Den  — Soil_Mud_Dry, Deco_Bone_Skulls, Plant_Grass_Sharp
 *   Start_Mine — Ore_Copper_Stone, Wood_Softwood_Planks, Furniture_Crude_Ladder
 */
public class WhereAmICommand extends AbstractCommand {

    /** Half-extents of the scan box around the player position. */
    private static final int HALF_X = 15;
    private static final int HALF_Y = 10;
    private static final int HALF_Z = 15;

    /**
     * Maps each block ID to the structure name it uniquely identifies.
     * A higher match count → stronger confidence.
     */
    private static final Map<String, String> SIGNATURES = new HashMap<>();
    static {
        // Start_Camp — camp with crude furniture and hardwood construction
        SIGNATURES.put("Furniture_Crude_Bed",    "Start_Camp");
        SIGNATURES.put("Wood_Hardwood_Stairs",   "Start_Camp");
        SIGNATURES.put("Rock_Stone_Mossy",       "Start_Camp");
        SIGNATURES.put("Furniture_Crude_Stool",  "Start_Camp");
        SIGNATURES.put("Wood_Hardwood_Fence",    "Start_Camp");

        // Start_Den — underground den with bones and mud
        SIGNATURES.put("Soil_Mud_Dry",           "Start_Den");
        SIGNATURES.put("Deco_Bone_Skulls",        "Start_Den");
        SIGNATURES.put("Plant_Grass_Sharp",       "Start_Den");
        SIGNATURES.put("Deco_Bone_Pile",          "Start_Den");
        SIGNATURES.put("Furniture_Dungeon_Earth_Brazier", "Start_Den");

        // Start_Mine — mineshaft with softwood scaffolding and copper ore
        SIGNATURES.put("Ore_Copper_Stone",        "Start_Mine");
        SIGNATURES.put("Wood_Softwood_Planks",    "Start_Mine");
        SIGNATURES.put("Furniture_Crude_Ladder",  "Start_Mine");
        SIGNATURES.put("Furniture_Crude_Platform","Start_Mine");
        SIGNATURES.put("Wood_Deadwood_Roof",      "Start_Mine");
    }

    private final GameManager gameManager;

    public WhereAmICommand(GameManager gameManager) {
        super("whereami", "Report which start structure you are standing in");
        this.gameManager = gameManager;
    }

    @Override
    protected CompletableFuture<Void> execute(@Nonnull CommandContext context) {
        if (!context.isPlayer()) {
            context.sendMessage(Message.raw("This command can only be used by players."));
            return CompletableFuture.completedFuture(null);
        }

        EntityStore es = gameManager.getEntityStore();
        if (es == null) {
            context.sendMessage(Message.raw("§cWorld not ready yet."));
            return CompletableFuture.completedFuture(null);
        }

        es.getWorld().execute(() -> {
            var pos = es.getStore()
                        .getComponent(context.senderAsPlayerRef(), TransformComponent.getComponentType())
                        .getPosition();

            int px = (int) Math.floor(pos.x);
            int py = (int) Math.floor(pos.y);
            int pz = (int) Math.floor(pos.z);

            context.sendMessage(Message.raw(String.format(
                    "§eScanning blocks around (§f%d, %d, %d§e)...", px, py, pz)));

            World world = es.getWorld();
            Map<String, Integer> scores = new HashMap<>();

            for (int bx = px - HALF_X; bx <= px + HALF_X; bx++) {
                for (int bz = pz - HALF_Z; bz <= pz + HALF_Z; bz++) {
                    long chunkIdx = ChunkUtil.indexChunkFromBlock(bx, bz);
                    WorldChunk chunk = world.getChunk(chunkIdx);
                    if (chunk == null) continue;

                    int minY = Math.max(0, py - HALF_Y);
                    int maxY = py + HALF_Y;
                    for (int by = minY; by <= maxY; by++) {
                        BlockType bt = chunk.getBlockType(bx, by, bz);
                        if (bt == null) continue;
                        String id = bt.getId();
                        if (id == null) continue;
                        String structure = SIGNATURES.get(id);
                        if (structure != null) {
                            scores.merge(structure, 1, Integer::sum);
                        }
                    }
                }
            }

            if (scores.isEmpty()) {
                context.sendMessage(Message.raw("§7No recognised start structure nearby."));
                return;
            }

            // Sort by score descending and report
            scores.entrySet().stream()
                  .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                  .forEach(e -> context.sendMessage(Message.raw(
                          "§a" + e.getKey() + " §7(§f" + e.getValue() + "§7 signature blocks)")));
        });

        return CompletableFuture.completedFuture(null);
    }
}
