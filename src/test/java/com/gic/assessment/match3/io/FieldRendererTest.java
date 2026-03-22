package com.gic.assessment.match3.io;

import com.gic.assessment.match3.model.ActiveBrick;
import com.gic.assessment.match3.model.Brick;
import com.gic.assessment.match3.model.Field;
import com.gic.assessment.match3.model.Orientation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FieldRendererTest {

    @Test
    void renderEmptyField() {
        Field field = new Field(5, 3);
        String rendered = FieldRenderer.render(field, null);

        String expected = String.join("\n",
                "1\t| . . . . . |",
                "2\t| . . . . . |",
                "3\t| . . . . . |"
        );
        assertEquals(expected, rendered);
    }

    @Test
    void renderFieldWithActiveBrick() {
        Field field = new Field(5, 3);
        Brick brick = new Brick(Orientation.HORIZONTAL, '^', '^', '*');
        ActiveBrick active = new ActiveBrick(brick, 0, 1);

        String rendered = FieldRenderer.render(field, active);
        assertTrue(rendered.contains("| . ^ ^ * . |"));
    }

    @Test
    void renderFieldWithStationaryCells() {
        Field field = new Field(5, 3);
        field.setCell(2, 0, '@');
        field.setCell(2, 1, '~');

        String rendered = FieldRenderer.render(field, null);
        assertTrue(rendered.contains("| @ ~ . . . |"));
    }

    @Test
    void renderFieldWithActiveBrickAndStationaryCells() {
        Field field = new Field(5, 3);
        field.setCell(2, 1, '^');
        field.setCell(2, 2, '^');
        field.setCell(2, 3, '*');

        Brick brick = new Brick(Orientation.VERTICAL, '*', '@', '^');
        ActiveBrick active = new ActiveBrick(brick, 0, 2);

        String rendered = FieldRenderer.render(field, active);
        assertTrue(rendered.contains("| . . * . . |")); // row 0
        assertTrue(rendered.contains("| . . @ . . |")); // row 1
    }

    @Test
    void renderSingleCellField() {
        Field field = new Field(1, 1);
        String rendered = FieldRenderer.render(field, null);
        assertEquals("1\t| . |", rendered);
    }

    @Test
    void renderSingleCellFieldWithSymbol() {
        Field field = new Field(1, 1);
        field.setCell(0, 0, '@');
        String rendered = FieldRenderer.render(field, null);
        assertEquals("1\t| @ |", rendered);
    }

    @Test
    void renderDoubleDigitRowNumbers() {
        Field field = new Field(3, 12);
        String rendered = FieldRenderer.render(field, null);
        assertTrue(rendered.contains(" 1\t| . . . |"));
        assertTrue(rendered.contains(" 9\t| . . . |"));
        assertTrue(rendered.contains("10\t| . . . |"));
        assertTrue(rendered.contains("12\t| . . . |"));
    }

    @Test
    void renderVerticalActiveBrick() {
        Field field = new Field(3, 5);
        Brick brick = new Brick(Orientation.VERTICAL, '~', '^', '@');
        ActiveBrick active = new ActiveBrick(brick, 1, 1);

        String rendered = FieldRenderer.render(field, active);
        String[] lines = rendered.split("\n");
        assertEquals("1\t| . . . |", lines[0]);
        assertEquals("2\t| . ~ . |", lines[1]);
        assertEquals("3\t| . ^ . |", lines[2]);
        assertEquals("4\t| . @ . |", lines[3]);
        assertEquals("5\t| . . . |", lines[4]);
    }

    @Test
    void renderAllSymbolTypes() {
        Field field = new Field(4, 1);
        field.setCell(0, 0, '~');
        field.setCell(0, 1, '^');
        field.setCell(0, 2, '*');
        field.setCell(0, 3, '@');

        String rendered = FieldRenderer.render(field, null);
        assertEquals("1\t| ~ ^ * @ |", rendered);
    }

    @Test
    void renderActiveBrickOverlaysField() {
        Field field = new Field(5, 3);
        field.setCell(0, 1, '~');
        field.setCell(0, 2, '~');
        field.setCell(0, 3, '~');

        Brick brick = new Brick(Orientation.HORIZONTAL, '^', '*', '@');
        ActiveBrick active = new ActiveBrick(brick, 0, 1);

        String rendered = FieldRenderer.render(field, active);
        assertTrue(rendered.contains("| . ^ * @ . |"));
    }

    @Test
    void renderNoTrailingNewline() {
        Field field = new Field(3, 3);
        String rendered = FieldRenderer.render(field, null);
        assertFalse(rendered.endsWith("\n"));
    }

    @Test
    void renderWithNextBrickAppendsMiniPreview() {
        Field field = new Field(3, 3);
        Brick next = Brick.tShape('*', '@', '^', '~');
        String rendered = FieldRenderer.render(field, null, next);
        assertTrue(rendered.contains("\n\nNext:\n"));
        assertTrue(rendered.endsWith("@ ^ ~")); // bottom row of mini T (3 cells wide)
        assertTrue(rendered.contains(". * ."));
    }

    @Test
    void renderLShapeOnField() {
        Field field = new Field(5, 4);
        Brick brick = Brick.lShape('^', '^', '*', '@');
        ActiveBrick active = new ActiveBrick(brick, 0, 1);
        String rendered = FieldRenderer.render(field, active);
        assertTrue(rendered.contains("| . ^ . . . |"));
        assertTrue(rendered.contains("| . * @ . . |"));
    }
}
