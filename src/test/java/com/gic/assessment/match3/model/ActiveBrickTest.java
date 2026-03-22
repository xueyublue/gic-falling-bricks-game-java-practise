package com.gic.assessment.match3.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ActiveBrickTest {

    @Test
    void horizontalBrickStartPosition() {
        Field field = new Field(5, 8);
        Brick brick = new Brick(Orientation.HORIZONTAL, '^', '^', '*');
        ActiveBrick active = ActiveBrick.createAtStart(brick, field);

        assertNotNull(active);
        assertEquals(0, active.getRow());
        assertEquals(1, active.getCol()); // (5-3)/2 = 1
    }

    @Test
    void verticalBrickStartPosition() {
        Field field = new Field(5, 8);
        Brick brick = new Brick(Orientation.VERTICAL, '*', '@', '^');
        ActiveBrick active = ActiveBrick.createAtStart(brick, field);

        assertNotNull(active);
        assertEquals(0, active.getRow());
        assertEquals(2, active.getCol()); // (5-1)/2 = 2
    }

    @Test
    void horizontalBrickOccupiedCells() {
        Brick brick = new Brick(Orientation.HORIZONTAL, '^', '^', '*');
        ActiveBrick active = new ActiveBrick(brick, 0, 1);

        List<Position> cells = active.getOccupiedCells();
        assertEquals(3, cells.size());
        assertEquals(new Position(0, 1), cells.get(0));
        assertEquals(new Position(0, 2), cells.get(1));
        assertEquals(new Position(0, 3), cells.get(2));
    }

    @Test
    void verticalBrickOccupiedCells() {
        Brick brick = new Brick(Orientation.VERTICAL, '*', '@', '^');
        ActiveBrick active = new ActiveBrick(brick, 0, 2);

        List<Position> cells = active.getOccupiedCells();
        assertEquals(3, cells.size());
        assertEquals(new Position(0, 2), cells.get(0));
        assertEquals(new Position(1, 2), cells.get(1));
        assertEquals(new Position(2, 2), cells.get(2));
    }

    @Test
    void moveLeft() {
        Field field = new Field(5, 8);
        Brick brick = new Brick(Orientation.HORIZONTAL, '^', '^', '*');
        ActiveBrick active = new ActiveBrick(brick, 0, 1);

        assertTrue(active.canMove(field, 0, -1));
        active.applyCommand(Command.LEFT, field);
        assertEquals(0, active.getCol());
    }

    @Test
    void moveLeftBlockedByBounds() {
        Field field = new Field(5, 8);
        Brick brick = new Brick(Orientation.HORIZONTAL, '^', '^', '*');
        ActiveBrick active = new ActiveBrick(brick, 0, 0);

        assertFalse(active.canMove(field, 0, -1));
        active.applyCommand(Command.LEFT, field);
        assertEquals(0, active.getCol());
    }

    @Test
    void moveRight() {
        Field field = new Field(5, 8);
        Brick brick = new Brick(Orientation.HORIZONTAL, '^', '^', '*');
        ActiveBrick active = new ActiveBrick(brick, 0, 1);

        assertTrue(active.canMove(field, 0, 1));
        active.applyCommand(Command.RIGHT, field);
        assertEquals(2, active.getCol());
    }

    @Test
    void moveRightBlockedByBounds() {
        Field field = new Field(5, 8);
        Brick brick = new Brick(Orientation.HORIZONTAL, '^', '^', '*');
        ActiveBrick active = new ActiveBrick(brick, 0, 2); // occupies cols 2,3,4

        assertFalse(active.canMove(field, 0, 1));
        active.applyCommand(Command.RIGHT, field);
        assertEquals(2, active.getCol());
    }

    @Test
    void dropToBottom() {
        Field field = new Field(5, 8);
        Brick brick = new Brick(Orientation.HORIZONTAL, '^', '^', '*');
        ActiveBrick active = new ActiveBrick(brick, 0, 1);

        active.applyCommand(Command.DROP, field);
        assertEquals(7, active.getRow());
    }

    @Test
    void dropAboveExistingBrick() {
        Field field = new Field(5, 8);
        field.setCell(7, 2, '*');
        Brick brick = new Brick(Orientation.HORIZONTAL, '^', '^', '*');
        ActiveBrick active = new ActiveBrick(brick, 0, 1);

        active.applyCommand(Command.DROP, field);
        assertEquals(6, active.getRow());
    }

    @Test
    void dropVerticalAboveExistingBrick() {
        Field field = new Field(5, 8);
        field.setCell(7, 2, '*');
        Brick brick = new Brick(Orientation.VERTICAL, '*', '@', '^');
        ActiveBrick active = new ActiveBrick(brick, 0, 2);

        active.applyCommand(Command.DROP, field);
        assertEquals(4, active.getRow());
    }

    @Test
    void moveBlockedByExistingBrick() {
        Field field = new Field(5, 8);
        field.setCell(0, 0, '*');
        Brick brick = new Brick(Orientation.HORIZONTAL, '^', '^', '*');
        ActiveBrick active = new ActiveBrick(brick, 0, 1);

        active.applyCommand(Command.LEFT, field);
        assertEquals(1, active.getCol());
    }

    @Test
    void cannotPlaceWhenBlocked() {
        Field field = new Field(5, 8);
        field.setCell(0, 2, '*');
        Brick brick = new Brick(Orientation.HORIZONTAL, '^', '^', '*');
        ActiveBrick active = ActiveBrick.createAtStart(brick, field);

        assertNull(active);
    }

    @Test
    void placeOnField() {
        Field field = new Field(5, 8);
        Brick brick = new Brick(Orientation.HORIZONTAL, '^', '^', '*');
        ActiveBrick active = new ActiveBrick(brick, 7, 1);
        active.placeOnField(field);

        assertEquals('^', field.getCell(7, 1));
        assertEquals('^', field.getCell(7, 2));
        assertEquals('*', field.getCell(7, 3));
    }

    @Test
    void horizontalStartInWidthThreeField() {
        Field field = new Field(3, 5);
        Brick brick = new Brick(Orientation.HORIZONTAL, '^', '*', '@');
        ActiveBrick active = ActiveBrick.createAtStart(brick, field);

        assertNotNull(active);
        assertEquals(0, active.getCol()); // (3-3)/2 = 0
    }

    @Test
    void verticalStartInWidthOneField() {
        Field field = new Field(1, 5);
        Brick brick = new Brick(Orientation.VERTICAL, '^', '*', '@');
        ActiveBrick active = ActiveBrick.createAtStart(brick, field);

        assertNotNull(active);
        assertEquals(0, active.getCol()); // (1-1)/2 = 0
    }

    @Test
    void horizontalStartEvenWidthField() {
        Field field = new Field(6, 5);
        Brick brick = new Brick(Orientation.HORIZONTAL, '^', '*', '@');
        ActiveBrick active = ActiveBrick.createAtStart(brick, field);

        assertNotNull(active);
        assertEquals(1, active.getCol()); // (6-3)/2 = 1
    }

    @Test
    void verticalStartEvenWidthField() {
        Field field = new Field(6, 5);
        Brick brick = new Brick(Orientation.VERTICAL, '^', '*', '@');
        ActiveBrick active = ActiveBrick.createAtStart(brick, field);

        assertNotNull(active);
        assertEquals(2, active.getCol()); // (6-1)/2 = 2
    }

    @Test
    void verticalBrickMoveLeftBlockedByBounds() {
        Field field = new Field(3, 5);
        Brick brick = new Brick(Orientation.VERTICAL, '^', '*', '@');
        ActiveBrick active = new ActiveBrick(brick, 0, 0);

        assertFalse(active.canMove(field, 0, -1));
        active.applyCommand(Command.LEFT, field);
        assertEquals(0, active.getCol());
    }

    @Test
    void verticalBrickMoveRightBlockedByBounds() {
        Field field = new Field(3, 5);
        Brick brick = new Brick(Orientation.VERTICAL, '^', '*', '@');
        ActiveBrick active = new ActiveBrick(brick, 0, 2);

        assertFalse(active.canMove(field, 0, 1));
        active.applyCommand(Command.RIGHT, field);
        assertEquals(2, active.getCol());
    }

    @Test
    void moveRightBlockedByExistingBrick() {
        Field field = new Field(5, 8);
        field.setCell(0, 4, '*');
        Brick brick = new Brick(Orientation.HORIZONTAL, '^', '^', '*');
        ActiveBrick active = new ActiveBrick(brick, 0, 1);

        active.applyCommand(Command.RIGHT, field);
        assertEquals(1, active.getCol());
    }

    @Test
    void verticalBrickMoveBlockedByExistingBrick() {
        Field field = new Field(5, 8);
        field.setCell(1, 1, '@');
        Brick brick = new Brick(Orientation.VERTICAL, '^', '*', '~');
        ActiveBrick active = new ActiveBrick(brick, 0, 2);

        active.applyCommand(Command.LEFT, field);
        assertEquals(2, active.getCol()); // blocked by @ at (1,1)
    }

    @Test
    void applyCommandWithNull() {
        Field field = new Field(5, 8);
        Brick brick = new Brick(Orientation.HORIZONTAL, '^', '^', '*');
        ActiveBrick active = new ActiveBrick(brick, 0, 1);

        boolean result = active.applyCommand(null, field);
        assertFalse(result);
        assertEquals(1, active.getCol());
    }

    @Test
    void dropWhenAlreadyAtBottom() {
        Field field = new Field(5, 3);
        Brick brick = new Brick(Orientation.HORIZONTAL, '^', '*', '@');
        ActiveBrick active = new ActiveBrick(brick, 2, 0);

        active.applyCommand(Command.DROP, field);
        assertEquals(2, active.getRow());
    }

    @Test
    void dropVerticalWhenAlreadyAtBottom() {
        Field field = new Field(5, 3);
        Brick brick = new Brick(Orientation.VERTICAL, '^', '*', '@');
        ActiveBrick active = new ActiveBrick(brick, 0, 2);

        active.applyCommand(Command.DROP, field);
        assertEquals(0, active.getRow());
    }

    @Test
    void cannotPlaceVerticalWhenPartiallyBlocked() {
        Field field = new Field(5, 8);
        field.setCell(1, 2, '*');
        Brick brick = new Brick(Orientation.VERTICAL, '^', '*', '@');
        ActiveBrick active = ActiveBrick.createAtStart(brick, field);

        assertNull(active);
    }

    @Test
    void canPlaceVerticalOnEmptyField() {
        Field field = new Field(5, 8);
        Brick brick = new Brick(Orientation.VERTICAL, '^', '*', '@');
        ActiveBrick active = ActiveBrick.createAtStart(brick, field);

        assertNotNull(active);
        assertTrue(active.canPlace(field));
    }

    @Test
    void placeVerticalOnField() {
        Field field = new Field(5, 8);
        Brick brick = new Brick(Orientation.VERTICAL, '^', '*', '@');
        ActiveBrick active = new ActiveBrick(brick, 5, 2);
        active.placeOnField(field);

        assertEquals('^', field.getCell(5, 2));
        assertEquals('*', field.getCell(6, 2));
        assertEquals('@', field.getCell(7, 2));
    }

    @Test
    void dropHorizontalWithPartialObstacle() {
        Field field = new Field(5, 8);
        field.setCell(5, 2, '@');
        Brick brick = new Brick(Orientation.HORIZONTAL, '^', '*', '~');
        ActiveBrick active = new ActiveBrick(brick, 0, 1);

        active.applyCommand(Command.DROP, field);
        assertEquals(4, active.getRow());
    }

    @Test
    void canMoveDownOneRow() {
        Field field = new Field(5, 8);
        Brick brick = new Brick(Orientation.HORIZONTAL, '^', '^', '*');
        ActiveBrick active = new ActiveBrick(brick, 0, 1);

        assertTrue(active.canMove(field, 1, 0));
    }

    @Test
    void cannotMoveDownAtBottom() {
        Field field = new Field(5, 8);
        Brick brick = new Brick(Orientation.HORIZONTAL, '^', '^', '*');
        ActiveBrick active = new ActiveBrick(brick, 7, 1);

        assertFalse(active.canMove(field, 1, 0));
    }

    @Test
    void verticalBrickCannotMoveDownAtBottom() {
        Field field = new Field(5, 8);
        Brick brick = new Brick(Orientation.VERTICAL, '^', '*', '@');
        ActiveBrick active = new ActiveBrick(brick, 5, 2);

        assertFalse(active.canMove(field, 1, 0));
    }

    @Test
    void getSymbolAtDelegatesToBrick() {
        Brick brick = new Brick(Orientation.HORIZONTAL, '~', '^', '@');
        ActiveBrick active = new ActiveBrick(brick, 0, 0);

        assertEquals('~', active.getSymbolAt(0));
        assertEquals('^', active.getSymbolAt(1));
        assertEquals('@', active.getSymbolAt(2));
    }

    @Test
    void getBrickReturnsSameInstance() {
        Brick brick = new Brick(Orientation.HORIZONTAL, '^', '*', '@');
        ActiveBrick active = new ActiveBrick(brick, 0, 0);

        assertSame(brick, active.getBrick());
    }

    @Test
    void consecutiveMovesThenDrop() {
        Field field = new Field(5, 8);
        Brick brick = new Brick(Orientation.HORIZONTAL, '^', '*', '@');
        ActiveBrick active = new ActiveBrick(brick, 0, 1);

        active.applyCommand(Command.LEFT, field);
        assertEquals(0, active.getCol());
        active.applyCommand(Command.RIGHT, field);
        assertEquals(1, active.getCol());
        active.applyCommand(Command.DROP, field);
        assertEquals(7, active.getRow());
    }

    @Test
    void createAtStartFieldExactlyHeight3ForVertical() {
        Field field = new Field(3, 3);
        Brick brick = new Brick(Orientation.VERTICAL, '^', '*', '@');
        ActiveBrick active = ActiveBrick.createAtStart(brick, field);

        assertNotNull(active);
        assertEquals(0, active.getRow());
        assertEquals(1, active.getCol());
        assertFalse(active.canMove(field, 1, 0));
    }

    @Test
    void occupiedCellsListIsImmutable() {
        Brick brick = new Brick(Orientation.HORIZONTAL, '^', '*', '@');
        ActiveBrick active = new ActiveBrick(brick, 0, 0);

        List<Position> cells = active.getOccupiedCells();
        assertThrows(UnsupportedOperationException.class, () -> cells.add(new Position(9, 9)));
    }

    // ── Rotation tests ─────────────────────────────────────────────────

    @Test
    void rotateHorizontalToVertical() {
        Field field = new Field(5, 8);
        Brick brick = new Brick(Orientation.HORIZONTAL, '^', '*', '@');
        ActiveBrick active = new ActiveBrick(brick, 0, 1);

        assertTrue(active.canRotate(field));
        active.applyCommand(Command.TURN, field);
        assertTrue(active.getBrick().isVertical());
        assertEquals(0, active.getRow());
        assertEquals(1, active.getCol());

        List<Position> cells = active.getOccupiedCells();
        assertEquals(new Position(0, 1), cells.get(0));
        assertEquals(new Position(1, 1), cells.get(1));
        assertEquals(new Position(2, 1), cells.get(2));
    }

    @Test
    void rotateVerticalToHorizontal() {
        Field field = new Field(5, 8);
        Brick brick = new Brick(Orientation.VERTICAL, '^', '*', '@');
        ActiveBrick active = new ActiveBrick(brick, 0, 1);

        assertTrue(active.canRotate(field));
        active.applyCommand(Command.TURN, field);
        assertTrue(active.getBrick().isHorizontal());

        List<Position> cells = active.getOccupiedCells();
        assertEquals(new Position(0, 1), cells.get(0));
        assertEquals(new Position(0, 2), cells.get(1));
        assertEquals(new Position(0, 3), cells.get(2));
    }

    @Test
    void rotatePreservesSymbols() {
        Field field = new Field(5, 8);
        Brick brick = new Brick(Orientation.HORIZONTAL, '~', '^', '*');
        ActiveBrick active = new ActiveBrick(brick, 0, 0);

        active.applyCommand(Command.TURN, field);
        assertEquals('~', active.getSymbolAt(0));
        assertEquals('^', active.getSymbolAt(1));
        assertEquals('*', active.getSymbolAt(2));
    }

    @Test
    void rotateBlockedByRightWall() {
        // V→H at col 3 on a 5-wide field: needs cols 3,4,5 but col 5 is out of bounds
        Field field = new Field(5, 8);
        Brick brick = new Brick(Orientation.VERTICAL, '^', '*', '@');
        ActiveBrick active = new ActiveBrick(brick, 0, 3);

        assertFalse(active.canRotate(field));
        active.applyCommand(Command.TURN, field);
        assertTrue(active.getBrick().isVertical());
    }

    @Test
    void rotateBlockedByBottomWall() {
        // H→V at row 6 on an 8-tall field: needs rows 6,7,8 but row 8 is out of bounds
        Field field = new Field(5, 8);
        Brick brick = new Brick(Orientation.HORIZONTAL, '^', '*', '@');
        ActiveBrick active = new ActiveBrick(brick, 6, 1);

        assertFalse(active.canRotate(field));
        active.applyCommand(Command.TURN, field);
        assertTrue(active.getBrick().isHorizontal());
    }

    @Test
    void rotateBlockedByExistingBrick() {
        // H→V blocked because cell below anchor is occupied
        Field field = new Field(5, 8);
        field.setCell(1, 1, '*');
        Brick brick = new Brick(Orientation.HORIZONTAL, '^', '*', '@');
        ActiveBrick active = new ActiveBrick(brick, 0, 1);

        assertFalse(active.canRotate(field));
        active.applyCommand(Command.TURN, field);
        assertTrue(active.getBrick().isHorizontal());
    }

    @Test
    void rotateRoundTrip() {
        Field field = new Field(5, 8);
        Brick brick = new Brick(Orientation.HORIZONTAL, '^', '*', '@');
        ActiveBrick active = new ActiveBrick(brick, 0, 1);

        active.applyCommand(Command.TURN, field);
        assertTrue(active.getBrick().isVertical());
        active.applyCommand(Command.TURN, field);
        assertTrue(active.getBrick().isHorizontal());
        assertEquals(0, active.getRow());
        assertEquals(1, active.getCol());
    }

    @Test
    void rotateThenDrop() {
        Field field = new Field(5, 8);
        Brick brick = new Brick(Orientation.HORIZONTAL, '^', '*', '@');
        ActiveBrick active = new ActiveBrick(brick, 0, 1);

        active.applyCommand(Command.TURN, field);
        assertTrue(active.getBrick().isVertical());
        active.applyCommand(Command.DROP, field);
        assertEquals(5, active.getRow());
    }

    @Test
    void rotateAtExactBottomEdge() {
        // H→V at row 5 on 8-tall: needs rows 5,6,7 — just fits
        Field field = new Field(5, 8);
        Brick brick = new Brick(Orientation.HORIZONTAL, '^', '*', '@');
        ActiveBrick active = new ActiveBrick(brick, 5, 1);

        assertTrue(active.canRotate(field));
        active.applyCommand(Command.TURN, field);
        assertTrue(active.getBrick().isVertical());
    }

    @Test
    void rotateAtExactRightEdge() {
        // V→H at col 2 on 5-wide: needs cols 2,3,4 — just fits
        Field field = new Field(5, 8);
        Brick brick = new Brick(Orientation.VERTICAL, '^', '*', '@');
        ActiveBrick active = new ActiveBrick(brick, 0, 2);

        assertTrue(active.canRotate(field));
        active.applyCommand(Command.TURN, field);
        assertTrue(active.getBrick().isHorizontal());
    }

    @Test
    void rotatePlaceOnFieldUsesNewOrientation() {
        Field field = new Field(5, 8);
        Brick brick = new Brick(Orientation.HORIZONTAL, '^', '*', '@');
        ActiveBrick active = new ActiveBrick(brick, 5, 1);

        active.applyCommand(Command.TURN, field);
        active.placeOnField(field);
        assertEquals('^', field.getCell(5, 1));
        assertEquals('*', field.getCell(6, 1));
        assertEquals('@', field.getCell(7, 1));
    }
}
