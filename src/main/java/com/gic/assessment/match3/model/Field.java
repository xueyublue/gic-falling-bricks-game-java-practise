package com.gic.assessment.match3.model;

import java.util.Arrays;

/**
 * The game field — a 2D grid of characters.
 *
 * Coordinate system (0-indexed internally):
 *   row 0 is the TOP of the field, row (height-1) is the BOTTOM.
 *   col 0 is the LEFT edge,        col (width-1)  is the RIGHT edge.
 *
 * Each cell either holds a symbol character (~, ^, *, @) from a placed brick,
 * or the EMPTY constant ('.') meaning the cell is unoccupied.
 */
public class Field {

    /** Character used to represent an empty (unoccupied) cell */
    public static final char EMPTY = '.';

    /** Number of columns in the field */
    private final int width;

    /** Number of rows in the field */
    private final int height;

    /**
     * The 2D grid storing the state of every cell.
     * Indexed as grid[row][col].
     */
    private final char[][] grid;

    /**
     * Creates a new field with the given dimensions, all cells initialised to EMPTY.
     *
     * @param width  number of columns (must be >= 1)
     * @param height number of rows    (must be >= 1)
     */
    public Field(int width, int height) {
        if (width < 1 || height < 1) {
            throw new IllegalArgumentException("Field dimensions must be positive");
        }
        this.width = width;
        this.height = height;
        this.grid = new char[height][width]; // grid[row][col]
        clear(); // fill every cell with EMPTY ('.')
    }

    /**
     * Resets every cell in the grid to EMPTY ('.').
     */
    public void clear() {
        for (char[] row : grid) {
            Arrays.fill(row, EMPTY);
        }
    }

    /** @return the number of columns */
    public int getWidth() {
        return width;
    }

    /** @return the number of rows */
    public int getHeight() {
        return height;
    }

    /**
     * Returns the character stored at the given cell.
     *
     * @param row 0-based row index (0 = top)
     * @param col 0-based column index (0 = left)
     * @return the character at that cell
     * @throws IndexOutOfBoundsException if (row, col) is outside the grid
     */
    public char getCell(int row, int col) {
        validateBounds(row, col);
        return grid[row][col];
    }

    /**
     * Overwrites the character at the given cell.
     *
     * @param row    0-based row index
     * @param col    0-based column index
     * @param symbol the character to write (a symbol or EMPTY)
     * @throws IndexOutOfBoundsException if (row, col) is outside the grid
     */
    public void setCell(int row, int col, char symbol) {
        validateBounds(row, col);
        grid[row][col] = symbol;
    }

    /** @return true if (row, col) is inside the grid AND the cell is EMPTY */
    public boolean isEmpty(int row, int col) {
        return isInBounds(row, col) && grid[row][col] == EMPTY;
    }

    /** @return true if (row, col) falls within the grid boundaries */
    public boolean isInBounds(int row, int col) {
        return row >= 0 && row < height && col >= 0 && col < width;
    }

    /** @return true if (row, col) is inside the grid AND the cell is NOT empty */
    public boolean isOccupied(int row, int col) {
        return isInBounds(row, col) && grid[row][col] != EMPTY;
    }

    public void applyGravity() {
        for (int col = 0; col < width; col++) {
            int writeRow = height - 1;
            for (int readRow = height - 1; readRow >= 0; readRow--) {
                if (grid[readRow][col] != EMPTY) {
                    grid[writeRow][col] = grid[readRow][col];
                    if (readRow != writeRow) {
                        grid[readRow][col] = EMPTY;
                    }
                    writeRow--;
                }
            }
        }
    }

    private void validateBounds(int row, int col) {
        if (!isInBounds(row, col)) {
            throw new IndexOutOfBoundsException(
                    "Cell (" + row + ", " + col + ") is outside the " + width + "x" + height + " field");
        }
    }
}
