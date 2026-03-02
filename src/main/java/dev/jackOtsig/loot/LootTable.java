package dev.jackOtsig.loot;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Rolls loot for a single chest.
 *
 * Per category: geometric count roll → P(1)=50%, P(2)=25%, P(3)=12.5% …
 * (minimum 1, uncapped; guarantees ≥3 items per chest).
 * Item selection within the category uses a cumulative-weight walk.
 */
public class LootTable {

    private static final Random RNG = new Random();

    /**
     * Returns a list of item IDs for a chest of the given tier.
     * May return an empty list if no items roll for any category.
     */
    public List<String> rollChest(ItemTier tier) {
        List<String> result = new ArrayList<>();
        for (ItemCategory category : ItemCategory.values()) {
            int count = rollItemCount();
            List<LootItem> pool = ItemRegistry.getItems(tier, category);
            if (pool.isEmpty()) continue;
            for (int i = 0; i < count; i++) {
                result.add(weightedRandom(pool));
            }
        }
        return result;
    }

    /**
     * Geometric roll starting at 1: P(1)=50%, P(2)=25%, P(3)=12.5% …
     * Uncapped; guarantees at least 1 item per category.
     */
    private int rollItemCount() {
        int count = 1;
        while (RNG.nextBoolean()) {
            count++;
        }
        return count;
    }

    /** Selects one item by cumulative-weight walk. */
    private String weightedRandom(List<LootItem> pool) {
        int total = pool.stream().mapToInt(LootItem::getWeight).sum();
        int roll = RNG.nextInt(total);
        int cumulative = 0;
        for (LootItem item : pool) {
            cumulative += item.getWeight();
            if (roll < cumulative) {
                return item.getItemId();
            }
        }
        // Fallback — should never be reached if weights are positive
        return pool.get(pool.size() - 1).getItemId();
    }
}
