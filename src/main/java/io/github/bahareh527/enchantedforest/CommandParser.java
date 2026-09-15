package io.github.bahareh527.enchantedforest;

import java.util.Locale;

/** Converts one line of player input into a command. */
public final class CommandParser {
    public Command parse(String input) {
        if (input == null || input.isBlank()) {
            return new Command("", "");
        }

        String normalized = input.strip().toLowerCase(Locale.ROOT).replaceAll("\\s+", " ");
        if (Direction.from(normalized).isPresent()) {
            return new Command("go", normalized);
        }

        String[] parts = normalized.split(" ", 2);
        String argument = parts.length == 2 ? parts[1] : "";
        return new Command(expandAlias(parts[0]), argument);
    }

    private String expandAlias(String action) {
        return switch (action) {
            case "i", "inv" -> "inventory";
            case "l" -> "look";
            case "x" -> "examine";
            case "q", "exit" -> "quit";
            default -> action;
        };
    }
}
