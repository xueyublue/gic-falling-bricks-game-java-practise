package com.gic.assessment.match3.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrientationTest {

    @Test
    void fromCharUppercase() {
        assertEquals(Orientation.HORIZONTAL, Orientation.fromChar('H'));
        assertEquals(Orientation.VERTICAL, Orientation.fromChar('V'));
    }

    @Test
    void fromCharLowercase() {
        assertEquals(Orientation.HORIZONTAL, Orientation.fromChar('h'));
        assertEquals(Orientation.VERTICAL, Orientation.fromChar('v'));
    }

    @Test
    void fromCharInvalidThrows() {
        assertThrows(IllegalArgumentException.class, () -> Orientation.fromChar('X'));
        assertThrows(IllegalArgumentException.class, () -> Orientation.fromChar('Z'));
        assertThrows(IllegalArgumentException.class, () -> Orientation.fromChar(' '));
    }
}
