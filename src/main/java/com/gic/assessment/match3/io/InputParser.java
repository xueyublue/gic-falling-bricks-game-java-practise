package com.gic.assessment.match3.io;

import com.gic.assessment.match3.model.Brick;
import com.gic.assessment.match3.model.Command;
import com.gic.assessment.match3.model.Orientation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Parses user input for two distinct purposes:
 *   1. Game initialisation  — field dimensions + brick definitions.
     *   2. Per-frame commands   — up to 2 commands (L, R, D, T).
 */
public class InputParser {

    private static final int MAX_BRICKS = 5;
    private static final int MAX_COMMANDS = 2;

    /**
     * Holds the result of parsing the initialisation line.
     *
     * @param width  number of columns in the field
     * @param height number of rows in the field
     * @param bricks list of Brick definitions (up to 5)
     */
    public record GameConfig(int width, int height, List<Brick> bricks) {}

    /**
     * Parses the initialisation line entered by the user.
     *
     * Expected format: "WIDTH HEIGHT [BRICK1] [BRICK2] ... [BRICK5]"
     * Example:         "5 8 H^^* V*@^"
     *
     * - The first token  is the field width.
     * - The second token is the field height.
     * - Each subsequent token is a 4-character brick definition (e.g. "H^^*").
     * - At most 5 bricks are accepted; any extras are ignored.
     *
     * @param input the raw input line from the user
     * @return a GameConfig containing the parsed width, height, and brick list
     * @throws IllegalArgumentException if the input is blank, missing dimensions,
     *                                  or contains invalid brick tokens
     */
    public static GameConfig parseInitInput(String input) {
        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException("Input cannot be empty");
        }

        // Split on whitespace to extract individual tokens
        String[] tokens = input.trim().split("\\s+");
        if (tokens.length < 2) {
            throw new IllegalArgumentException("Must provide at least width and height");
        }

        // tokens[0] = width, tokens[1] = height
        int width = Integer.parseInt(tokens[0]);
        int height = Integer.parseInt(tokens[1]);

        if (width < 1 || height < 1) {
            throw new IllegalArgumentException("Width and height must be positive");
        }

        // Remaining tokens are brick definitions; collect up to MAX_BRICKS
        List<Brick> bricks = new ArrayList<>();
        for (int i = 2; i < tokens.length && bricks.size() < MAX_BRICKS; i++) {
            bricks.add(parseBrick(tokens[i]));
        }

        return new GameConfig(width, height, Collections.unmodifiableList(bricks));
    }

    /**
     * Parses a single 4-character brick token.
     *
     * Format: first char = orientation ('H' or 'V'),
     *         next 3 chars = the three block symbols.
     * Example: "H^^*" → Horizontal brick with symbols ^, ^, *
     *          "V*@^" → Vertical   brick with symbols *, @, ^
     *
     * @param token the 4-character brick string
     * @return a new Brick constructed from the token
     * @throws IllegalArgumentException if the token length is not 4
     */
    public static Brick parseBrick(String token) {
        if (token == null || token.length() != 4) {
            throw new IllegalArgumentException(
                    "Brick must be exactly 4 characters (orientation + 3 symbols): " + token);
        }
        // char 0 → orientation, chars 1-3 → symbols
        Orientation orientation = Orientation.fromChar(token.charAt(0));
        return new Brick(orientation, token.charAt(1), token.charAt(2), token.charAt(3));
    }

    /**
     * Parses the per-frame command string entered by the user.
     *
     * Scans the input character-by-character:
     *   - 'L' → Command.LEFT
     *   - 'R' → Command.RIGHT
     *   - 'D' → Command.DROP
     *   - 'T' → Command.TURN
     *   - Any other character is silently skipped.
     *
     * Only the first TWO valid commands are collected; the rest are ignored
     * (per the game rule: "up to 2 commands per frame").
     *
     * @param input the raw command string (may be null or empty)
     * @return an unmodifiable list containing 0, 1, or 2 Command values
     */
    public static List<Command> parseCommands(String input) {
        List<Command> commands = new ArrayList<>(MAX_COMMANDS);
        if (input == null) {
            return Collections.emptyList(); // no input → no commands
        }
        for (int i = 0; i < input.length() && commands.size() < MAX_COMMANDS; i++) {
            Command cmd = Command.fromChar(input.charAt(i)); // null if invalid char
            if (cmd != null) {
                commands.add(cmd);
            }
        }
        return Collections.unmodifiableList(commands);
    }
}
