package dev.jackOtsig.loot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Central registry of every item that can appear in chests.
 *
 * Item ID strings match the JSON filenames under Server/Item/Items/ in Assets.zip
 * (filename without the .json extension).
 *
 * FIELD       — scattered field chests; crude/cloth/leather gear, basic food.
 * CORNUCOPIA  — centre cluster; copper/bronze/iron/steel gear, potions, better food.
 *
 * Material tier order: Crude/Wood → Copper/Bronze → Iron → Steel → Thorium → …
 */
public final class ItemRegistry {

    private static final List<LootItem> ALL_ITEMS = new ArrayList<>();
    // Keyed by "itemId:tierName" to avoid collision when the same item ID
    // appears in multiple tiers (e.g. Bandage_Crude).
    private static final Map<String, LootItem> BY_KEY = new HashMap<>();

    static {

        // ── FIELD weapons ──────────────────────────────────────────────────────
        register("Weapon_Sword_Crude",       ItemCategory.WEAPON,     ItemTier.FIELD,       10);
        register("Weapon_Axe_Crude",         ItemCategory.WEAPON,     ItemTier.FIELD,        9);
        register("Weapon_Spear_Crude",       ItemCategory.WEAPON,     ItemTier.FIELD,        9);
        register("Weapon_Daggers_Crude",     ItemCategory.WEAPON,     ItemTier.FIELD,        8);
        register("Weapon_Club_Crude",        ItemCategory.WEAPON,     ItemTier.FIELD,        8);
        register("Weapon_Mace_Crude",        ItemCategory.WEAPON,     ItemTier.FIELD,        7);
        register("Weapon_Battleaxe_Crude",   ItemCategory.WEAPON,     ItemTier.FIELD,        6);
        register("Weapon_Longsword_Crude",   ItemCategory.WEAPON,     ItemTier.FIELD,        6);
        register("Weapon_Shortbow_Crude",    ItemCategory.WEAPON,     ItemTier.FIELD,        7);
        register("Weapon_Staff_Wood",        ItemCategory.WEAPON,     ItemTier.FIELD,        5);
        register("Weapon_Sword_Wood",        ItemCategory.WEAPON,     ItemTier.FIELD,        5);
        register("Weapon_Arrow_Crude",       ItemCategory.WEAPON,     ItemTier.FIELD,       10);
        register("Weapon_Shield_Wood",       ItemCategory.WEAPON,     ItemTier.FIELD,        5);
        register("Weapon_Shield_Rusty",      ItemCategory.WEAPON,     ItemTier.FIELD,        4);
        register("Weapon_Shield_Scrap",      ItemCategory.WEAPON,     ItemTier.FIELD,        4);

        // ── CORNUCOPIA weapons ─────────────────────────────────────────────────
        // Copper tier
        register("Weapon_Sword_Copper",      ItemCategory.WEAPON,     ItemTier.CORNUCOPIA,   9);
        register("Weapon_Longsword_Copper",  ItemCategory.WEAPON,     ItemTier.CORNUCOPIA,   7);
        register("Weapon_Axe_Copper",        ItemCategory.WEAPON,     ItemTier.CORNUCOPIA,   7);
        register("Weapon_Spear_Copper",      ItemCategory.WEAPON,     ItemTier.CORNUCOPIA,   7);
        register("Weapon_Daggers_Copper",    ItemCategory.WEAPON,     ItemTier.CORNUCOPIA,   7);
        register("Weapon_Club_Copper",       ItemCategory.WEAPON,     ItemTier.CORNUCOPIA,   6);
        register("Weapon_Mace_Copper",       ItemCategory.WEAPON,     ItemTier.CORNUCOPIA,   6);
        register("Weapon_Shortbow_Copper",   ItemCategory.WEAPON,     ItemTier.CORNUCOPIA,   6);
        register("Weapon_Shield_Copper",     ItemCategory.WEAPON,     ItemTier.CORNUCOPIA,   6);
        // Bronze tier
        register("Weapon_Sword_Bronze",      ItemCategory.WEAPON,     ItemTier.CORNUCOPIA,   8);
        register("Weapon_Spear_Bronze",      ItemCategory.WEAPON,     ItemTier.CORNUCOPIA,   6);
        register("Weapon_Daggers_Bronze",    ItemCategory.WEAPON,     ItemTier.CORNUCOPIA,   6);
        register("Weapon_Shortbow_Bronze",   ItemCategory.WEAPON,     ItemTier.CORNUCOPIA,   5);
        // Iron tier
        register("Weapon_Sword_Iron",        ItemCategory.WEAPON,     ItemTier.CORNUCOPIA,  10);
        register("Weapon_Longsword_Iron",    ItemCategory.WEAPON,     ItemTier.CORNUCOPIA,   8);
        register("Weapon_Spear_Iron",        ItemCategory.WEAPON,     ItemTier.CORNUCOPIA,   8);
        register("Weapon_Daggers_Iron",      ItemCategory.WEAPON,     ItemTier.CORNUCOPIA,   8);
        register("Weapon_Axe_Iron",          ItemCategory.WEAPON,     ItemTier.CORNUCOPIA,   7);
        register("Weapon_Battleaxe_Iron",    ItemCategory.WEAPON,     ItemTier.CORNUCOPIA,   5);
        register("Weapon_Mace_Iron",         ItemCategory.WEAPON,     ItemTier.CORNUCOPIA,   6);
        register("Weapon_Shortbow_Iron",     ItemCategory.WEAPON,     ItemTier.CORNUCOPIA,   7);
        register("Weapon_Crossbow_Iron",     ItemCategory.WEAPON,     ItemTier.CORNUCOPIA,   5);
        register("Weapon_Shield_Iron",       ItemCategory.WEAPON,     ItemTier.CORNUCOPIA,   7);
        register("Weapon_Arrow_Iron",        ItemCategory.WEAPON,     ItemTier.CORNUCOPIA,  10);
        register("Weapon_Arrow_Clearshot",   ItemCategory.WEAPON,     ItemTier.CORNUCOPIA,   7);
        register("Weapon_Arrow_Deadeye",     ItemCategory.WEAPON,     ItemTier.CORNUCOPIA,   4);
        // Steel / Thorium (rare)
        register("Weapon_Sword_Steel",       ItemCategory.WEAPON,     ItemTier.CORNUCOPIA,   4);
        register("Weapon_Arrow_Trueshot",    ItemCategory.WEAPON,     ItemTier.CORNUCOPIA,   2);
        register("Weapon_Spear_Thorium",     ItemCategory.WEAPON,     ItemTier.CORNUCOPIA,   2);
        register("Weapon_Daggers_Thorium",   ItemCategory.WEAPON,     ItemTier.CORNUCOPIA,   2);
        register("Weapon_Shortbow_Thorium",  ItemCategory.WEAPON,     ItemTier.CORNUCOPIA,   2);
        register("Weapon_Crossbow_Ancient_Steel", ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  2);

        // ── FIELD armor ────────────────────────────────────────────────────────
        // Cloth (worst protection, lightest)
        register("Armor_Cloth_Cotton_Head",  ItemCategory.ARMOR,      ItemTier.FIELD,        8);
        register("Armor_Cloth_Cotton_Chest", ItemCategory.ARMOR,      ItemTier.FIELD,        8);
        register("Armor_Cloth_Cotton_Hands", ItemCategory.ARMOR,      ItemTier.FIELD,        8);
        register("Armor_Cloth_Cotton_Legs",  ItemCategory.ARMOR,      ItemTier.FIELD,        8);
        register("Armor_Cloth_Wool_Head",    ItemCategory.ARMOR,      ItemTier.FIELD,        8);
        register("Armor_Cloth_Wool_Chest",   ItemCategory.ARMOR,      ItemTier.FIELD,        8);
        register("Armor_Cloth_Wool_Hands",   ItemCategory.ARMOR,      ItemTier.FIELD,        8);
        register("Armor_Cloth_Wool_Legs",    ItemCategory.ARMOR,      ItemTier.FIELD,        8);
        register("Armor_Cloth_Linen_Head",   ItemCategory.ARMOR,      ItemTier.FIELD,        6);
        register("Armor_Cloth_Linen_Chest",  ItemCategory.ARMOR,      ItemTier.FIELD,        6);
        register("Armor_Cloth_Linen_Hands",  ItemCategory.ARMOR,      ItemTier.FIELD,        6);
        register("Armor_Cloth_Linen_Legs",   ItemCategory.ARMOR,      ItemTier.FIELD,        6);
        // Leather (light and soft)
        register("Armor_Leather_Soft_Head",  ItemCategory.ARMOR,      ItemTier.FIELD,        7);
        register("Armor_Leather_Soft_Chest", ItemCategory.ARMOR,      ItemTier.FIELD,        7);
        register("Armor_Leather_Soft_Hands", ItemCategory.ARMOR,      ItemTier.FIELD,        7);
        register("Armor_Leather_Soft_Legs",  ItemCategory.ARMOR,      ItemTier.FIELD,        7);
        register("Armor_Leather_Light_Head", ItemCategory.ARMOR,      ItemTier.FIELD,        5);
        register("Armor_Leather_Light_Chest",ItemCategory.ARMOR,      ItemTier.FIELD,        5);
        register("Armor_Leather_Light_Hands",ItemCategory.ARMOR,      ItemTier.FIELD,        5);
        register("Armor_Leather_Light_Legs", ItemCategory.ARMOR,      ItemTier.FIELD,        5);
        register("Armor_Leather_Medium_Head",ItemCategory.ARMOR,      ItemTier.FIELD,        4);
        register("Armor_Leather_Medium_Chest",ItemCategory.ARMOR,     ItemTier.FIELD,        4);
        register("Armor_Leather_Medium_Hands",ItemCategory.ARMOR,     ItemTier.FIELD,        4);
        register("Armor_Leather_Medium_Legs",ItemCategory.ARMOR,      ItemTier.FIELD,        4);
        // Wood (crude solid protection)
        register("Armor_Wood_Head",          ItemCategory.ARMOR,      ItemTier.FIELD,        4);
        register("Armor_Wood_Chest",         ItemCategory.ARMOR,      ItemTier.FIELD,        4);
        register("Armor_Wood_Hands",         ItemCategory.ARMOR,      ItemTier.FIELD,        4);
        register("Armor_Wood_Legs",          ItemCategory.ARMOR,      ItemTier.FIELD,        4);

        // ── CORNUCOPIA armor ───────────────────────────────────────────────────
        // Heavy leather
        register("Armor_Leather_Heavy_Head", ItemCategory.ARMOR,      ItemTier.CORNUCOPIA,   6);
        register("Armor_Leather_Heavy_Chest",ItemCategory.ARMOR,      ItemTier.CORNUCOPIA,   6);
        register("Armor_Leather_Heavy_Hands",ItemCategory.ARMOR,      ItemTier.CORNUCOPIA,   6);
        register("Armor_Leather_Heavy_Legs", ItemCategory.ARMOR,      ItemTier.CORNUCOPIA,   6);
        // Copper
        register("Armor_Copper_Head",        ItemCategory.ARMOR,      ItemTier.CORNUCOPIA,   7);
        register("Armor_Copper_Chest",       ItemCategory.ARMOR,      ItemTier.CORNUCOPIA,   7);
        register("Armor_Copper_Hands",       ItemCategory.ARMOR,      ItemTier.CORNUCOPIA,   7);
        register("Armor_Copper_Legs",        ItemCategory.ARMOR,      ItemTier.CORNUCOPIA,   7);
        // Bronze
        register("Armor_Bronze_Head",        ItemCategory.ARMOR,      ItemTier.CORNUCOPIA,   7);
        register("Armor_Bronze_Chest",       ItemCategory.ARMOR,      ItemTier.CORNUCOPIA,   7);
        register("Armor_Bronze_Hands",       ItemCategory.ARMOR,      ItemTier.CORNUCOPIA,   7);
        register("Armor_Bronze_Legs",        ItemCategory.ARMOR,      ItemTier.CORNUCOPIA,   7);
        // Iron
        register("Armor_Iron_Head",          ItemCategory.ARMOR,      ItemTier.CORNUCOPIA,   8);
        register("Armor_Iron_Chest",         ItemCategory.ARMOR,      ItemTier.CORNUCOPIA,   8);
        register("Armor_Iron_Hands",         ItemCategory.ARMOR,      ItemTier.CORNUCOPIA,   8);
        register("Armor_Iron_Legs",          ItemCategory.ARMOR,      ItemTier.CORNUCOPIA,   8);
        // Steel
        register("Armor_Steel_Head",         ItemCategory.ARMOR,      ItemTier.CORNUCOPIA,   5);
        register("Armor_Steel_Chest",        ItemCategory.ARMOR,      ItemTier.CORNUCOPIA,   5);
        register("Armor_Steel_Hands",        ItemCategory.ARMOR,      ItemTier.CORNUCOPIA,   5);
        register("Armor_Steel_Legs",         ItemCategory.ARMOR,      ItemTier.CORNUCOPIA,   5);
        // Thorium (rare)
        register("Armor_Thorium_Head",       ItemCategory.ARMOR,      ItemTier.CORNUCOPIA,   2);
        register("Armor_Thorium_Chest",      ItemCategory.ARMOR,      ItemTier.CORNUCOPIA,   2);
        register("Armor_Thorium_Hands",      ItemCategory.ARMOR,      ItemTier.CORNUCOPIA,   2);
        register("Armor_Thorium_Legs",       ItemCategory.ARMOR,      ItemTier.CORNUCOPIA,   2);

        // ── FIELD consumables ──────────────────────────────────────────────────
        register("Food_Bread",               ItemCategory.CONSUMABLE, ItemTier.FIELD,       10);
        register("Food_Beef_Raw",            ItemCategory.CONSUMABLE, ItemTier.FIELD,        8);
        register("Food_Chicken_Raw",         ItemCategory.CONSUMABLE, ItemTier.FIELD,        8);
        register("Food_Pork_Raw",            ItemCategory.CONSUMABLE, ItemTier.FIELD,        7);
        register("Food_Fish_Raw",            ItemCategory.CONSUMABLE, ItemTier.FIELD,        7);
        register("Food_Wildmeat_Raw",        ItemCategory.CONSUMABLE, ItemTier.FIELD,        7);
        register("Food_Egg",                 ItemCategory.CONSUMABLE, ItemTier.FIELD,        5);
        register("Food_Cheese",              ItemCategory.CONSUMABLE, ItemTier.FIELD,        6);
        register("Food_Fish_Grilled",        ItemCategory.CONSUMABLE, ItemTier.FIELD,        6);
        register("Food_Wildmeat_Cooked",     ItemCategory.CONSUMABLE, ItemTier.FIELD,        6);
        register("Food_Vegetable_Cooked",    ItemCategory.CONSUMABLE, ItemTier.FIELD,        5);
        register("Food_Kebab_Mushroom",      ItemCategory.CONSUMABLE, ItemTier.FIELD,        6);
        register("Bandage_Crude",            ItemCategory.CONSUMABLE, ItemTier.FIELD,        9);
        register("Potion_Health_Lesser",     ItemCategory.CONSUMABLE, ItemTier.FIELD,        8);
        register("Potion_Health_Small",      ItemCategory.CONSUMABLE, ItemTier.FIELD,        6);
        register("Potion_Regen_Health_Small",ItemCategory.CONSUMABLE, ItemTier.FIELD,        7);

        // ── CORNUCOPIA consumables ─────────────────────────────────────────────
        register("Potion_Health",            ItemCategory.CONSUMABLE, ItemTier.CORNUCOPIA,  10);
        register("Potion_Health_Large",      ItemCategory.CONSUMABLE, ItemTier.CORNUCOPIA,   6);
        register("Potion_Health_Greater",    ItemCategory.CONSUMABLE, ItemTier.CORNUCOPIA,   4);
        register("Potion_Regen_Health",      ItemCategory.CONSUMABLE, ItemTier.CORNUCOPIA,   7);
        register("Potion_Regen_Health_Large",ItemCategory.CONSUMABLE, ItemTier.CORNUCOPIA,   3);
        register("Potion_Stamina",           ItemCategory.CONSUMABLE, ItemTier.CORNUCOPIA,   6);
        register("Potion_Stamina_Large",     ItemCategory.CONSUMABLE, ItemTier.CORNUCOPIA,   5);
        register("Potion_Regen_Stamina",     ItemCategory.CONSUMABLE, ItemTier.CORNUCOPIA,   5);
        register("Potion_Antidote",          ItemCategory.CONSUMABLE, ItemTier.CORNUCOPIA,   5);
        register("Potion_Purify",            ItemCategory.CONSUMABLE, ItemTier.CORNUCOPIA,   4);
        register("Food_Kebab_Meat",          ItemCategory.CONSUMABLE, ItemTier.CORNUCOPIA,   8);
        register("Food_Kebab_Fruit",         ItemCategory.CONSUMABLE, ItemTier.CORNUCOPIA,   6);
        register("Food_Pie_Meat",            ItemCategory.CONSUMABLE, ItemTier.CORNUCOPIA,   6);
        register("Food_Pie_Apple",           ItemCategory.CONSUMABLE, ItemTier.CORNUCOPIA,   7);
        register("Food_Pie_Pumpkin",         ItemCategory.CONSUMABLE, ItemTier.CORNUCOPIA,   6);
        register("Food_Salad_Caesar",        ItemCategory.CONSUMABLE, ItemTier.CORNUCOPIA,   5);
        register("Food_Salad_Berry",         ItemCategory.CONSUMABLE, ItemTier.CORNUCOPIA,   5);
        register("Food_Salad_Mushroom",      ItemCategory.CONSUMABLE, ItemTier.CORNUCOPIA,   5);
    }

    private ItemRegistry() {}

    private static void register(String id, ItemCategory category, ItemTier tier, int weight) {
        LootItem item = new LootItem(id, category, tier, weight);
        ALL_ITEMS.add(item);
        BY_KEY.put(id + ":" + tier.name(), item);
    }

    /** Returns all items matching the given tier and category. */
    public static List<LootItem> getItems(ItemTier tier, ItemCategory category) {
        return ALL_ITEMS.stream()
                .filter(i -> i.getTier() == tier && i.getCategory() == category)
                .collect(Collectors.toList());
    }

    /** Live weight tuning — adjust drop rates without a restart. */
    public static void setWeight(String itemId, ItemTier tier, int weight) {
        LootItem item = BY_KEY.get(itemId + ":" + tier.name());
        if (item != null) {
            item.setWeight(weight);
        }
    }
}
