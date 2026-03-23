package com.gic.assessment.match3.engine;

import com.gic.assessment.match3.model.Field;

/**
 * Detects and removes "matches" on the field.
 *
 * A match is a horizontal or vertical run of 3 or more consecutive cells
 * containing the same symbol.  All matches are identified first, then
 * removed simultaneously.  Gravity is NOT applied after removal — cleared
 * cells simply become empty ('.').
 */
public class MatchChecker {

    private static final int MIN_RUN_LENGTH = 3;

    /**
     * Scans the entire field for horizontal and vertical matches of 3+
     * identical symbols, then clears (sets to EMPTY) every matched cell.
     *
     * Algorithm:
     *   1. Create a boolean[][] "toRemove" grid, same size as the field.
     *   2. Scan every row left-to-right for horizontal runs of 3+.
     *   3. Scan every column top-to-bottom for vertical runs of 3+.
     *   4. Any cell marked true in toRemove is set to EMPTY on the field.
     *
     * All matches are collected before any cells are cleared, so overlapping
     * matches (e.g. a cross shape) are handled correctly in a single pass.
     *
     * @param field the game field to check and modify
     * @return the total number of cells that were cleared
     */
    public static int checkAndClear(Field field) {
        int totalCleared = 0;
        int cleared = 0;
        do {
            cleared = singleCheckAndClear(field);
            if (cleared > 0) {
                field.applyGravity();
            }
            totalCleared += cleared;
        } while (cleared > 0);
        return totalCleared;
    }

    public static int singleCheckAndClear(Field field) {
        // toRemove[row][col] = true means this cell is part of a match
        boolean[][] toRemove = new boolean[field.getHeight()][field.getWidth()];

        markHorizontalRuns(field, toRemove);
        markVerticalRuns(field, toRemove);

        return clearMarkedCells(field, toRemove);
    }

    /**
     * Scans every row left-to-right for horizontal runs of identical symbols.
     * Skips ahead past each run to avoid redundant re-scanning.
     */
    private static void markHorizontalRuns(Field field, boolean[][] toRemove) {
        for (int row = 0; row < field.getHeight(); row++) {
            int col = 0;
            while (col <= field.getWidth() - MIN_RUN_LENGTH) {
                char symbol = field.getCell(row, col);
                if (symbol == Field.EMPTY) {
                    col++;
                    continue;
                }

                // Count how many consecutive cells to the right share the same symbol
                int runLength = 1;
                while (col + runLength < field.getWidth()
                        && field.getCell(row, col + runLength) == symbol) {
                    runLength++;
                }

                // If the run is long enough, mark every cell in it for removal
                if (runLength >= MIN_RUN_LENGTH) {
                    for (int k = 0; k < runLength; k++) {
                        toRemove[row][col + k] = true;
                    }
                }

                col += runLength; // skip past the run
            }
        }
    }

    /**
     * Scans every column top-to-bottom for vertical runs of identical symbols.
     * Skips ahead past each run to avoid redundant re-scanning.
     */
    private static void markVerticalRuns(Field field, boolean[][] toRemove) {
        for (int col = 0; col < field.getWidth(); col++) {
            int row = 0;
            while (row <= field.getHeight() - MIN_RUN_LENGTH) {
                char symbol = field.getCell(row, col);
                if (symbol == Field.EMPTY) {
                    row++;
                    continue;
                }

                // Count how many consecutive cells downward share the same symbol
                int runLength = 1;
                while (row + runLength < field.getHeight()
                        && field.getCell(row + runLength, col) == symbol) {
                    runLength++;
                }

                // If the run is long enough, mark every cell in it for removal
                if (runLength >= MIN_RUN_LENGTH) {
                    for (int k = 0; k < runLength; k++) {
                        toRemove[row + k][col] = true;
                    }
                }

                row += runLength; // skip past the run
            }
        }
    }

    /**
     * Clears all cells marked for removal and returns the count.
     */
    private static int clearMarkedCells(Field field, boolean[][] toRemove) {
        int cleared = 0; // counter for how many cells are removed
        for (int row = 0; row < field.getHeight(); row++) {
            for (int col = 0; col < field.getWidth(); col++) {
                if (toRemove[row][col]) {
                    field.setCell(row, col, Field.EMPTY); // erase the symbol
                    cleared++;
                }
            }
        }
        return cleared;
    }
}
