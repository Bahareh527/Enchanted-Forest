package io.github.bahareh527.enchantedforest;

import java.util.List;
import java.util.Objects;

/** The constructed set of locations and the player's starting point. */
public record World(Location start, List<Location> locations) {
    public World {
        Objects.requireNonNull(start, "start");
        locations = List.copyOf(locations);
        if (!locations.contains(start)) {
            throw new IllegalArgumentException("The start location must belong to the world");
        }
    }
}
