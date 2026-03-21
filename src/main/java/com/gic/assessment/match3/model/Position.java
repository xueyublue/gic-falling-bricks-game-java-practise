package com.gic.assessment.match3.model;

/**
 * An immutable (row, col) coordinate on the game field.
 *
 * @param row 0-based row index (0 = top of field)
 * @param col 0-based column index (0 = left edge)
 */
public record Position(int row, int col) {

    /**
     * Returns a new Position shifted by the given deltas.
     *
     * @param dRow row delta (positive = downward)
     * @param dCol column delta (positive = rightward)
     * @return the shifted position
     */
    public Position offset(int dRow, int dCol) {
        return new Position(row + dRow, col + dCol);
    }
}
