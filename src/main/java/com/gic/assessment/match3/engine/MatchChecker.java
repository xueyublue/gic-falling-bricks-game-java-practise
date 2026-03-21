package com.gic.assessment.match3.engine;

import com.gic.assessment.match3.model.Field;

/**
 * Detects and removes "matches" on the field.
 *
 * A match is a horizontal or vertical run of 3 or more consecutive cells
 * containing the same symbol.  All matches are identified first, then
 * removed simultaneously.  After removal, gravity is applied so that
 * floating symbols fall down to fill gaps.  This process repeats until
 * no new matches are formed (chain reactions).
 */
public class MatchChecker {

    private static final int MIN_RUN_LENGTH = 3;
    static final int POINTS_PER_CELL = 10;

    /**
     * Holds the result of a full match-clear-gravity cycle.
     *
     * @param totalCleared total number of cells cleared across all chain levels
     * @param score        points earned: sum of (cells * POINTS_PER_CELL * chainLevel) per chain
     */
    public record ScoreResult(int totalCleared, int score) {}

    /**
     * Repeatedly scans for matches, clears them, and applies gravity
     * until no further matches exist (resolving chain reactions).
     *
     * Scoring: each chain level earns {@code cleared * POINTS_PER_CELL * chainLevel}.
     * Chain 1 (initial match) = 1x, chain 2 (first cascade) = 2x, etc.
     *
     * @param field the game field to check and modify
     * @return a {@link ScoreResult} with the total cleared count and score
     */
    public static ScoreResult checkAndClear(Field field) {
        int totalCleared = 0;
        int totalScore = 0;
        int chainLevel = 0;
        int cleared;
        do {
            cleared = singlePassClear(field);
            if (cleared > 0) {
                chainLevel++;
                totalScore += cleared * POINTS_PER_CELL * chainLevel;
                field.applyGravity();
            }
            totalCleared += cleared;
        } while (cleared > 0);
        return new ScoreResult(totalCleared, totalScore);
    }

    /**
     * Performs a single round of match detection and clearing (no gravity).
     *
     * @param field the game field to check and modify
     * @return the number of cells cleared in this pass
     */
    static int singlePassClear(Field field) {
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
