package com.gic.assessment.match3;

import com.gic.assessment.match3.engine.Game;

import java.io.PrintStream;
import java.util.Scanner;

/**
 * Entry point for the Match-3 falling-bricks game.
 *
 * Outer loop: the player can restart (S) or quit (Q) after each round.
 * Each round is fully managed by a Game instance.
 */
public class FallingBricksGame {

    private final Scanner scanner;
    private final PrintStream out;

    public FallingBricksGame(Scanner scanner, PrintStream out) {
        this.scanner = scanner;
        this.out = out;
    }

    /**
     * Runs the outer game loop: play rounds until the user chooses to quit.
     */
    public void start() {
        out.println("Welcome to Match-3 game!");

        // running — controls the outer replay loop; false when the player quits
        boolean running = true;
        while (running) {
            // Create a fresh Game for each round (new field, new bricks)
            Game game = new Game(scanner, out);
            game.run(); // plays one full round: init → frames → game over

            // After the round ends, ask the player whether to restart or quit
            running = promptRestart();
        }

        out.println("Thank you for playing Match-3!");
    }

    /**
     * Repeatedly prompts the user until they enter 'S' (start over) or 'Q' (quit).
     *
     * @return true if the player chose to start over, false if they chose to quit
     */
    private boolean promptRestart() {
        while (true) {
            out.println("Enter S to start over or Q to quit:");
            String input = scanner.nextLine().trim().toUpperCase(); // normalise
            if (input.equals("S")) {
                return true;  // restart → outer loop creates a new Game
            } else if (input.equals("Q")) {
                return false; // quit → outer loop exits
            }
            // Any other input is ignored; prompt again
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        FallingBricksGame app = new FallingBricksGame(scanner, System.out);
        app.start();
        scanner.close(); // release the input resource
    }
}
