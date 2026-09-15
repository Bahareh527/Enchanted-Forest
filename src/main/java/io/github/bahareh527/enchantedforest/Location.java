package io.github.bahareh527.enchantedforest;

import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/** One place in the game world, including its exits, items, obstacles, and damage. */
public final class Location {
    private final String id;
    private final String name;
    private final String description;
    private final Map<Direction, Location> exits = new EnumMap<>(Direction.class);
    private final Map<String, Item> items = new LinkedHashMap<>();
    private Requirement requirement;
    private int damage;

    public Location(String id, String name, String description) {
        this.id = Objects.requireNonNull(id, "id");
        this.name = Objects.requireNonNull(name, "name");
        this.description = Objects.requireNonNull(description, "description");
    }

    public String id() {
        return id;
    }

    public String name() {
        return name;
    }

    public Location connect(Direction direction, Location destination) {
        exits.put(Objects.requireNonNull(direction, "direction"),
                Objects.requireNonNull(destination, "destination"));
        return this;
    }

    public Optional<Location> exit(Direction direction) {
        return Optional.ofNullable(exits.get(direction));
    }

    public Location addItem(Item item) {
        items.put(normalize(item.name()), Objects.requireNonNull(item, "item"));
        return this;
    }

    public Optional<Item> item(String itemName) {
        return Optional.ofNullable(items.get(normalize(itemName)));
    }

    public Optional<Item> removeItem(String itemName) {
        return Optional.ofNullable(items.remove(normalize(itemName)));
    }

    public Location requires(Requirement newRequirement) {
        this.requirement = Objects.requireNonNull(newRequirement, "newRequirement");
        return this;
    }

    public Optional<Requirement> requirement() {
        return Optional.ofNullable(requirement);
    }

    public void clearRequirement() {
        requirement = null;
    }

    public Location damages(int points) {
        if (points < 0) {
            throw new IllegalArgumentException("Damage cannot be negative");
        }
        damage = points;
        return this;
    }

    public int damage() {
        return damage;
    }

    public String describe() {
        String visibleItems = items.isEmpty()
                ? "none"
                : items.values().stream().map(Item::name).collect(Collectors.joining(", "));
        String availableExits = exits.keySet().stream().map(Direction::toString).collect(Collectors.joining(", "));
        return name + "\n" + description + "\nItems: " + visibleItems + "\nExits: " + availableExits;
    }

    private static String normalize(String value) {
        return value == null ? "" : value.strip().toLowerCase(Locale.ROOT);
    }
}
