package com.gic.assessment.match3.io;

import com.gic.assessment.match3.model.Brick;
import com.gic.assessment.match3.model.Command;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InputParserTest {

    @Test
    void parseInitInputBasic() {
        InputParser.GameConfig config = InputParser.parseInitInput("5 8 H^^* V*@^");
        assertEquals(5, config.width());
        assertEquals(8, config.height());
        assertEquals(2, config.bricks().size());

        Brick first = config.bricks().get(0);
        assertTrue(first.isHorizontal());
        assertEquals('^', first.symbolAt(0));
        assertEquals('^', first.symbolAt(1));
        assertEquals('*', first.symbolAt(2));

        Brick second = config.bricks().get(1);
        assertTrue(second.isVertical());
        assertEquals('*', second.symbolAt(0));
        assertEquals('@', second.symbolAt(1));
        assertEquals('^', second.symbolAt(2));
    }

    @Test
    void parseInitInputNoBricks() {
        InputParser.GameConfig config = InputParser.parseInitInput("10 10");
        assertEquals(10, config.width());
        assertEquals(10, config.height());
        assertTrue(config.bricks().isEmpty());
    }

    @Test
    void parseInitInputMaxFiveBricks() {
        InputParser.GameConfig config = InputParser.parseInitInput(
                "5 5 H^^^ H*** H~~~ H@@@ H^^* H***");
        assertEquals(5, config.bricks().size());
    }

    @Test
    void parseInitInputExtraWhitespace() {
        InputParser.GameConfig config = InputParser.parseInitInput("  5  8  H^^*  ");
        assertEquals(5, config.width());
        assertEquals(8, config.height());
        assertEquals(1, config.bricks().size());
    }

    @Test
    void parseInitInputEmptyThrows() {
        assertThrows(IllegalArgumentException.class, () -> InputParser.parseInitInput(""));
        assertThrows(IllegalArgumentException.class, () -> InputParser.parseInitInput("  "));
    }

    @Test
    void parseInitInputMissingHeightThrows() {
        assertThrows(IllegalArgumentException.class, () -> InputParser.parseInitInput("5"));
    }

    @Test
    void parseBrickValid() {
        Brick brick = InputParser.parseBrick("H^^*");
        assertTrue(brick.isHorizontal());
        assertEquals('^', brick.symbolAt(0));
    }

    @Test
    void parseBrickInvalidLength() {
        assertThrows(IllegalArgumentException.class, () -> InputParser.parseBrick("H^^"));
        assertThrows(IllegalArgumentException.class, () -> InputParser.parseBrick("H^^**"));
    }

    @Test
    void parseCommandsBasic() {
        List<Command> cmds = InputParser.parseCommands("LR");
        assertEquals(2, cmds.size());
        assertEquals(Command.LEFT, cmds.get(0));
        assertEquals(Command.RIGHT, cmds.get(1));
    }

    @Test
    void parseCommandsOnlyFirstTwo() {
        List<Command> cmds = InputParser.parseCommands("LLR");
        assertEquals(2, cmds.size());
        assertEquals(Command.LEFT, cmds.get(0));
        assertEquals(Command.LEFT, cmds.get(1));
    }

    @Test
    void parseCommandsDrop() {
        List<Command> cmds = InputParser.parseCommands("DR");
        assertEquals(2, cmds.size());
        assertEquals(Command.DROP, cmds.get(0));
        assertEquals(Command.RIGHT, cmds.get(1));
    }

    @Test
    void parseCommandsEmpty() {
        assertTrue(InputParser.parseCommands("").isEmpty());
        assertTrue(InputParser.parseCommands(null).isEmpty());
    }

    @Test
    void parseCommandsSkipsInvalidChars() {
        List<Command> cmds = InputParser.parseCommands("xLyRz");
        assertEquals(2, cmds.size());
        assertEquals(Command.LEFT, cmds.get(0));
        assertEquals(Command.RIGHT, cmds.get(1));
    }

    @Test
    void parseCommandsSingleCommand() {
        List<Command> cmds = InputParser.parseCommands("D");
        assertEquals(1, cmds.size());
        assertEquals(Command.DROP, cmds.get(0));
    }

    @Test
    void parseCommandsTurn() {
        List<Command> cmds = InputParser.parseCommands("TL");
        assertEquals(2, cmds.size());
        assertEquals(Command.TURN, cmds.get(0));
        assertEquals(Command.LEFT, cmds.get(1));
    }

    @Test
    void parseCommandsTurnLowercase() {
        List<Command> cmds = InputParser.parseCommands("t");
        assertEquals(1, cmds.size());
        assertEquals(Command.TURN, cmds.get(0));
    }

    @Test
    void parseCommandsAllInvalidChars() {
        List<Command> cmds = InputParser.parseCommands("XYZ123");
        assertTrue(cmds.isEmpty());
    }

    @Test
    void parseCommandsLowercaseValid() {
        List<Command> cmds = InputParser.parseCommands("lr");
        assertEquals(2, cmds.size());
        assertEquals(Command.LEFT, cmds.get(0));
        assertEquals(Command.RIGHT, cmds.get(1));
    }

    @Test
    void parseCommandsTwoDrops() {
        List<Command> cmds = InputParser.parseCommands("DD");
        assertEquals(2, cmds.size());
        assertEquals(Command.DROP, cmds.get(0));
        assertEquals(Command.DROP, cmds.get(1));
    }

    @Test
    void parseInitInputNonNumericWidthThrows() {
        assertThrows(NumberFormatException.class, () -> InputParser.parseInitInput("abc 8"));
    }

    @Test
    void parseInitInputNonNumericHeightThrows() {
        assertThrows(NumberFormatException.class, () -> InputParser.parseInitInput("5 xyz"));
    }

    @Test
    void parseInitInputNegativeDimensionsThrows() {
        assertThrows(IllegalArgumentException.class, () -> InputParser.parseInitInput("-1 5"));
    }

    @Test
    void parseInitInputZeroDimensionsThrows() {
        assertThrows(IllegalArgumentException.class, () -> InputParser.parseInitInput("0 0"));
    }

    @Test
    void parseInitInputNullThrows() {
        assertThrows(IllegalArgumentException.class, () -> InputParser.parseInitInput(null));
    }

    @Test
    void parseBrickLowercaseOrientation() {
        Brick brick = InputParser.parseBrick("h^^*");
        assertTrue(brick.isHorizontal());
    }

    @Test
    void parseBrickVertical() {
        Brick brick = InputParser.parseBrick("V*@^");
        assertTrue(brick.isVertical());
        assertEquals('*', brick.symbolAt(0));
        assertEquals('@', brick.symbolAt(1));
        assertEquals('^', brick.symbolAt(2));
    }

    @Test
    void parseBrickInvalidOrientation() {
        assertThrows(IllegalArgumentException.class, () -> InputParser.parseBrick("X^^*"));
    }

    @Test
    void parseBrickNullThrows() {
        assertThrows(IllegalArgumentException.class, () -> InputParser.parseBrick(null));
    }

    @Test
    void parseBrickInvalidSymbol() {
        assertThrows(IllegalArgumentException.class, () -> InputParser.parseBrick("HABC"));
    }

    @Test
    void parseInitInputExactlyFiveBricks() {
        InputParser.GameConfig config = InputParser.parseInitInput(
                "5 5 H^^^ H*** H~~~ H@@@ V^^^");
        assertEquals(5, config.bricks().size());
    }

    @Test
    void parseInitInputSingleBrick() {
        InputParser.GameConfig config = InputParser.parseInitInput("3 3 V^^^");
        assertEquals(1, config.bricks().size());
        assertTrue(config.bricks().get(0).isVertical());
    }

    @Test
    void parseInitInputWidthAndHeightOnly() {
        InputParser.GameConfig config = InputParser.parseInitInput("1 1");
        assertEquals(1, config.width());
        assertEquals(1, config.height());
        assertTrue(config.bricks().isEmpty());
    }
}
