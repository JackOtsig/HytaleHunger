package dev.jackOtsig.loot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Central registry of every Hytale item that can appear in chests.
 *
 * NOTE: Item ID strings are placeholders — replace with actual Hytale item
 * identifiers once the item API is known.
 */
public final class ItemRegistry {

    private static final List<LootItem> ALL_ITEMS = new ArrayList<>();
    private static final Map<String, LootItem> BY_ID = new HashMap<>();

    static {
        // TODO: Replace item ID strings with actual Hytale item identifiers — Hytale item API unknown

        // ── FIELD weapons ──────────────────────────────────────────────────────
        register("stone_sword",       ItemCategory.WEAPON,     ItemTier.FIELD,        10);
        register("wooden_bow",        ItemCategory.WEAPON,     ItemTier.FIELD,         8);
        register("stone_axe",         ItemCategory.WEAPON,     ItemTier.FIELD,         6);

        // ── CORNUCOPIA weapons ─────────────────────────────────────────────────
        register("iron_sword",        ItemCategory.WEAPON,     ItemTier.CORNUCOPIA,   10);
        register("steel_bow",         ItemCategory.WEAPON,     ItemTier.CORNUCOPIA,    8);
        register("iron_spear",        ItemCategory.WEAPON,     ItemTier.CORNUCOPIA,    6);
        register("golden_sword",      ItemCategory.WEAPON,     ItemTier.CORNUCOPIA,    4);

        // ── FIELD armor ────────────────────────────────────────────────────────
        register("leather_helmet",    ItemCategory.ARMOR,      ItemTier.FIELD,         8);
        register("leather_chestplate",ItemCategory.ARMOR,      ItemTier.FIELD,         8);
        register("leather_leggings",  ItemCategory.ARMOR,      ItemTier.FIELD,         8);

        // ── CORNUCOPIA armor ───────────────────────────────────────────────────
        register("iron_helmet",       ItemCategory.ARMOR,      ItemTier.CORNUCOPIA,    8);
        register("iron_chestplate",   ItemCategory.ARMOR,      ItemTier.CORNUCOPIA,    8);
        register("iron_leggings",     ItemCategory.ARMOR,      ItemTier.CORNUCOPIA,    8);
        register("iron_boots",        ItemCategory.ARMOR,      ItemTier.CORNUCOPIA,    8);

        // ── FIELD consumables ──────────────────────────────────────────────────
        register("bread",             ItemCategory.CONSUMABLE, ItemTier.FIELD,        10);
        register("bandage",           ItemCategory.CONSUMABLE, ItemTier.FIELD,         8);

        // ── CORNUCOPIA consumables ─────────────────────────────────────────────
        register("health_potion",     ItemCategory.CONSUMABLE, ItemTier.CORNUCOPIA,   10);
        register("speed_potion",      ItemCategory.CONSUMABLE, ItemTier.CORNUCOPIA,    6);
        register("strength_elixir",   ItemCategory.CONSUMABLE, ItemTier.CORNUCOPIA,    4);
    }

    private ItemRegistry() {}

    private static void register(String id, ItemCategory category, ItemTier tier, int weight) {
        LootItem item = new LootItem(id, category, tier, weight);
        ALL_ITEMS.add(item);
        BY_ID.put(id, item);
    }

    /** Returns all items matching the given tier and category. */
    public static List<LootItem> getItems(ItemTier tier, ItemCategory category) {
        return ALL_ITEMS.stream()
                .filter(i -> i.getTier() == tier && i.getCategory() == category)
                .collect(Collectors.toList());
    }

    /** Live weight tuning — adjust drop rates without a restart. */
    public static void setWeight(String itemId, int weight) {
        LootItem item = BY_ID.get(itemId);
        if (item != null) {
            item.setWeight(weight);
        }
    }
}
