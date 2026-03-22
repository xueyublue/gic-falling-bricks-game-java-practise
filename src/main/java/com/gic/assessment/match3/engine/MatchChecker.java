package com.gic.assessment.match3.engine;

import com.gic.assessment.match3.model.Field;

/**
 * Detects and removes "matches" on the field.
 *
 * A match is a horizontal or vertical run of {@code minRunLength} or more
 * consecutive cells containing the same symbol.  All matches are identified
 * first, then removed simultaneously.
 */
public class MatchChecker {

    /** Default minimum run length when not specified (classic match-3). */
    public static final int DEFAULT_MIN_RUN_LENGTH = 3;

    /**
     * Same as {@link #checkAndClear(Field, int)} with {@link #DEFAULT_MIN_RUN_LENGTH}.
     */
    public static int checkAndClear(Field field) {
        return checkAndClear(field, DEFAULT_MIN_RUN_LENGTH);
    }

    /**
     * Scans the entire field for horizontal and vertical runs of {@code minRunLength}+
     * identical symbols, then clears (sets to EMPTY) every matched cell.
     *
     * @param field        the game field to check and modify
     * @param minRunLength minimum contiguous same-symbol cells to count as a match (≥ 2)
     * @return the total number of cells that were cleared
     * @throws IllegalArgumentException if {@code minRunLength} is less than 2
     */
    public static int checkAndClear(Field field, int minRunLength) {
        if (minRunLength < 2) {
            throw new IllegalArgumentException("minRunLength must be at least 2, got: " + minRunLength);
        }
        boolean[][] toRemove = new boolean[field.getHeight()][field.getWidth()];

        markHorizontalRuns(field, toRemove, minRunLength);
        markVerticalRuns(field, toRemove, minRunLength);

        return clearMarkedCells(field, toRemove);
    }

    /**
     * Scans every row left-to-right for horizontal runs of identical symbols.
     * Skips ahead past each run to avoid redundant re-scanning.
     */
    private static void markHorizontalRuns(Field field, boolean[][] toRemove, int minRunLength) {
        for (int row = 0; row < field.getHeight(); row++) {
            int col = 0;
            while (col <= field.getWidth() - minRunLength) {
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
                if (runLength >= minRunLength) {
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
    private static void markVerticalRuns(Field field, boolean[][] toRemove, int minRunLength) {
        for (int col = 0; col < field.getWidth(); col++) {
            int row = 0;
            while (row <= field.getHeight() - minRunLength) {
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
                if (runLength >= minRunLength) {
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
