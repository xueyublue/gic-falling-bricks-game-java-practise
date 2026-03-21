package com.gic.assessment.match3.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PositionTest {

    @Test
    void accessors() {
        Position pos = new Position(3, 5);
        assertEquals(3, pos.row());
        assertEquals(5, pos.col());
    }

    @Test
    void offsetCreatesNewPosition() {
        Position pos = new Position(2, 3);
        Position moved = pos.offset(1, -1);

        assertEquals(3, moved.row());
        assertEquals(2, moved.col());
        assertEquals(2, pos.row());
        assertEquals(3, pos.col());
    }

    @Test
    void offsetByZero() {
        Position pos = new Position(4, 7);
        Position same = pos.offset(0, 0);

        assertEquals(pos, same);
    }

    @Test
    void recordEquality() {
        Position a = new Position(1, 2);
        Position b = new Position(1, 2);
        Position c = new Position(2, 1);

        assertEquals(a, b);
        assertNotEquals(a, c);
    }

    @Test
    void recordToString() {
        Position pos = new Position(3, 7);
        String str = pos.toString();
        assertTrue(str.contains("3"));
        assertTrue(str.contains("7"));
    }
}
