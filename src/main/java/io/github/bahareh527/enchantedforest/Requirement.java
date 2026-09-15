package io.github.bahareh527.enchantedforest;

/** An item-gated obstacle protecting a location. */
public enum Requirement {
    SWORD(ItemType.SWORD, "A wall of giant spiders blocks %s. You need a Sword.",
            "You drive the spiders away. The path to %s is clear."),
    GOLDEN_KEY(ItemType.GOLDEN_KEY, "%s is sealed. You need the Golden Key.",
            "The Golden Key turns, and %s opens."),
    TORCH(ItemType.TORCH, "%s is too dark to enter safely. You need a Torch.",
            "Your Torch illuminates %s. The passage is now safe.");

    private final ItemType itemType;
    private final String blockedTemplate;
    private final String resolvedTemplate;

    Requirement(ItemType itemType, String blockedTemplate, String resolvedTemplate) {
        this.itemType = itemType;
        this.blockedTemplate = blockedTemplate;
        this.resolvedTemplate = resolvedTemplate;
    }

    public ItemType itemType() {
        return itemType;
    }

    public String blockedMessage(String destinationName) {
        return blockedTemplate.formatted(destinationName);
    }

    public String resolvedMessage(String destinationName) {
        return resolvedTemplate.formatted(destinationName);
    }
}
