package io.github.bahareh527.enchantedforest;

import java.util.ArrayList;
import java.util.List;

/** Minimal dependency-free test runner used locally and in continuous integration. */
public final class AllTests {
    private int passed;
    private final List<String> failures = new ArrayList<>();

    private AllTests() {
    }

    public static void main(String[] args) {
        AllTests suite = new AllTests();
        suite.run("parser handles whitespace and multi-word items", suite::parserHandlesInput);
        suite.run("parser expands direction and command shortcuts", suite::parserExpandsShortcuts);
        suite.run("player enforces inventory capacity", suite::playerEnforcesCapacity);
        suite.run("locked routes explain their requirements", suite::obstaclesBlockMovement);
        suite.run("health potion restores health without exceeding maximum", suite::healthPotionRestoresHealth);
        suite.run("bottomless lake ends the game", suite::lakeEndsGame);
        suite.run("complete route reaches the win state", suite::winningRouteWorks);
        suite.finish();
    }

    private void parserHandlesInput() {
        Command command = new CommandParser().parse("  TAKE   Golden   Key  ");
        equal("take", command.action());
        equal("golden key", command.argument());
    }

    private void parserExpandsShortcuts() {
        Command direction = new CommandParser().parse("NORTH");
        equal("go", direction.action());
        equal("north", direction.argument());
        equal("inventory", new CommandParser().parse("i").action());
    }

    private void playerEnforcesCapacity() {
        Player player = new Player("Tester", 3);
        check(player.addItem(new Item(ItemType.SWORD)), "first item should fit");
        check(player.addItem(new Item(ItemType.GOLDEN_KEY)), "second item should fit");
        check(player.addItem(new Item(ItemType.TORCH)), "third item should fit");
        check(!player.addItem(new Item(ItemType.HEALTH_POTION)), "fourth item should be rejected");
        equal(3, player.itemCount());
    }

    private void obstaclesBlockMovement() {
        Game game = new Game();
        contains(game.execute("go west").message(), "need a Sword");
        equal("entrance", game.currentLocationId());
        contains(game.execute("go north").message(), "need the Golden Key");
        equal("entrance", game.currentLocationId());
    }

    private void healthPotionRestoresHealth() {
        Game game = new Game();
        play(game, "take sword", "use sword", "west", "take golden key", "take health potion",
                "east", "use golden key", "north", "take torch", "use torch", "north");
        equal(80, game.health());
        contains(game.execute("use health potion").message(), "restore 20 health");
        equal(100, game.health());
    }

    private void lakeEndsGame() {
        Game game = new Game();
        CommandResult result = game.execute("east");
        equal(GameStatus.LOST, result.status());
        equal(0, game.health());
        contains(result.message(), "Game over");
    }

    private void winningRouteWorks() {
        Game game = new Game();
        play(game, "take sword", "use sword", "go west", "take golden key", "go east",
                "use golden key", "go north", "take torch", "use torch", "go north", "go north");
        CommandResult result = game.execute("take enchanted treasure");
        equal(GameStatus.WON, result.status());
        contains(result.message(), "victory");
        equal("treasure-sanctuary", game.currentLocationId());
    }

    private void run(String name, TestCase test) {
        try {
            test.execute();
            passed++;
            System.out.println("PASS  " + name);
        } catch (AssertionError error) {
            failures.add(name + ": " + error.getMessage());
            System.out.println("FAIL  " + name);
        }
    }

    private void finish() {
        System.out.println("\n" + passed + " passed, " + failures.size() + " failed");
        if (!failures.isEmpty()) {
            failures.forEach(failure -> System.err.println("- " + failure));
            throw new AssertionError("Test suite failed");
        }
    }

    private static void play(Game game, String... commands) {
        for (String command : commands) {
            CommandResult result = game.execute(command);
            if (result.status() != GameStatus.RUNNING) {
                throw new AssertionError("Game ended during command '" + command + "': " + result.message());
            }
        }
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static void contains(String actual, String expectedFragment) {
        if (!actual.contains(expectedFragment)) {
            throw new AssertionError("Expected <" + actual + "> to contain <" + expectedFragment + ">");
        }
    }

    private static void equal(Object expected, Object actual) {
        if (!expected.equals(actual)) {
            throw new AssertionError("Expected <" + expected + "> but was <" + actual + ">");
        }
    }

    @FunctionalInterface
    private interface TestCase {
        void execute();
    }
}
