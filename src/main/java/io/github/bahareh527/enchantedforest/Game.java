package io.github.bahareh527.enchantedforest;

import java.util.Optional;

/** Coordinates player commands and the game world without performing console I/O. */
public final class Game {
    private static final String HELP = """
            Commands:
              look                  describe your surroundings
              go <direction>        move north, east, south, or west
              take <item>           pick up an item
              drop <item>           leave an item here
              use <item>            use a tool or potion
              examine <item>        inspect an item you carry
              inventory             list carried items
              status                show health and location
              map                   display the forest map
              help                  show this command list
              quit                  leave the game

            Tip: type a direction by itself as a shortcut.
            """;

    private static final String MAP = """
                                     [Treasure Sanctuary]
                                               |
                                          [Dark Cave]
                                               |
                 [Moonlit Clearing] --- [Ancient Gate]
                          |                    |
                   [Spider Grove] ----- [Forest Entrance] ----- [Bottomless Lake]
            """;

    private final CommandParser parser;
    private final Player player;
    private Location currentLocation;
    private GameStatus status;

    public Game() {
        this(WorldFactory.create(), new CommandParser(), new Player("Adventurer", 3));
    }

    Game(World world, CommandParser parser, Player player) {
        this.currentLocation = world.start();
        this.parser = parser;
        this.player = player;
        this.status = GameStatus.RUNNING;
    }

    public String welcomeMessage() {
        return """
                Welcome to Enchanted Forest!
                Find the hidden treasure, but choose your path carefully.
                Type 'help' for commands.

                """ + currentLocation.describe();
    }

    public CommandResult execute(String input) {
        if (status != GameStatus.RUNNING) {
            return result("The game has ended. Start a new game to play again.");
        }

        Command command = parser.parse(input);
        String message = switch (command.action()) {
            case "" -> "Please enter a command. Type 'help' if you need a hint.";
            case "help" -> HELP.stripTrailing();
            case "look" -> currentLocation.describe();
            case "map" -> MAP.stripTrailing();
            case "inventory" -> player.inventoryDescription();
            case "status", "health" -> statusDescription();
            case "go" -> move(command);
            case "take", "get" -> take(command);
            case "drop" -> drop(command);
            case "use" -> use(command);
            case "examine" -> examine(command);
            case "quit" -> quit();
            default -> "I don't understand '" + command.action() + "'. Type 'help' for commands.";
        };
        return result(message);
    }

    public GameStatus status() {
        return status;
    }

    public int health() {
        return player.health();
    }

    public String currentLocationId() {
        return currentLocation.id();
    }

    private String move(Command command) {
        if (!command.hasArgument()) {
            return "Go where? Choose north, east, south, or west.";
        }

        Optional<Direction> direction = Direction.from(command.argument());
        if (direction.isEmpty()) {
            return "'" + command.argument() + "' is not a direction.";
        }

        Location destination = currentLocation.exit(direction.get()).orElse(null);
        if (destination == null) {
            return "You can't go " + direction.get() + " from here.";
        }
        if (destination.requirement().isPresent()) {
            return destination.requirement().get().blockedMessage(destination.name());
        }

        currentLocation = destination;
        StringBuilder message = new StringBuilder("You travel ")
                .append(direction.get()).append(" to ").append(destination.name()).append(".\n")
                .append(destination.describe());

        if (destination.damage() > 0) {
            player.adjustHealth(-destination.damage());
            message.append("\nYou lose ").append(destination.damage())
                    .append(" health. Health: ").append(player.health()).append('/').append(Player.MAX_HEALTH).append('.');
            if (!player.isAlive()) {
                status = GameStatus.LOST;
                message.append("\nThe forest claims another adventurer. Game over.");
            }
        }
        return message.toString();
    }

    private String take(Command command) {
        if (!command.hasArgument()) {
            return "Take what?";
        }
        Item item = currentLocation.item(command.argument()).orElse(null);
        if (item == null) {
            return "There is no '" + command.argument() + "' here.";
        }
        if (!player.addItem(item)) {
            return "Your inventory is full. Drop something before taking " + item.name() + ".";
        }

        currentLocation.removeItem(item.name());
        if (item.type() == ItemType.TREASURE) {
            status = GameStatus.WON;
            return "You lift the Enchanted Treasure. The forest brightens around you—victory!";
        }
        return "You take " + item.name() + ".";
    }

    private String drop(Command command) {
        if (!command.hasArgument()) {
            return "Drop what?";
        }
        Item item = player.removeItem(command.argument()).orElse(null);
        if (item == null) {
            return "You are not carrying '" + command.argument() + "'.";
        }
        currentLocation.addItem(item);
        return "You drop " + item.name() + ".";
    }

    private String use(Command command) {
        if (!command.hasArgument()) {
            return "Use what?";
        }
        Item item = player.item(command.argument()).orElse(null);
        if (item == null) {
            return "You are not carrying '" + command.argument() + "'.";
        }

        if (item.type() == ItemType.HEALTH_POTION) {
            if (player.health() == Player.MAX_HEALTH) {
                return "Your health is already full.";
            }
            int before = player.health();
            player.adjustHealth(ItemType.HEALTH_POTION.effectValue());
            player.removeItem(item.name());
            return "You drink the Health Potion and restore " + (player.health() - before)
                    + " health. Health: " + player.health() + '/' + Player.MAX_HEALTH + ".";
        }

        for (Direction direction : Direction.values()) {
            Location destination = currentLocation.exit(direction).orElse(null);
            if (destination == null || destination.requirement().isEmpty()) {
                continue;
            }
            Requirement requirement = destination.requirement().get();
            if (requirement.itemType() == item.type()) {
                destination.clearRequirement();
                player.removeItem(item.name());
                return requirement.resolvedMessage(destination.name());
            }
        }
        return item.name() + " cannot be used here.";
    }

    private String examine(Command command) {
        if (!command.hasArgument()) {
            return "Examine what?";
        }
        return player.item(command.argument())
                .map(item -> item.name() + ": " + item.description())
                .orElse("You are not carrying '" + command.argument() + "'.");
    }

    private String quit() {
        status = GameStatus.QUIT;
        return "You leave the forest for another day. Goodbye!";
    }

    private String statusDescription() {
        return "Location: " + currentLocation.name() + " | Health: " + player.health() + '/'
                + Player.MAX_HEALTH + " | Items: " + player.itemCount() + '/' + player.capacity();
    }

    private CommandResult result(String message) {
        return new CommandResult(status, message);
    }
}
