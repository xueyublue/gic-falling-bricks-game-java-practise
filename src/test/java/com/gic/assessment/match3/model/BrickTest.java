package com.gic.assessment.match3.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BrickTest {

    @Test
    void createHorizontalBrick() {
        Brick brick = new Brick(Orientation.HORIZONTAL, '^', '^', '*');
        assertTrue(brick.isHorizontal());
        assertFalse(brick.isVertical());
        assertEquals('^', brick.symbolAt(0));
        assertEquals('^', brick.symbolAt(1));
        assertEquals('*', brick.symbolAt(2));
    }

    @Test
    void createVerticalBrick() {
        Brick brick = new Brick(Orientation.VERTICAL, '*', '@', '^');
        assertTrue(brick.isVertical());
        assertFalse(brick.isHorizontal());
    }

    @Test
    void invalidSymbolThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> new Brick(Orientation.HORIZONTAL, 'A', '^', '*'));
    }

    @Test
    void symbolAtOutOfBoundsThrows() {
        Brick brick = new Brick(Orientation.HORIZONTAL, '^', '^', '*');
        assertThrows(IndexOutOfBoundsException.class, () -> brick.symbolAt(3));
        assertThrows(IndexOutOfBoundsException.class, () -> brick.symbolAt(-1));
    }

    @Test
    void toStringFormat() {
        Brick h = new Brick(Orientation.HORIZONTAL, '^', '^', '*');
        assertEquals("H^^*", h.toString());

        Brick v = new Brick(Orientation.VERTICAL, '*', '@', '^');
        assertEquals("V*@^", v.toString());
    }

    @Test
    void allAllowedSymbols() {
        assertDoesNotThrow(() -> new Brick(Orientation.HORIZONTAL, '~', '^', '*'));
        assertDoesNotThrow(() -> new Brick(Orientation.HORIZONTAL, '@', '~', '^'));
    }

    @Test
    void invalidSymbolInSecondPosition() {
        assertThrows(IllegalArgumentException.class,
                () -> new Brick(Orientation.HORIZONTAL, '^', 'B', '*'));
    }

    @Test
    void invalidSymbolInThirdPosition() {
        assertThrows(IllegalArgumentException.class,
                () -> new Brick(Orientation.HORIZONTAL, '^', '*', '!'));
    }

    @Test
    void dotIsNotAllowedSymbol() {
        assertThrows(IllegalArgumentException.class,
                () -> new Brick(Orientation.HORIZONTAL, '.', '.', '.'));
    }

    @Test
    void allSameSymbols() {
        Brick brick = new Brick(Orientation.HORIZONTAL, '*', '*', '*');
        assertEquals('*', brick.symbolAt(0));
        assertEquals('*', brick.symbolAt(1));
        assertEquals('*', brick.symbolAt(2));
        assertEquals("H***", brick.toString());
    }

    @Test
    void rotatedFlipsOrientation() {
        Brick h = new Brick(Orientation.HORIZONTAL, '^', '*', '@');
        Brick v = h.rotated();
        assertTrue(v.isVertical());
        assertEquals('^', v.symbolAt(0));
        assertEquals('*', v.symbolAt(1));
        assertEquals('@', v.symbolAt(2));
    }

    @Test
    void rotatedRoundTrip() {
        Brick original = new Brick(Orientation.HORIZONTAL, '^', '*', '@');
        assertEquals(original, original.rotated().rotated());
    }

    @Test
    void rotatedVerticalToHorizontal() {
        Brick v = new Brick(Orientation.VERTICAL, '~', '^', '*');
        Brick h = v.rotated();
        assertTrue(h.isHorizontal());
        assertEquals('~', h.symbolAt(0));
        assertEquals('^', h.symbolAt(1));
        assertEquals('*', h.symbolAt(2));
    }

    @Test
    void recordEquality() {
        Brick a = new Brick(Orientation.HORIZONTAL, '^', '^', '*');
        Brick b = new Brick(Orientation.HORIZONTAL, '^', '^', '*');
        Brick c = new Brick(Orientation.VERTICAL, '^', '^', '*');
        assertEquals(a, b);
        assertNotEquals(a, c);
    }
}
