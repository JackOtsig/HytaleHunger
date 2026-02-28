package dev.jackOtsig.loot;

public class LootItem {

    private final String itemId;
    private final ItemCategory category;
    private final ItemTier tier;
    private int weight;

    public LootItem(String itemId, ItemCategory category, ItemTier tier, int weight) {
        this.itemId = itemId;
        this.category = category;
        this.tier = tier;
        this.weight = weight;
    }

    public String getItemId() { return itemId; }
    public ItemCategory getCategory() { return category; }
    public ItemTier getTier() { return tier; }
    public int getWeight() { return weight; }

    /** Live weight tuning — e.g. from a config reload or admin command. */
    public void setWeight(int weight) { this.weight = weight; }
}
