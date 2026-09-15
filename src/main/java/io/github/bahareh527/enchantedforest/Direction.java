package io.github.bahareh527.enchantedforest;

import java.util.Locale;
import java.util.Optional;

public enum Direction {
    NORTH,
    EAST,
    SOUTH,
    WEST;

    public static Optional<Direction> from(String value) {
        if (value == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(valueOf(value.strip().toUpperCase(Locale.ROOT)));
        } catch (IllegalArgumentException exception) {
            return Optional.empty();
        }
    }

    public Direction opposite() {
        return switch (this) {
            case NORTH -> SOUTH;
            case EAST -> WEST;
            case SOUTH -> NORTH;
            case WEST -> EAST;
        };
    }

    @Override
    public String toString() {
        return name().toLowerCase(Locale.ROOT);
    }
}
