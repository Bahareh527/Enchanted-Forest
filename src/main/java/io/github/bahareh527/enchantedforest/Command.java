package io.github.bahareh527.enchantedforest;

/** A normalized action and its optional argument. */
public record Command(String action, String argument) {
    public Command {
        action = action == null ? "" : action.strip().toLowerCase();
        argument = argument == null || argument.isBlank()
                ? ""
                : argument.strip().toLowerCase();
    }

    public boolean hasArgument() {
        return !argument.isEmpty();
    }
}
