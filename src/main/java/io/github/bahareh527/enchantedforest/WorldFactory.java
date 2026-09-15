package io.github.bahareh527.enchantedforest;

import java.util.List;

/** Builds the deterministic Enchanted Forest map. */
public final class WorldFactory {
    private WorldFactory() {
    }

    public static World create() {
        Location entrance = new Location(
                "entrance",
                "Forest Entrance",
                "Ancient trees lean over three paths. One leads toward a sealed stone gate."
        ).addItem(new Item(ItemType.SWORD));

        Location spiderGrove = new Location(
                "spider-grove",
                "Spider Grove",
                "Silver webs cover the branches. Beyond them, something golden catches the light."
        ).requires(Requirement.SWORD)
                .addItem(new Item(ItemType.GOLDEN_KEY))
                .addItem(new Item(ItemType.HEALTH_POTION));

        Location ancientGate = new Location(
                "ancient-gate",
                "Ancient Gate",
                "A weathered arch marks the route deeper into the forest."
        ).requires(Requirement.GOLDEN_KEY)
                .addItem(new Item(ItemType.TORCH));

        Location moonlitClearing = new Location(
                "moonlit-clearing",
                "Moonlit Clearing",
                "Pale flowers form a quiet circle between the dangerous paths."
        );

        Location darkCave = new Location(
                "dark-cave",
                "Dark Cave",
                "Your light reveals old carvings, but falling stones make the crossing painful."
        ).requires(Requirement.TORCH).damages(20);

        Location treasureSanctuary = new Location(
                "treasure-sanctuary",
                "Treasure Sanctuary",
                "Warm light surrounds a pedestal at the heart of the forest."
        ).addItem(new Item(ItemType.TREASURE));

        Location bottomlessLake = new Location(
                "bottomless-lake",
                "Bottomless Lake",
                "The ground disappears beneath black water. There is no way back."
        ).damages(Player.MAX_HEALTH);

        connectBothWays(entrance, Direction.WEST, spiderGrove);
        connectBothWays(entrance, Direction.NORTH, ancientGate);
        connectBothWays(entrance, Direction.EAST, bottomlessLake);
        connectBothWays(spiderGrove, Direction.NORTH, moonlitClearing);
        connectBothWays(moonlitClearing, Direction.EAST, ancientGate);
        connectBothWays(ancientGate, Direction.NORTH, darkCave);
        connectBothWays(darkCave, Direction.NORTH, treasureSanctuary);

        return new World(entrance, List.of(
                entrance, spiderGrove, ancientGate, moonlitClearing,
                darkCave, treasureSanctuary, bottomlessLake
        ));
    }

    private static void connectBothWays(Location first, Direction direction, Location second) {
        first.connect(direction, second);
        second.connect(direction.opposite(), first);
    }
}
