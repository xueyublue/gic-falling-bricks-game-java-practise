package com.gic.assessment.match3.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FieldTest {

    @Test
    void newFieldIsEmpty() {
        Field field = new Field(5, 8);
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 5; c++) {
                assertEquals(Field.EMPTY, field.getCell(r, c));
                assertTrue(field.isEmpty(r, c));
                assertFalse(field.isOccupied(r, c));
            }
        }
    }

    @Test
    void setAndGetCell() {
        Field field = new Field(5, 8);
        field.setCell(3, 2, '^');
        assertEquals('^', field.getCell(3, 2));
        assertTrue(field.isOccupied(3, 2));
        assertFalse(field.isEmpty(3, 2));
    }

    @Test
    void boundsChecking() {
        Field field = new Field(5, 8);
        assertTrue(field.isInBounds(0, 0));
        assertTrue(field.isInBounds(7, 4));
        assertFalse(field.isInBounds(-1, 0));
        assertFalse(field.isInBounds(8, 0));
        assertFalse(field.isInBounds(0, 5));
        assertFalse(field.isInBounds(0, -1));
    }

    @Test
    void clearResetsField() {
        Field field = new Field(3, 3);
        field.setCell(1, 1, '*');
        field.clear();
        assertEquals(Field.EMPTY, field.getCell(1, 1));
    }

    @Test
    void invalidDimensionsThrow() {
        assertThrows(IllegalArgumentException.class, () -> new Field(0, 5));
        assertThrows(IllegalArgumentException.class, () -> new Field(5, 0));
        assertThrows(IllegalArgumentException.class, () -> new Field(-1, -1));
    }

    @Test
    void minimumField1x1() {
        Field field = new Field(1, 1);
        assertEquals(1, field.getWidth());
        assertEquals(1, field.getHeight());
        assertTrue(field.isEmpty(0, 0));
        field.setCell(0, 0, '^');
        assertEquals('^', field.getCell(0, 0));
        assertTrue(field.isOccupied(0, 0));
    }

    @Test
    void cornerCells() {
        Field field = new Field(5, 8);
        field.setCell(0, 0, '~');
        field.setCell(0, 4, '^');
        field.setCell(7, 0, '*');
        field.setCell(7, 4, '@');
        assertEquals('~', field.getCell(0, 0));
        assertEquals('^', field.getCell(0, 4));
        assertEquals('*', field.getCell(7, 0));
        assertEquals('@', field.getCell(7, 4));
    }

    @Test
    void isEmptyReturnsFalseForOutOfBounds() {
        Field field = new Field(3, 3);
        assertFalse(field.isEmpty(-1, 0));
        assertFalse(field.isEmpty(0, -1));
        assertFalse(field.isEmpty(3, 0));
        assertFalse(field.isEmpty(0, 3));
    }

    @Test
    void isOccupiedReturnsFalseForOutOfBounds() {
        Field field = new Field(3, 3);
        field.setCell(0, 0, '^');
        assertFalse(field.isOccupied(-1, 0));
        assertFalse(field.isOccupied(0, 3));
    }

    @Test
    void clearAfterMultipleSymbols() {
        Field field = new Field(3, 3);
        for (int r = 0; r < 3; r++)
            for (int c = 0; c < 3; c++)
                field.setCell(r, c, '*');
        field.clear();
        for (int r = 0; r < 3; r++)
            for (int c = 0; c < 3; c++)
                assertTrue(field.isEmpty(r, c));
    }

    @Test
    void overwriteCell() {
        Field field = new Field(3, 3);
        field.setCell(1, 1, '^');
        assertEquals('^', field.getCell(1, 1));
        field.setCell(1, 1, '*');
        assertEquals('*', field.getCell(1, 1));
    }

    @Test
    void getCellOutOfBoundsThrows() {
        Field field = new Field(3, 3);
        assertThrows(IndexOutOfBoundsException.class, () -> field.getCell(-1, 0));
        assertThrows(IndexOutOfBoundsException.class, () -> field.getCell(0, 3));
        assertThrows(IndexOutOfBoundsException.class, () -> field.getCell(3, 0));
        assertThrows(IndexOutOfBoundsException.class, () -> field.getCell(0, -1));
    }

    @Test
    void setCellOutOfBoundsThrows() {
        Field field = new Field(3, 3);
        assertThrows(IndexOutOfBoundsException.class, () -> field.setCell(-1, 0, '^'));
        assertThrows(IndexOutOfBoundsException.class, () -> field.setCell(0, 3, '^'));
        assertThrows(IndexOutOfBoundsException.class, () -> field.setCell(3, 0, '^'));
    }

    @Test
    void boundsErrorMessageContainsCoordinates() {
        Field field = new Field(5, 8);
        IndexOutOfBoundsException ex = assertThrows(
                IndexOutOfBoundsException.class, () -> field.getCell(10, 3));
        assertTrue(ex.getMessage().contains("10"));
        assertTrue(ex.getMessage().contains("3"));
    }

    @Test
    void gravityDropSingleCell() {
        Field field = new Field(3, 3);
        field.setCell(0, 0, '*');
        field.applyGravity();
        assertEquals('*', field.getCell(2, 0));
    }

    @Test
    void gravityDropMultipleCells() {
        Field field = new Field(5, 5);
        field.setCell(1, 0, '^');
        field.setCell(2, 0, '@');
        field.setCell(3, 0, '*');
        field.applyGravity();
        assertEquals('^', field.getCell(2, 0));
        assertEquals('@', field.getCell(3, 0));
        assertEquals('*', field.getCell(4, 0));
    }
}
