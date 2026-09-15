package io.github.bahareh527.enchantedforest;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/** Player health and bounded inventory. */
public final class Player {
    public static final int MAX_HEALTH = 100;

    private final String name;
    private final int capacity;
    private final Map<String, Item> inventory = new LinkedHashMap<>();
    private int health = MAX_HEALTH;

    public Player(String name, int capacity) {
        this.name = Objects.requireNonNull(name, "name");
        if (capacity < 1) {
            throw new IllegalArgumentException("Inventory capacity must be positive");
        }
        this.capacity = capacity;
    }

    public boolean addItem(Item item) {
        Objects.requireNonNull(item, "item");
        String key = normalize(item.name());
        if (inventory.containsKey(key) || inventory.size() >= capacity) {
            return false;
        }
        inventory.put(key, item);
        return true;
    }

    public Optional<Item> item(String itemName) {
        return Optional.ofNullable(inventory.get(normalize(itemName)));
    }

    public Optional<Item> removeItem(String itemName) {
        return Optional.ofNullable(inventory.remove(normalize(itemName)));
    }

    public void adjustHealth(int amount) {
        health = Math.max(0, Math.min(MAX_HEALTH, health + amount));
    }

    public int health() {
        return health;
    }

    public boolean isAlive() {
        return health > 0;
    }

    public int itemCount() {
        return inventory.size();
    }

    public int capacity() {
        return capacity;
    }

    public String inventoryDescription() {
        if (inventory.isEmpty()) {
            return "Inventory: empty (0/" + capacity + ")";
        }
        String names = inventory.values().stream().map(Item::name).collect(Collectors.joining(", "));
        return "Inventory: " + names + " (" + inventory.size() + '/' + capacity + ')';
    }

    private static String normalize(String value) {
        return value == null ? "" : value.strip().toLowerCase(Locale.ROOT);
    }
}
