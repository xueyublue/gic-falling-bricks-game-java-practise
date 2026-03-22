package com.gic.assessment.match3.model;

import java.util.List;

/**
 * Represents a brick that is currently "in play" — actively falling on the field.
 *
 * The brick's position is tracked by an anchor point (row, col), which is the
 * top-left cell of the brick:
 *   Horizontal brick → occupies (row, col), (row, col+1), (row, col+2)
 *   Vertical   brick → occupies (row, col), (row+1, col), (row+2, col)
 *
 * Once the brick can no longer move down, it becomes "stationary" and its
 * symbols are written permanently onto the Field via {@link #placeOnField}.
 */
public class ActiveBrick {

    /** The underlying brick definition (orientation + 3 symbols). Replaced on rotation. */
    private Brick brick;

    /** Row index of the anchor (top-left) cell — 0 = top of field */
    private int row;

    /** Column index of the anchor (top-left) cell — 0 = left edge of field */
    private int col;

    /**
     * @param brick the brick definition to wrap
     * @param row   initial row of the anchor cell
     * @param col   initial column of the anchor cell
     */
    public ActiveBrick(Brick brick, int row, int col) {
        this.brick = brick;
        this.row = row;
        this.col = col;
    }

    /** @return the underlying Brick definition */
    public Brick getBrick() {
        return brick;
    }

    /** @return current anchor row (0-indexed, 0 = top) */
    public int getRow() {
        return row;
    }

    /** @return current anchor column (0-indexed, 0 = left) */
    public int getCol() {
        return col;
    }

    /**
     * Computes the three grid positions this brick currently occupies,
     * based on its anchor position and orientation.
     *
     * @return an immutable list of three {@link Position} values
     */
    public List<Position> getOccupiedCells() {
        if (brick.isHorizontal()) {
            // Horizontal: same row, columns shift right
            return List.of(
                    new Position(row, col),
                    new Position(row, col + 1),
                    new Position(row, col + 2));
        }
        // Vertical: same column, rows shift down
        return List.of(
                new Position(row, col),
                new Position(row + 1, col),
                new Position(row + 2, col));
    }

    /**
     * Returns the symbol character at the given positional index (0, 1, or 2).
     * Delegates to the underlying Brick.
     *
     * @param cellIndex 0-based index matching the order in getOccupiedCells()
     * @return the symbol character at that cell
     */
    public char getSymbolAt(int cellIndex) {
        return brick.symbolAt(cellIndex);
    }

    /**
     * Checks whether this brick can legally occupy its current position.
     * Every cell must be within bounds AND not already occupied on the field.
     *
     * Used when spawning a new brick to verify the starting position is free.
     *
     * @param field the game field to check against
     * @return true if all three cells are in-bounds and empty
     */
    public boolean canPlace(Field field) {
        for (Position pos : getOccupiedCells()) {
            if (!field.isInBounds(pos.row(), pos.col()) || field.isOccupied(pos.row(), pos.col())) {
                return false; // blocked or out of bounds
            }
        }
        return true;
    }

    /**
     * Checks whether the brick can move by a given delta (dRow, dCol).
     *
     * Every cell of the brick is shifted by the delta; if ALL shifted cells
     * are in-bounds and empty, the move is legal.
     *
     * Common usage:
     *   canMove(field, 0, -1)  → can move LEFT?
     *   canMove(field, 0,  1)  → can move RIGHT?
     *   canMove(field, 1,  0)  → can move DOWN by 1 row?
     *
     * @param field the game field to check against
     * @param dRow  row delta (positive = downward)
     * @param dCol  column delta (positive = rightward)
     * @return true if the move is legal
     */
    public boolean canMove(Field field, int dRow, int dCol) {
        for (Position pos : getOccupiedCells()) {
            Position target = pos.offset(dRow, dCol);
            if (!field.isInBounds(target.row(), target.col()) || field.isOccupied(target.row(), target.col())) {
                return false;
            }
        }
        return true;
    }

