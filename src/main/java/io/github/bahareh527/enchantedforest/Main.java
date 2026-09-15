package io.github.bahareh527.enchantedforest;

import java.util.Scanner;

/** Console entry point for Enchanted Forest. */
public final class Main {
    private Main() {
    }

    public static void main(String[] args) {
        Game game = new Game();
        System.out.println(game.welcomeMessage());

        try (Scanner scanner = new Scanner(System.in)) {
            while (game.status() == GameStatus.RUNNING) {
                System.out.print("\n> ");
                if (!scanner.hasNextLine()) {
                    break;
                }
                CommandResult result = game.execute(scanner.nextLine());
                System.out.println(result.message());
            }
        }
    }
}
