package com.gic.assessment.match3.io;

import com.gic.assessment.match3.model.ActiveBrick;
import com.gic.assessment.match3.model.Brick;
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
 *
 * An optional {@link Brick} preview shows the next brick to spawn, rendered
 * in a column to the right of each field row (aligned on the first rows).
 */
public class FieldRenderer {

    private static final String PREVIEW_GAP = "   ";

    /**
     * Same as {@link #render(Field, ActiveBrick, Brick)} with no next-brick preview.
     */
    public static String render(Field field, ActiveBrick activeBrick) {
        return render(field, activeBrick, null);
    }

    /**
     * Builds a display string showing the field with an optional active brick
     * and an optional preview of the upcoming brick definition.
     *
     * @param field       the game field (contains placed/stationary bricks)
     * @param activeBrick the currently falling brick to overlay, or null if none
     * @param nextBrick   the next brick in the queue to show as a preview, or null
     * @return a multi-line string ready to be printed to the console
     */
    public static String render(Field field, ActiveBrick activeBrick, Brick nextBrick) {
        char[][] display = buildDisplayGrid(field, activeBrick);
        String[] previewColumn = buildNextBrickPreviewColumn(nextBrick, field.getHeight());

        StringBuilder sb = new StringBuilder();

        int rowLabelWidth = String.valueOf(field.getHeight()).length();

        for (int row = 0; row < field.getHeight(); row++) {
            sb.append(padLeft(row + 1, rowLabelWidth));

            sb.append("\t| ");

            for (int col = 0; col < field.getWidth(); col++) {
                if (col > 0) {
                    sb.append(' ');
                }
                sb.append(display[row][col]);
            }

            sb.append(" |");

            if (previewColumn[row] != null && !previewColumn[row].isEmpty()) {
                sb.append(PREVIEW_GAP).append(previewColumn[row]);
            }

            if (row < field.getHeight() - 1) {
                sb.append('\n');
            }
        }
        return sb.toString();
    }

    /**
     * One display line per field row: text shown to the right of the field border.
     * Horizontal next bricks use the first row only; vertical uses up to three rows.
     */
    private static String[] buildNextBrickPreviewColumn(Brick next, int fieldHeight) {
        String[] lines = new String[fieldHeight];
        for (int i = 0; i < fieldHeight; i++) {
            lines[i] = "";
        }
        if (next == null) {
            return lines;
        }
        if (next.isHorizontal()) {
            lines[0] = String.format(
                    "Next: H %c %c %c",
                    next.symbolAt(0), next.symbolAt(1), next.symbolAt(2));
        } else {
            lines[0] = String.format("Next: V %c", next.symbolAt(0));
            // Align lower symbols under the top cell (same column as char after "V ")
            if (fieldHeight > 1) {
                lines[1] = String.format("        %c", next.symbolAt(1));
            }
            if (fieldHeight > 2) {
                lines[2] = String.format("        %c", next.symbolAt(2));
            }
        }
        return lines;
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