    /** Moves the brick one column to the left (decrements col). */
    public void moveLeft() {
        col--;
    }

    /** Moves the brick one column to the right (increments col). */
    public void moveRight() {
        col++;
    }

    /** Moves the brick one row downward (increments row). */
    public void moveDown() {
        row++;
    }

    /**
     * Checks whether the brick can rotate (H↔V) at its current anchor.
     *
     * Creates a temporary ActiveBrick with the rotated brick definition at the
     * same anchor position, then checks whether all three cells are in-bounds
     * and empty on the field.
     *
     * @param field the game field to check against
     * @return true if the rotation is legal
     */
    public boolean canRotate(Field field) {
        ActiveBrick rotated = new ActiveBrick(brick.rotated(), row, col);
        return rotated.canPlace(field);
    }

    /**
     * Rotates the brick by swapping its orientation (H↔V).
     * The anchor position (row, col) stays the same.
     */
    public void rotate() {
        brick = brick.rotated();
    }

    /**
     * Applies a single command to this brick on the given field.
     *
     * LEFT  → move left  if the space is free (canMove with dCol = -1)
     * RIGHT → move right if the space is free (canMove with dCol = +1)
     * DROP  → drop all the way down until blocked
     * TURN  → rotate H↔V if the rotated position is free
     *
     * If the move would go out of bounds or collide with a placed brick,
     * the command is silently ignored (the brick stays in place).
     *
     * @param command the command to apply (may be null)
     * @param field   the game field used for collision checks
     * @return true if the command was a recognised type, false if null
     */
    public boolean applyCommand(Command command, Field field) {
        if (command == null) {
            return false;
        }
        switch (command) {
            case LEFT -> {
                if (canMove(field, 0, -1)) moveLeft();
            }
            case RIGHT -> {
                if (canMove(field, 0, 1)) moveRight();
            }
            case DROP -> drop(field);
            case TURN -> {
                if (canRotate(field)) rotate();
            }
        }
        return true;
    }

    /**
     * Drops the brick as far down as possible in a single action.
     * Repeatedly moves down one row until the next row would be blocked
     * (out of bounds or occupied).
     *
     * @param field the game field used for collision checks
     */
    public void drop(Field field) {
        while (canMove(field, 1, 0)) { // while the row below is free…
            moveDown();                // …keep falling
        }
    }

    /**
     * Writes this brick's three symbols onto the field grid, making
     * the brick "stationary" (permanent).  After this call the cells
     * are occupied and will block future bricks.
     *
     * @param field the game field to write symbols into
     */
    public void placeOnField(Field field) {
        List<Position> cells = getOccupiedCells();
        for (int i = 0; i < cells.size(); i++) {
            Position pos = cells.get(i);
            field.setCell(pos.row(), pos.col(), brick.symbolAt(i)); // write symbol
        }
    }

    /**
     * Factory method: creates an ActiveBrick positioned at its starting
     * location for the given field.
     *
     * Starting position rules:
     *   - The brick always starts at row 0 (the very top).
     *   - Horizontal: centred using (fieldWidth - 3) / 2 so the 3-wide
     *     brick sits in the middle columns.
     *   - Vertical: centred using (fieldWidth - 1) / 2 so the 1-wide
     *     brick sits in the middle column.
     *
     * If the starting cells are already occupied (blocked by previously
     * placed bricks), returns null — the game should end.
     *
     * @param brick the brick definition to place
     * @param field the game field
     * @return a new ActiveBrick at the start position, or null if blocked
     */
    public static ActiveBrick createAtStart(Brick brick, Field field) {
        int startRow = 0; // always spawn at the top row
        int startCol = brick.isHorizontal()
                ? (field.getWidth() - 3) / 2   // centre a 3-column-wide brick
                : (field.getWidth() - 1) / 2;  // centre a 1-column-wide brick

        ActiveBrick active = new ActiveBrick(brick, startRow, startCol);

        // If any of the three starting cells are blocked, the brick cannot spawn
        return active.canPlace(field) ? active : null;
    }
}
