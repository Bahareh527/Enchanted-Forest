package io.github.bahareh527.enchantedforest;

public enum ItemType {
    SWORD("Sword", "A balanced blade capable of clearing the spider grove.", 0),
    GOLDEN_KEY("Golden Key", "An ornate key that fits the ancient gate.", 0),
    TORCH("Torch", "A resin-soaked torch bright enough for the dark cave.", 0),
    HEALTH_POTION("Health Potion", "A crimson potion that restores up to 50 health.", 50),
    TREASURE("Enchanted Treasure", "The legendary treasure hidden at the forest's heart.", 0);

    private final String displayName;
    private final String description;
    private final int effectValue;

    ItemType(String displayName, String description, int effectValue) {
        this.displayName = displayName;
        this.description = description;
        this.effectValue = effectValue;
    }

    public String displayName() {
        return displayName;
    }

    public String description() {
        return description;
    }

    public int effectValue() {
        return effectValue;
    }
}
