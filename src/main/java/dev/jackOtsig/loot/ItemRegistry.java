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
 * CORNUCOPIA  — centre cluster; mid-to-high tier gear, magic, potions.
 *
 * Metal tier order (low → high):
 *   Crude/Wood → Scrap/Bone → Copper → Bronze → Iron → Steel → Cobalt
 *   → Thorium → Mithril → Onyxium → Adamantite
 */
public final class ItemRegistry {

    private static final List<LootItem> ALL_ITEMS = new ArrayList<>();
    // Keyed by "itemId:tierName" to avoid collision when the same item ID
    // appears in multiple tiers.
    private static final Map<String, LootItem> BY_KEY = new HashMap<>();

    static {

        // ══════════════════════════════════════════════════════════════════════
        // FIELD WEAPONS  —  crude / scrap / bone / wood tier
        // ══════════════════════════════════════════════════════════════════════

        // ── Swords ────────────────────────────────────────────────────────────
        register("Weapon_Sword_Crude",            ItemCategory.WEAPON, ItemTier.FIELD, 10);
        register("Weapon_Sword_Wood",             ItemCategory.WEAPON, ItemTier.FIELD,  5);
        register("Weapon_Sword_Scrap",            ItemCategory.WEAPON, ItemTier.FIELD,  4);
        register("Weapon_Sword_Bone",             ItemCategory.WEAPON, ItemTier.FIELD,  4);

        // ── Axes ──────────────────────────────────────────────────────────────
        register("Weapon_Axe_Crude",              ItemCategory.WEAPON, ItemTier.FIELD,  9);
        register("Weapon_Axe_Bone",               ItemCategory.WEAPON, ItemTier.FIELD,  5);

        // ── Spears ────────────────────────────────────────────────────────────
        register("Weapon_Spear_Crude",            ItemCategory.WEAPON, ItemTier.FIELD,  9);
        register("Weapon_Spear_Bone",             ItemCategory.WEAPON, ItemTier.FIELD,  5);
        register("Weapon_Spear_Scrap",            ItemCategory.WEAPON, ItemTier.FIELD,  4);

        // ── Daggers ───────────────────────────────────────────────────────────
        register("Weapon_Daggers_Crude",          ItemCategory.WEAPON, ItemTier.FIELD,  8);
        register("Weapon_Daggers_Bone",           ItemCategory.WEAPON, ItemTier.FIELD,  5);
        register("Weapon_Daggers_Claw_Bone",      ItemCategory.WEAPON, ItemTier.FIELD,  4);

        // ── Clubs ─────────────────────────────────────────────────────────────
        register("Weapon_Club_Crude",             ItemCategory.WEAPON, ItemTier.FIELD,  8);
        register("Weapon_Club_Scrap",             ItemCategory.WEAPON, ItemTier.FIELD,  5);

        // ── Maces ─────────────────────────────────────────────────────────────
        register("Weapon_Mace_Crude",             ItemCategory.WEAPON, ItemTier.FIELD,  7);
        register("Weapon_Mace_Scrap",             ItemCategory.WEAPON, ItemTier.FIELD,  5);

        // ── Battleaxes ────────────────────────────────────────────────────────
        register("Weapon_Battleaxe_Crude",        ItemCategory.WEAPON, ItemTier.FIELD,  6);
        register("Weapon_Battleaxe_Wood_Fence",   ItemCategory.WEAPON, ItemTier.FIELD,  4);

        // ── Longswords ────────────────────────────────────────────────────────
        register("Weapon_Longsword_Crude",        ItemCategory.WEAPON, ItemTier.FIELD,  6);

        // ── Shortbows ─────────────────────────────────────────────────────────
        register("Weapon_Shortbow_Crude",         ItemCategory.WEAPON, ItemTier.FIELD,  7);

        // ── Shields ───────────────────────────────────────────────────────────
        register("Weapon_Shield_Wood",            ItemCategory.WEAPON, ItemTier.FIELD,  5);
        register("Weapon_Shield_Rusty",           ItemCategory.WEAPON, ItemTier.FIELD,  4);
        register("Weapon_Shield_Scrap",           ItemCategory.WEAPON, ItemTier.FIELD,  4);
        register("Weapon_Shield_Scrap_Spiked",    ItemCategory.WEAPON, ItemTier.FIELD,  3);

        // ── Staffs ────────────────────────────────────────────────────────────
        register("Weapon_Staff_Wood",             ItemCategory.WEAPON, ItemTier.FIELD,  5);
        register("Weapon_Staff_Bo_Wood",          ItemCategory.WEAPON, ItemTier.FIELD,  4);
        register("Weapon_Staff_Bo_Bamboo",        ItemCategory.WEAPON, ItemTier.FIELD,  4);
        register("Weapon_Staff_Bone",             ItemCategory.WEAPON, ItemTier.FIELD,  4);
        register("Weapon_Staff_Cane",             ItemCategory.WEAPON, ItemTier.FIELD,  4);
        register("Weapon_Staff_Wood_Rotten",      ItemCategory.WEAPON, ItemTier.FIELD,  3);

        // ── Wands ─────────────────────────────────────────────────────────────
        register("Weapon_Wand_Wood",              ItemCategory.WEAPON, ItemTier.FIELD,  5);
        register("Weapon_Wand_Wood_Rotten",       ItemCategory.WEAPON, ItemTier.FIELD,  3);
        register("Weapon_Wand_Tribal",            ItemCategory.WEAPON, ItemTier.FIELD,  4);

        // ── Spellbooks ────────────────────────────────────────────────────────
        register("Weapon_Spellbook_Grimoire_Brown", ItemCategory.WEAPON, ItemTier.FIELD, 5);

        // ── Throwables / Primitive Ranged ─────────────────────────────────────
        register("Weapon_Blowgun_Tribal",         ItemCategory.WEAPON, ItemTier.FIELD,  6);
        register("Weapon_Dart_Tribal",            ItemCategory.WEAPON, ItemTier.FIELD,  6);
        register("Weapon_Claws_Tribal",           ItemCategory.WEAPON, ItemTier.FIELD,  5);

        // ── Bombs ─────────────────────────────────────────────────────────────
        register("Weapon_Bomb_Popberry",          ItemCategory.WEAPON, ItemTier.FIELD,  8);


        // ══════════════════════════════════════════════════════════════════════
        // CORNUCOPIA WEAPONS  —  copper through adamantite + magic + specials
        // ══════════════════════════════════════════════════════════════════════

        // ── Copper tier ───────────────────────────────────────────────────────
        register("Weapon_Sword_Copper",           ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  9);
        register("Weapon_Longsword_Copper",       ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  7);
        register("Weapon_Axe_Copper",             ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  7);
        register("Weapon_Spear_Copper",           ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  7);
        register("Weapon_Daggers_Copper",         ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  7);
        register("Weapon_Club_Copper",            ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  6);
        register("Weapon_Mace_Copper",            ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  6);
        register("Weapon_Battleaxe_Copper",       ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  6);
        register("Weapon_Shortbow_Copper",        ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  6);
        register("Weapon_Shield_Copper",          ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  6);
        register("Weapon_Staff_Copper",           ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  6);

        // ── Bronze tier ───────────────────────────────────────────────────────
        register("Weapon_Sword_Bronze",           ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  8);
        register("Weapon_Sword_Bronze_Ancient",   ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  4);
        register("Weapon_Spear_Bronze",           ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  6);
        register("Weapon_Daggers_Bronze",         ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  6);
        register("Weapon_Daggers_Bronze_Ancient", ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  3);
        register("Weapon_Shortbow_Bronze",        ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  5);
        register("Weapon_Staff_Bronze",           ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  5);

        // ── Iron tier ─────────────────────────────────────────────────────────
        register("Weapon_Sword_Iron",             ItemCategory.WEAPON, ItemTier.CORNUCOPIA, 10);
        register("Weapon_Longsword_Iron",         ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  8);
        register("Weapon_Spear_Iron",             ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  8);
        register("Weapon_Daggers_Iron",           ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  8);
        register("Weapon_Axe_Iron",               ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  7);
        register("Weapon_Axe_Iron_Rusty",         ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  4);
        register("Weapon_Mace_Iron",              ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  6);
        register("Weapon_Club_Iron",              ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  6);
        register("Weapon_Club_Iron_Rusty",        ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  3);
        register("Weapon_Battleaxe_Iron",         ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  5);
        register("Weapon_Shortbow_Iron",          ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  7);
        register("Weapon_Shortbow_Iron_Rusty",    ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  3);
        register("Weapon_Crossbow_Iron",          ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  5);
        register("Weapon_Shield_Iron",            ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  7);
        register("Weapon_Staff_Iron",             ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  7);

        // ── Steel tier ────────────────────────────────────────────────────────
        register("Weapon_Sword_Steel",            ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  4);
        register("Weapon_Sword_Steel_Rusty",      ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  3);
        register("Weapon_Battleaxe_Steel_Rusty",  ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  3);
        register("Weapon_Club_Steel_Flail_Rusty", ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  3);
        register("Weapon_Crossbow_Ancient_Steel", ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  2);

        // ── Thorium tier ──────────────────────────────────────────────────────
        register("Weapon_Sword_Thorium",          ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  2);
        register("Weapon_Axe_Thorium",            ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  2);
        register("Weapon_Spear_Thorium",          ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  2);
        register("Weapon_Daggers_Thorium",        ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  2);
        register("Weapon_Club_Thorium",           ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  2);
        register("Weapon_Mace_Thorium",           ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  2);
        register("Weapon_Battleaxe_Thorium",      ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  2);
        register("Weapon_Longsword_Thorium",      ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  2);
        register("Weapon_Shortbow_Thorium",       ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  2);
        register("Weapon_Shield_Thorium",         ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  2);
        register("Weapon_Staff_Thorium",          ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  2);

        // ── Cobalt tier ───────────────────────────────────────────────────────
        register("Weapon_Sword_Cobalt",           ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  3);
        register("Weapon_Axe_Cobalt",             ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  3);
        register("Weapon_Spear_Cobalt",           ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  3);
        register("Weapon_Daggers_Cobalt",         ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  3);
        register("Weapon_Club_Cobalt",            ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  3);
        register("Weapon_Mace_Cobalt",            ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  3);
        register("Weapon_Battleaxe_Cobalt",       ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  3);
        register("Weapon_Longsword_Cobalt",       ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  3);
        register("Weapon_Shortbow_Cobalt",        ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  3);
        register("Weapon_Shield_Cobalt",          ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  3);
        register("Weapon_Staff_Cobalt",           ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  3);

        // ── Mithril tier ──────────────────────────────────────────────────────
        register("Weapon_Sword_Mithril",          ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  2);
        register("Weapon_Axe_Mithril",            ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  2);
        register("Weapon_Spear_Mithril",          ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  2);
        register("Weapon_Daggers_Mithril",        ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  2);
        register("Weapon_Club_Mithril",           ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  2);
        register("Weapon_Mace_Mithril",           ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  2);
        register("Weapon_Battleaxe_Mithril",      ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  2);
        register("Weapon_Longsword_Mithril",      ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  2);
        register("Weapon_Shortbow_Mithril",       ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  2);
        register("Weapon_Shield_Mithril",         ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  2);
        register("Weapon_Staff_Mithril",          ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  2);

        // ── Onyxium tier ──────────────────────────────────────────────────────
        register("Weapon_Sword_Onyxium",          ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  2);
        register("Weapon_Axe_Onyxium",            ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  2);
        register("Weapon_Spear_Onyxium",          ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  2);
        register("Weapon_Daggers_Onyxium",        ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  2);
        register("Weapon_Club_Onyxium",           ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  2);
        register("Weapon_Mace_Onyxium",           ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  2);
        register("Weapon_Battleaxe_Onyxium",      ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  2);
        register("Weapon_Longsword_Onyxium",      ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  2);
        register("Weapon_Shortbow_Onyxium",       ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  2);
        register("Weapon_Shield_Onyxium",         ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  2);
        register("Weapon_Staff_Onyxium",          ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  2);

        // ── Adamantite tier ───────────────────────────────────────────────────
        register("Weapon_Sword_Adamantite",       ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  1);
        register("Weapon_Axe_Adamantite",         ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  1);
        register("Weapon_Spear_Adamantite",       ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  1);
        register("Weapon_Daggers_Adamantite",     ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  1);
        register("Weapon_Club_Adamantite",        ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  1);
        register("Weapon_Mace_Adamantite",        ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  1);
        register("Weapon_Battleaxe_Adamantite",   ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  1);
        register("Weapon_Longsword_Adamantite",   ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  1);
        register("Weapon_Shortbow_Adamantite",    ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  1);
        register("Weapon_Shield_Adamantite",      ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  1);
        register("Weapon_Staff_Adamantite",       ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  1);

        // ── Doomed variants ───────────────────────────────────────────────────
        register("Weapon_Sword_Doomed",           ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  3);
        register("Weapon_Axe_Doomed",             ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  3);
        register("Weapon_Spear_Doomed",           ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  3);
        register("Weapon_Daggers_Doomed",         ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  3);
        register("Weapon_Daggers_Fang_Doomed",    ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  2);
        register("Weapon_Club_Doomed",            ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  3);
        register("Weapon_Battleaxe_Doomed",       ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  3);
        register("Weapon_Shortbow_Doomed",        ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  3);
        register("Weapon_Shield_Doomed",          ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  3);
        register("Weapon_Staff_Doomed",           ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  3);

        // ── Special shortbows ─────────────────────────────────────────────────
        register("Weapon_Shortbow_Combat",        ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  6);
        register("Weapon_Shortbow_Flame",         ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  4);
        register("Weapon_Shortbow_Frost",         ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  4);
        register("Weapon_Shortbow_Vampire",       ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  3);
        register("Weapon_Shortbow_Ricochet",      ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  3);
        register("Weapon_Shortbow_Pull",          ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  3);
        register("Weapon_Shortbow_Bomb",          ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  3);

        // ── Special longswords ────────────────────────────────────────────────
        register("Weapon_Longsword_Flame",        ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  3);
        register("Weapon_Longsword_Katana",       ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  3);
        register("Weapon_Longsword_Scarab",       ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  2);

        // ── Special battleaxes ────────────────────────────────────────────────
        register("Weapon_Battleaxe_Scarab",       ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  2);

        // ── Special swords ────────────────────────────────────────────────────
        register("Weapon_Sword_Frost",            ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  3);
        register("Weapon_Sword_Cutlass",          ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  3);
        register("Weapon_Sword_Silversteel",      ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  2);
        register("Weapon_Sword_Steel_Incandescent", ItemCategory.WEAPON, ItemTier.CORNUCOPIA, 2);

        // ── Staffs (magic / elemental) ────────────────────────────────────────
        register("Weapon_Staff_Wizard",           ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  4);
        register("Weapon_Staff_Frost",            ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  3);
        register("Weapon_Staff_Crystal_Flame",    ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  3);
        register("Weapon_Staff_Crystal_Ice",      ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  3);
        register("Weapon_Staff_Crystal_Purple",   ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  3);
        register("Weapon_Staff_Crystal_Red",      ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  3);

        // ── Wands (magic) ─────────────────────────────────────────────────────
        register("Weapon_Wand_Root",              ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  5);
        register("Weapon_Wand_Stoneskin",         ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  5);

        // ── Spellbooks (magic) ────────────────────────────────────────────────
        register("Weapon_Spellbook_Grimoire_Purple", ItemCategory.WEAPON, ItemTier.CORNUCOPIA, 6);
        register("Weapon_Spellbook_Fire",         ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  5);
        register("Weapon_Spellbook_Frost",        ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  5);
        register("Weapon_Spellbook_Demon",        ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  3);

        // ── Mace specials ─────────────────────────────────────────────────────
        register("Weapon_Mace_Prisma",            ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  2);

        // ── Shield specials ───────────────────────────────────────────────────
        register("Weapon_Shield_Orbis_Incandescent", ItemCategory.WEAPON, ItemTier.CORNUCOPIA, 2);
        register("Weapon_Shield_Orbis_Knight",    ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  2);

        // ── Throwables ────────────────────────────────────────────────────────
        register("Weapon_Kunai",                  ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  6);

        // ── Bombs ─────────────────────────────────────────────────────────────
        register("Weapon_Bomb",                   ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  8);
        register("Weapon_Bomb_Fire",              ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  6);
        register("Weapon_Bomb_Stun",              ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  6);
        register("Weapon_Bomb_Large_Fire",        ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  3);
        register("Weapon_Bomb_Potion_Poison",     ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  3);
        register("Weapon_Bomb_Continuous",        ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  3);

        // ── Ultra-rare / unique ───────────────────────────────────────────────
        register("Weapon_Sword_Nexus",            ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  1);
        register("Weapon_Sword_Runic",            ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  1);
        register("Weapon_Longsword_Void",         ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  1);
        register("Weapon_Longsword_Spectral",     ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  1);
        register("Weapon_Battleaxe_Scythe_Void",  ItemCategory.WEAPON, ItemTier.CORNUCOPIA,  1);
        register("Weapon_Spear_Double_Incandescent", ItemCategory.WEAPON, ItemTier.CORNUCOPIA, 1);
        register("Weapon_Daggers_Adamantite_Saurian", ItemCategory.WEAPON, ItemTier.CORNUCOPIA, 1);
        register("Weapon_Longsword_Adamantite_Saurian", ItemCategory.WEAPON, ItemTier.CORNUCOPIA, 1);
        register("Weapon_Spear_Adamantite_Saurian", ItemCategory.WEAPON, ItemTier.CORNUCOPIA, 1);


        // ══════════════════════════════════════════════════════════════════════
        // FIELD ARMOR  —  cloth, leather, wood, wool
        // ══════════════════════════════════════════════════════════════════════

        // ── Cloth: Cotton ─────────────────────────────────────────────────────
        register("Armor_Cloth_Cotton_Head",       ItemCategory.ARMOR, ItemTier.FIELD,  8);
        register("Armor_Cloth_Cotton_Chest",      ItemCategory.ARMOR, ItemTier.FIELD,  8);
        register("Armor_Cloth_Cotton_Hands",      ItemCategory.ARMOR, ItemTier.FIELD,  8);
        register("Armor_Cloth_Cotton_Legs",       ItemCategory.ARMOR, ItemTier.FIELD,  8);

        // ── Cloth: Wool ───────────────────────────────────────────────────────
        register("Armor_Cloth_Wool_Head",         ItemCategory.ARMOR, ItemTier.FIELD,  8);
        register("Armor_Cloth_Wool_Chest",        ItemCategory.ARMOR, ItemTier.FIELD,  8);
        register("Armor_Cloth_Wool_Hands",        ItemCategory.ARMOR, ItemTier.FIELD,  8);
        register("Armor_Cloth_Wool_Legs",         ItemCategory.ARMOR, ItemTier.FIELD,  8);

        // ── Wool (simple) ─────────────────────────────────────────────────────
        register("Armor_Wool_Head",               ItemCategory.ARMOR, ItemTier.FIELD,  7);
        register("Armor_Wool_Chest",              ItemCategory.ARMOR, ItemTier.FIELD,  7);
        register("Armor_Wool_Hands",              ItemCategory.ARMOR, ItemTier.FIELD,  7);
        register("Armor_Wool_Legs",               ItemCategory.ARMOR, ItemTier.FIELD,  7);

        // ── Cloth: Linen ──────────────────────────────────────────────────────
        register("Armor_Cloth_Linen_Head",        ItemCategory.ARMOR, ItemTier.FIELD,  6);
        register("Armor_Cloth_Linen_Chest",       ItemCategory.ARMOR, ItemTier.FIELD,  6);
        register("Armor_Cloth_Linen_Hands",       ItemCategory.ARMOR, ItemTier.FIELD,  6);
        register("Armor_Cloth_Linen_Legs",        ItemCategory.ARMOR, ItemTier.FIELD,  6);

        // ── Leather: Soft ─────────────────────────────────────────────────────
        register("Armor_Leather_Soft_Head",       ItemCategory.ARMOR, ItemTier.FIELD,  7);
        register("Armor_Leather_Soft_Chest",      ItemCategory.ARMOR, ItemTier.FIELD,  7);
        register("Armor_Leather_Soft_Hands",      ItemCategory.ARMOR, ItemTier.FIELD,  7);
        register("Armor_Leather_Soft_Legs",       ItemCategory.ARMOR, ItemTier.FIELD,  7);

        // ── Leather: Light ────────────────────────────────────────────────────
        register("Armor_Leather_Light_Head",      ItemCategory.ARMOR, ItemTier.FIELD,  5);
        register("Armor_Leather_Light_Chest",     ItemCategory.ARMOR, ItemTier.FIELD,  5);
        register("Armor_Leather_Light_Hands",     ItemCategory.ARMOR, ItemTier.FIELD,  5);
        register("Armor_Leather_Light_Legs",      ItemCategory.ARMOR, ItemTier.FIELD,  5);

        // ── Leather: Medium ───────────────────────────────────────────────────
        register("Armor_Leather_Medium_Head",     ItemCategory.ARMOR, ItemTier.FIELD,  4);
        register("Armor_Leather_Medium_Chest",    ItemCategory.ARMOR, ItemTier.FIELD,  4);
        register("Armor_Leather_Medium_Hands",    ItemCategory.ARMOR, ItemTier.FIELD,  4);
        register("Armor_Leather_Medium_Legs",     ItemCategory.ARMOR, ItemTier.FIELD,  4);

        // ── Wood ──────────────────────────────────────────────────────────────
        register("Armor_Wood_Head",               ItemCategory.ARMOR, ItemTier.FIELD,  4);
        register("Armor_Wood_Chest",              ItemCategory.ARMOR, ItemTier.FIELD,  4);
        register("Armor_Wood_Hands",              ItemCategory.ARMOR, ItemTier.FIELD,  4);
        register("Armor_Wood_Legs",               ItemCategory.ARMOR, ItemTier.FIELD,  4);


        // ══════════════════════════════════════════════════════════════════════
        // CORNUCOPIA ARMOR  —  leather heavy through adamantite + special sets
        // ══════════════════════════════════════════════════════════════════════

        // ── Cloth: Silk ───────────────────────────────────────────────────────
        register("Armor_Cloth_Silk_Head",         ItemCategory.ARMOR, ItemTier.CORNUCOPIA,  5);
        register("Armor_Cloth_Silk_Chest",        ItemCategory.ARMOR, ItemTier.CORNUCOPIA,  5);
        register("Armor_Cloth_Silk_Hands",        ItemCategory.ARMOR, ItemTier.CORNUCOPIA,  5);
        register("Armor_Cloth_Silk_Legs",         ItemCategory.ARMOR, ItemTier.CORNUCOPIA,  5);

        // ── Cloth: Cindercloth (fire magic) ───────────────────────────────────
        register("Armor_Cloth_Cindercloth_Head",  ItemCategory.ARMOR, ItemTier.CORNUCOPIA,  3);
        register("Armor_Cloth_Cindercloth_Chest", ItemCategory.ARMOR, ItemTier.CORNUCOPIA,  3);
        register("Armor_Cloth_Cindercloth_Hands", ItemCategory.ARMOR, ItemTier.CORNUCOPIA,  3);
        register("Armor_Cloth_Cindercloth_Legs",  ItemCategory.ARMOR, ItemTier.CORNUCOPIA,  3);

        // ── Leather: Heavy ────────────────────────────────────────────────────
        register("Armor_Leather_Heavy_Head",      ItemCategory.ARMOR, ItemTier.CORNUCOPIA,  6);
        register("Armor_Leather_Heavy_Chest",     ItemCategory.ARMOR, ItemTier.CORNUCOPIA,  6);
        register("Armor_Leather_Heavy_Hands",     ItemCategory.ARMOR, ItemTier.CORNUCOPIA,  6);
        register("Armor_Leather_Heavy_Legs",      ItemCategory.ARMOR, ItemTier.CORNUCOPIA,  6);

        // ── Leather: Raven ────────────────────────────────────────────────────
        register("Armor_Leather_Raven_Head",      ItemCategory.ARMOR, ItemTier.CORNUCOPIA,  4);
        register("Armor_Leather_Raven_Chest",     ItemCategory.ARMOR, ItemTier.CORNUCOPIA,  4);
        register("Armor_Leather_Raven_Hands",     ItemCategory.ARMOR, ItemTier.CORNUCOPIA,  4);
        register("Armor_Leather_Raven_Legs",      ItemCategory.ARMOR, ItemTier.CORNUCOPIA,  4);

        // ── Copper ────────────────────────────────────────────────────────────
        register("Armor_Copper_Head",             ItemCategory.ARMOR, ItemTier.CORNUCOPIA,  7);
        register("Armor_Copper_Chest",            ItemCategory.ARMOR, ItemTier.CORNUCOPIA,  7);
        register("Armor_Copper_Hands",            ItemCategory.ARMOR, ItemTier.CORNUCOPIA,  7);
        register("Armor_Copper_Legs",             ItemCategory.ARMOR, ItemTier.CORNUCOPIA,  7);

        // ── Bronze ────────────────────────────────────────────────────────────
        register("Armor_Bronze_Head",             ItemCategory.ARMOR, ItemTier.CORNUCOPIA,  7);
        register("Armor_Bronze_Chest",            ItemCategory.ARMOR, ItemTier.CORNUCOPIA,  7);
        register("Armor_Bronze_Hands",            ItemCategory.ARMOR, ItemTier.CORNUCOPIA,  7);
        register("Armor_Bronze_Legs",             ItemCategory.ARMOR, ItemTier.CORNUCOPIA,  7);

        // ── Bronze Ornate (3-piece — no Legs in assets) ───────────────────────
        register("Armor_Bronze_Ornate_Head",      ItemCategory.ARMOR, ItemTier.CORNUCOPIA,  4);
        register("Armor_Bronze_Ornate_Chest",     ItemCategory.ARMOR, ItemTier.CORNUCOPIA,  4);
        register("Armor_Bronze_Ornate_Hands",     ItemCategory.ARMOR, ItemTier.CORNUCOPIA,  4);

        // ── Iron ──────────────────────────────────────────────────────────────
        register("Armor_Iron_Head",               ItemCategory.ARMOR, ItemTier.CORNUCOPIA,  8);
        register("Armor_Iron_Chest",              ItemCategory.ARMOR, ItemTier.CORNUCOPIA,  8);
        register("Armor_Iron_Hands",              ItemCategory.ARMOR, ItemTier.CORNUCOPIA,  8);
        register("Armor_Iron_Legs",               ItemCategory.ARMOR, ItemTier.CORNUCOPIA,  8);

        // ── Steel ─────────────────────────────────────────────────────────────
        register("Armor_Steel_Head",              ItemCategory.ARMOR, ItemTier.CORNUCOPIA,  5);
        register("Armor_Steel_Chest",             ItemCategory.ARMOR, ItemTier.CORNUCOPIA,  5);
        register("Armor_Steel_Hands",             ItemCategory.ARMOR, ItemTier.CORNUCOPIA,  5);
        register("Armor_Steel_Legs",              ItemCategory.ARMOR, ItemTier.CORNUCOPIA,  5);

        // ── Steel Ancient ─────────────────────────────────────────────────────
        register("Armor_Steel_Ancient_Head",      ItemCategory.ARMOR, ItemTier.CORNUCOPIA,  4);
        register("Armor_Steel_Ancient_Chest",     ItemCategory.ARMOR, ItemTier.CORNUCOPIA,  4);
        register("Armor_Steel_Ancient_Hands",     ItemCategory.ARMOR, ItemTier.CORNUCOPIA,  4);
        register("Armor_Steel_Ancient_Legs",      ItemCategory.ARMOR, ItemTier.CORNUCOPIA,  4);

        // ── Thorium ───────────────────────────────────────────────────────────
        register("Armor_Thorium_Head",            ItemCategory.ARMOR, ItemTier.CORNUCOPIA,  2);
        register("Armor_Thorium_Chest",           ItemCategory.ARMOR, ItemTier.CORNUCOPIA,  2);
        register("Armor_Thorium_Hands",           ItemCategory.ARMOR, ItemTier.CORNUCOPIA,  2);
        register("Armor_Thorium_Legs",            ItemCategory.ARMOR, ItemTier.CORNUCOPIA,  2);

        // ── Cobalt ────────────────────────────────────────────────────────────
        register("Armor_Cobalt_Head",             ItemCategory.ARMOR, ItemTier.CORNUCOPIA,  3);
        register("Armor_Cobalt_Chest",            ItemCategory.ARMOR, ItemTier.CORNUCOPIA,  3);
        register("Armor_Cobalt_Hands",            ItemCategory.ARMOR, ItemTier.CORNUCOPIA,  3);
        register("Armor_Cobalt_Legs",             ItemCategory.ARMOR, ItemTier.CORNUCOPIA,  3);

        // ── Mithril ───────────────────────────────────────────────────────────
        register("Armor_Mithril_Head",            ItemCategory.ARMOR, ItemTier.CORNUCOPIA,  2);
        register("Armor_Mithril_Chest",           ItemCategory.ARMOR, ItemTier.CORNUCOPIA,  2);
        register("Armor_Mithril_Hands",           ItemCategory.ARMOR, ItemTier.CORNUCOPIA,  2);
        register("Armor_Mithril_Legs",            ItemCategory.ARMOR, ItemTier.CORNUCOPIA,  2);

        // ── Onyxium ───────────────────────────────────────────────────────────
        register("Armor_Onyxium_Head",            ItemCategory.ARMOR, ItemTier.CORNUCOPIA,  2);
        register("Armor_Onyxium_Chest",           ItemCategory.ARMOR, ItemTier.CORNUCOPIA,  2);
        register("Armor_Onyxium_Hands",           ItemCategory.ARMOR, ItemTier.CORNUCOPIA,  2);
        register("Armor_Onyxium_Legs",            ItemCategory.ARMOR, ItemTier.CORNUCOPIA,  2);

        // ── Adamantite ────────────────────────────────────────────────────────
        register("Armor_Adamantite_Head",         ItemCategory.ARMOR, ItemTier.CORNUCOPIA,  1);
        register("Armor_Adamantite_Chest",        ItemCategory.ARMOR, ItemTier.CORNUCOPIA,  1);
        register("Armor_Adamantite_Hands",        ItemCategory.ARMOR, ItemTier.CORNUCOPIA,  1);
        register("Armor_Adamantite_Legs",         ItemCategory.ARMOR, ItemTier.CORNUCOPIA,  1);

        // ── Prisma ────────────────────────────────────────────────────────────
        register("Armor_Prisma_Head",             ItemCategory.ARMOR, ItemTier.CORNUCOPIA,  1);
        register("Armor_Prisma_Chest",            ItemCategory.ARMOR, ItemTier.CORNUCOPIA,  1);
        register("Armor_Prisma_Hands",            ItemCategory.ARMOR, ItemTier.CORNUCOPIA,  1);
        register("Armor_Prisma_Legs",             ItemCategory.ARMOR, ItemTier.CORNUCOPIA,  1);


        // ══════════════════════════════════════════════════════════════════════
        // FIELD CONSUMABLES
        // ══════════════════════════════════════════════════════════════════════

        register("Food_Bread",                    ItemCategory.CONSUMABLE, ItemTier.FIELD, 10);
        register("Food_Beef_Raw",                 ItemCategory.CONSUMABLE, ItemTier.FIELD,  8);
        register("Food_Chicken_Raw",              ItemCategory.CONSUMABLE, ItemTier.FIELD,  8);
        register("Food_Pork_Raw",                 ItemCategory.CONSUMABLE, ItemTier.FIELD,  7);
        register("Food_Fish_Raw",                 ItemCategory.CONSUMABLE, ItemTier.FIELD,  7);
        register("Food_Wildmeat_Raw",             ItemCategory.CONSUMABLE, ItemTier.FIELD,  7);
        register("Food_Egg",                      ItemCategory.CONSUMABLE, ItemTier.FIELD,  5);
        register("Food_Cheese",                   ItemCategory.CONSUMABLE, ItemTier.FIELD,  6);
        register("Food_Fish_Grilled",             ItemCategory.CONSUMABLE, ItemTier.FIELD,  6);
        register("Food_Wildmeat_Cooked",          ItemCategory.CONSUMABLE, ItemTier.FIELD,  6);
        register("Food_Vegetable_Cooked",         ItemCategory.CONSUMABLE, ItemTier.FIELD,  5);
        register("Food_Kebab_Mushroom",           ItemCategory.CONSUMABLE, ItemTier.FIELD,  6);
        register("Bandage_Crude",                 ItemCategory.CONSUMABLE, ItemTier.FIELD,  9);
        register("Potion_Health_Lesser",          ItemCategory.CONSUMABLE, ItemTier.FIELD,  8);
        register("Potion_Health_Small",           ItemCategory.CONSUMABLE, ItemTier.FIELD,  6);
        register("Potion_Regen_Health_Small",     ItemCategory.CONSUMABLE, ItemTier.FIELD,  7);


        // ══════════════════════════════════════════════════════════════════════
        // CORNUCOPIA CONSUMABLES
        // ══════════════════════════════════════════════════════════════════════

        register("Potion_Health",                 ItemCategory.CONSUMABLE, ItemTier.CORNUCOPIA, 10);
        register("Potion_Health_Large",           ItemCategory.CONSUMABLE, ItemTier.CORNUCOPIA,  6);
        register("Potion_Health_Greater",         ItemCategory.CONSUMABLE, ItemTier.CORNUCOPIA,  4);
        register("Potion_Regen_Health",           ItemCategory.CONSUMABLE, ItemTier.CORNUCOPIA,  7);
        register("Potion_Regen_Health_Large",     ItemCategory.CONSUMABLE, ItemTier.CORNUCOPIA,  3);
        register("Potion_Stamina",                ItemCategory.CONSUMABLE, ItemTier.CORNUCOPIA,  6);
        register("Potion_Stamina_Large",          ItemCategory.CONSUMABLE, ItemTier.CORNUCOPIA,  5);
        register("Potion_Regen_Stamina",          ItemCategory.CONSUMABLE, ItemTier.CORNUCOPIA,  5);
        register("Potion_Antidote",               ItemCategory.CONSUMABLE, ItemTier.CORNUCOPIA,  5);
        register("Potion_Purify",                 ItemCategory.CONSUMABLE, ItemTier.CORNUCOPIA,  4);
        register("Food_Kebab_Meat",               ItemCategory.CONSUMABLE, ItemTier.CORNUCOPIA,  8);
        register("Food_Kebab_Fruit",              ItemCategory.CONSUMABLE, ItemTier.CORNUCOPIA,  6);
        register("Food_Pie_Meat",                 ItemCategory.CONSUMABLE, ItemTier.CORNUCOPIA,  6);
        register("Food_Pie_Apple",                ItemCategory.CONSUMABLE, ItemTier.CORNUCOPIA,  7);
        register("Food_Pie_Pumpkin",              ItemCategory.CONSUMABLE, ItemTier.CORNUCOPIA,  6);
        register("Food_Salad_Caesar",             ItemCategory.CONSUMABLE, ItemTier.CORNUCOPIA,  5);
        register("Food_Salad_Berry",              ItemCategory.CONSUMABLE, ItemTier.CORNUCOPIA,  5);
        register("Food_Salad_Mushroom",           ItemCategory.CONSUMABLE, ItemTier.CORNUCOPIA,  5);
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
