package com.gic.assessment.match3.io;

import com.gic.assessment.match3.model.ActiveBrick;
import com.gic.assessment.match3.model.Field;
import com.gic.assessment.match3.model.Position;

import java.util.List;

/**
 * Renders the game field to a String for console display.
 *
 * Output format (one line per row, 1-indexed):
 *   N\t| c c c c c |
 *
 * Where N is the row number, \t is a tab, and each 'c' is either a symbol
 * from a placed brick, the active brick's symbol, or '.' for an empty cell.
 *
 * If an ActiveBrick is provided, its symbols are overlaid on top of the
 * field grid so the user can see the brick in its current position.
 */
public class FieldRenderer {

    /**
     * Builds a display string showing the field with an optional active brick.
     *
     * @param field       the game field (contains placed/stationary bricks)
     * @param activeBrick the currently falling brick to overlay, or null if none
     * @return a multi-line string ready to be printed to the console
     */
    public static String render(Field field, ActiveBrick activeBrick) {
        // Build a 2D char array that merges the field grid + active brick
        char[][] display = buildDisplayGrid(field, activeBrick);

        StringBuilder sb = new StringBuilder();

        // Determine padding width for row labels (e.g. height=8 → width 1,
        // height=12 → width 2) so numbers align neatly.
        int rowLabelWidth = String.valueOf(field.getHeight()).length();

        for (int row = 0; row < field.getHeight(); row++) {
            // Row number, right-aligned and padded, e.g. " 1" or "12"
            sb.append(padLeft(row + 1, rowLabelWidth));

            // Tab + opening border
            sb.append("\t| ");

            // Print each cell in this row, separated by spaces
            for (int col = 0; col < field.getWidth(); col++) {
                if (col > 0) {
                    sb.append(' '); // space between columns
                }
                sb.append(display[row][col]); // the character to show
            }

            // Closing border
            sb.append(" |");

            // Newline between rows, but not after the very last row
            if (row < field.getHeight() - 1) {
                sb.append('\n');
            }
        }
        return sb.toString();
    }

    /**
     * Creates a 2D char array representing what should be displayed.
     *
     * Step 1: Copy every cell from the field grid (placed bricks + empty cells).
     * Step 2: If there is an active brick, overwrite its cells with its symbols.
     *
     * @param field       the game field
     * @param activeBrick the falling brick to overlay (may be null)
     * @return a char[][] indexed by [row][col]
     */
    private static char[][] buildDisplayGrid(Field field, ActiveBrick activeBrick) {
        char[][] display = new char[field.getHeight()][field.getWidth()];

        // Step 1 — copy the underlying field state into the display array
        for (int row = 0; row < field.getHeight(); row++) {
            for (int col = 0; col < field.getWidth(); col++) {
                display[row][col] = field.getCell(row, col);
            }
        }

        // Step 2 — overlay the active brick (if one exists)
        if (activeBrick != null) {
            List<Position> cells = activeBrick.getOccupiedCells();
            for (int i = 0; i < cells.size(); i++) {
                Position pos = cells.get(i);
                if (field.isInBounds(pos.row(), pos.col())) {
                    // Overwrite the display cell with the brick's symbol
                    display[pos.row()][pos.col()] = activeBrick.getSymbolAt(i);
                }
            }
        }

        return display;
    }

    /**
     * Right-aligns a number within a fixed width using String.format.
     * Example: padLeft(3, 2) → " 3"
     *
     * @param number the integer to format
     * @param width  the minimum character width
     * @return the formatted string
     */
    private static String padLeft(int number, int width) {
        return String.format("%" + width + "d", number);
    }
}
