package com.gic.assessment.match3.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CommandTest {

    @Test
    void fromCharUppercase() {
        assertEquals(Command.LEFT, Command.fromChar('L'));
        assertEquals(Command.RIGHT, Command.fromChar('R'));
        assertEquals(Command.DROP, Command.fromChar('D'));
        assertEquals(Command.TURN, Command.fromChar('T'));
    }

    @Test
    void fromCharLowercase() {
        assertEquals(Command.LEFT, Command.fromChar('l'));
        assertEquals(Command.RIGHT, Command.fromChar('r'));
        assertEquals(Command.DROP, Command.fromChar('d'));
        assertEquals(Command.TURN, Command.fromChar('t'));
    }

    @Test
    void fromCharInvalidReturnsNull() {
        assertNull(Command.fromChar('X'));
        assertNull(Command.fromChar('0'));
        assertNull(Command.fromChar(' '));
        assertNull(Command.fromChar('\n'));
    }
}
