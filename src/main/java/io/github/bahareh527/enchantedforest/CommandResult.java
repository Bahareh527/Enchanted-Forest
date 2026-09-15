package io.github.bahareh527.enchantedforest;

/** The state and text produced after processing a command. */
public record CommandResult(GameStatus status, String message) {
}
