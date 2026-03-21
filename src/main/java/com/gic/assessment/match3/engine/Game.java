package com.gic.assessment.match3.engine;

import com.gic.assessment.match3.io.FieldRenderer;
import com.gic.assessment.match3.io.InputParser;
import com.gic.assessment.match3.model.ActiveBrick;
import com.gic.assessment.match3.model.Brick;
import com.gic.assessment.match3.model.Command;
import com.gic.assessment.match3.model.Field;

import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;

/**
 * Orchestrates the main game loop for a single round of Match-3.
 *
 * Lifecycle:  initialise → game loop (per-brick) → game over.
 *
 * The game loop spawns bricks one at a time.  Each brick falls through
 * a series of "frames" where the user enters commands (L/R/D).  Once a
 * brick can no longer drop, it becomes stationary, matches are checked,
 * and the next brick is spawned.  The round ends when all bricks have
 * been placed or a new brick cannot be spawned (starting cells blocked).
 */
public class Game {

    private static final String INIT_PROMPT =
            "Please enter field size (width and height) and up to 5 bricks set:";
    private static final String COMMAND_PROMPT =
            "Enter up to 2 commands to process before moving to the next frame (valid commands are L, R, D):";
    private static final String GAME_OVER = "Game Over.";

    /** Reads user input from the console */
    private final Scanner scanner;

    /** Output stream for printing prompts, frames, and messages */
    private final PrintStream out;

    /** The game field (2D grid) where stationary bricks accumulate */
    private Field field;

    /** The ordered list of brick definitions to be played in this round */
    private List<Brick> bricks;

    /** Index into the bricks list — points to the NEXT brick to spawn (0-based) */
    private int currentBrickIndex;

    /** The brick currently falling on the field (null when between bricks) */
    private ActiveBrick activeBrick;

    /** Monotonically increasing frame counter displayed to the user (1-based) */
    private int frameNumber;

    /** Cumulative score earned from match clearing */
    private int score;

    /**
     * @param scanner input source (typically wrapping System.in)
     * @param out     output destination (typically System.out)
     */
    public Game(Scanner scanner, PrintStream out) {
        this.scanner = scanner;
        this.out = out;
    }

    /**
     * Runs one complete game session:
     *   1. Prompt the user for field size and bricks.
     *   2. Execute the game loop (brick-by-brick, frame-by-frame).
     *   3. Print "Game Over." when the round ends.
     */
    public void run() {
        initialize();
        gameLoop();
        out.println(GAME_OVER + " Final Score: " + score);
    }

    /**
     * Prompts the user for the field dimensions and brick set,
     * then initialises all game state for a fresh round.
     */
    private void initialize() {
        out.println(INIT_PROMPT);
        String input = scanner.nextLine(); // e.g. "5 8 H^^* V*@^"
        InputParser.GameConfig config = InputParser.parseInitInput(input);

        field = new Field(config.width(), config.height()); // empty grid
        bricks = config.bricks();       // ordered list of brick definitions
        currentBrickIndex = 0;          // start with the first brick
        frameNumber = 0;                // will be incremented to 1 on the first frame
        score = 0;                      // no points yet
    }

    /**
     * The main game loop — processes bricks one at a time.
     *
     * For each brick:
     *   1. Spawn it at the top-centre of the field.
     *      If the starting cells are blocked → break (game over).
     *   2. Run processBrick() which handles frames until the brick is stationary.
     *   3. Write the brick's symbols onto the field permanently.
     *   4. Clear matches, apply gravity, and repeat until no chain reactions remain.
     *   5. Move to the next brick.
     *
     * After all bricks are placed (or a brick could not be spawned),
     * a final frame is displayed showing the resulting field state.
     */
    private void gameLoop() {
        while (currentBrickIndex < bricks.size()) {
            // Try to spawn the next brick at the top-centre of the field
            activeBrick = ActiveBrick.createAtStart(bricks.get(currentBrickIndex), field);

            if (activeBrick == null) {
                // Starting position is blocked → game ends immediately
                break;
            }

            // Let the user control this brick until it lands
            processBrick();

            // Brick is now stationary — write its symbols onto the field
            activeBrick.placeOnField(field);

            // Clear matches, apply gravity, resolve chains, and accumulate score
            score += MatchChecker.checkAndClear(field).score();

            // Advance to the next brick in the list
            currentBrickIndex++;
        }

        // Show one final frame (no active brick — only placed symbols are rendered)
        displayFrame(null);
    }

    /**
     * Handles one brick through successive frames until it becomes stationary.
     *
     * Each frame:
     *   1. Display the field with the active brick overlaid.
     *   2. Prompt the user for up to 2 commands (L, R, D).
     *   3. Apply the parsed commands to the active brick.
     *   4. Attempt to auto-drop the brick by 1 row:
     *      - If the brick CAN move down  → move it and continue to the next frame.
     *      - If the brick CANNOT move down → it is now stationary; return.
     */
    private void processBrick() {
        while (true) {
            displayFrame(activeBrick);

            // Ask the user for commands
            out.println(COMMAND_PROMPT);
            List<Command> commands = InputParser.parseCommands(scanner.nextLine());

            // Apply each command in order (moves that would go out of bounds
            // or collide with a placed brick are silently ignored)
            for (Command cmd : commands) {
                activeBrick.applyCommand(cmd, field);
            }

            // Auto-drop: try to move the brick down by 1 row
            if (!activeBrick.canMove(field, 1, 0)) {
                // The brick cannot fall any further → it becomes stationary
                return;
            }
            // Drop the brick one row for the next frame
            activeBrick.moveDown();
        }
    }

    /**
     * Increments the frame counter and prints the current field state.
     *
     * @param brick the active brick to overlay, or null for the final frame
     */
    private void displayFrame(ActiveBrick brick) {
        frameNumber++;
        out.println("Frame " + frameNumber);
        out.println("Score: " + score);
        out.println(FieldRenderer.render(field, brick));
    }
}
