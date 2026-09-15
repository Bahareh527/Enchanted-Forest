package io.github.bahareh527.enchantedforest;

import java.util.Objects;

/** A concrete item placed in the world or carried by the player. */
public record Item(ItemType type) {
    public Item {
        Objects.requireNonNull(type, "type");
    }

    public String name() {
        return type.displayName();
    }

    public String description() {
        return type.description();
    }
}
