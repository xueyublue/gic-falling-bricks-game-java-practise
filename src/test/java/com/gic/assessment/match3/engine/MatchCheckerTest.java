package com.gic.assessment.match3.engine;

import com.gic.assessment.match3.model.Field;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MatchCheckerTest {

    @Test
    void horizontalMatchOfThree() {
        Field field = new Field(5, 5);
        field.setCell(4, 1, '^');
        field.setCell(4, 2, '^');
        field.setCell(4, 3, '^');

        int cleared = MatchChecker.checkAndClear(field);
        assertEquals(3, cleared);
        assertEquals(Field.EMPTY, field.getCell(4, 1));
        assertEquals(Field.EMPTY, field.getCell(4, 2));
        assertEquals(Field.EMPTY, field.getCell(4, 3));
    }

    @Test
    void verticalMatchOfThree() {
        Field field = new Field(5, 5);
        field.setCell(2, 1, '*');
        field.setCell(3, 1, '*');
        field.setCell(4, 1, '*');

        int cleared = MatchChecker.checkAndClear(field);
        assertEquals(3, cleared);
        assertEquals(Field.EMPTY, field.getCell(2, 1));
        assertEquals(Field.EMPTY, field.getCell(3, 1));
        assertEquals(Field.EMPTY, field.getCell(4, 1));
    }

    @Test
    void noMatchOfTwo() {
        Field field = new Field(5, 5);
        field.setCell(4, 1, '^');
        field.setCell(4, 2, '^');

        int cleared = MatchChecker.checkAndClear(field);
        assertEquals(0, cleared);
        assertEquals('^', field.getCell(4, 1));
        assertEquals('^', field.getCell(4, 2));
    }

    @Test
    void matchOfFour() {
        Field field = new Field(5, 5);
        field.setCell(4, 0, '@');
        field.setCell(4, 1, '@');
        field.setCell(4, 2, '@');
        field.setCell(4, 3, '@');

        int cleared = MatchChecker.checkAndClear(field);
        assertEquals(4, cleared);
    }

    @Test
    void multipleMatchesAtOnce() {
        Field field = new Field(5, 5);
        // Horizontal match
        field.setCell(4, 0, '^');
        field.setCell(4, 1, '^');
        field.setCell(4, 2, '^');
        // Vertical match
        field.setCell(0, 4, '*');
        field.setCell(1, 4, '*');
        field.setCell(2, 4, '*');

        int cleared = MatchChecker.checkAndClear(field);
        assertEquals(6, cleared);
    }

    @Test
    void crossMatch() {
        Field field = new Field(5, 5);
        // Horizontal: row 2, cols 0-2
        field.setCell(2, 0, '^');
        field.setCell(2, 1, '^');
        field.setCell(2, 2, '^');
        // Vertical: rows 0-2, col 1 (overlaps at (2,1))
        field.setCell(0, 1, '^');
        field.setCell(1, 1, '^');
        // (2,1) already set

        int cleared = MatchChecker.checkAndClear(field);
        assertEquals(5, cleared);
    }

    @Test
    void noMatchOnEmptyField() {
        Field field = new Field(5, 5);
        int cleared = MatchChecker.checkAndClear(field);
        assertEquals(0, cleared);
    }

    @Test
    void differentSymbolsDontMatch() {
        Field field = new Field(5, 5);
        field.setCell(4, 0, '^');
        field.setCell(4, 1, '*');
        field.setCell(4, 2, '^');

        int cleared = MatchChecker.checkAndClear(field);
        assertEquals(0, cleared);
    }

    @Test
    void fullRowMatchClearsEntireRow() {
        Field field = new Field(5, 3);
        for (int c = 0; c < 5; c++) {
            field.setCell(2, c, '^');
        }
        int cleared = MatchChecker.checkAndClear(field);
        assertEquals(5, cleared);
        for (int c = 0; c < 5; c++) {
            assertEquals(Field.EMPTY, field.getCell(2, c));
        }
    }

    @Test
    void fullColumnMatchClearsEntireColumn() {
        Field field = new Field(3, 5);
        for (int r = 0; r < 5; r++) {
            field.setCell(r, 1, '*');
        }
        int cleared = MatchChecker.checkAndClear(field);
        assertEquals(5, cleared);
        for (int r = 0; r < 5; r++) {
            assertEquals(Field.EMPTY, field.getCell(r, 1));
        }
    }

    @Test
    void multipleHorizontalMatchesDifferentRows() {
        Field field = new Field(5, 5);
        field.setCell(1, 0, '^');
        field.setCell(1, 1, '^');
        field.setCell(1, 2, '^');
        field.setCell(3, 1, '*');
        field.setCell(3, 2, '*');
        field.setCell(3, 3, '*');

        int cleared = MatchChecker.checkAndClear(field);
        assertEquals(6, cleared);
    }

    @Test
    void multipleVerticalMatchesDifferentColumns() {
        Field field = new Field(5, 5);
        field.setCell(0, 0, '^');
        field.setCell(1, 0, '^');
        field.setCell(2, 0, '^');
        field.setCell(1, 4, '@');
        field.setCell(2, 4, '@');
        field.setCell(3, 4, '@');

        int cleared = MatchChecker.checkAndClear(field);
        assertEquals(6, cleared);
    }

    @Test
    void tShapeMatch() {
        Field field = new Field(5, 5);
        // Horizontal: row 2, cols 0-2
        field.setCell(2, 0, '^');
        field.setCell(2, 1, '^');
        field.setCell(2, 2, '^');
        // Vertical continuing from (2,1): rows 2-4
        field.setCell(3, 1, '^');
        field.setCell(4, 1, '^');

        int cleared = MatchChecker.checkAndClear(field);
        assertEquals(5, cleared);
    }

    @Test
    void adjacentRunsOfDifferentSymbols() {
        Field field = new Field(6, 1);
        field.setCell(0, 0, '^');
        field.setCell(0, 1, '^');
        field.setCell(0, 2, '^');
        field.setCell(0, 3, '*');
        field.setCell(0, 4, '*');
        field.setCell(0, 5, '*');

        int cleared = MatchChecker.checkAndClear(field);
        assertEquals(6, cleared);
    }

    @Test
    void twoInRowTwoInColumnNoMatch() {
        Field field = new Field(3, 3);
        field.setCell(0, 0, '^');
        field.setCell(0, 1, '^');
        field.setCell(1, 0, '^');

        int cleared = MatchChecker.checkAndClear(field);
        assertEquals(0, cleared);
    }

    @Test
    void minimumFieldSize3x3WithMatch() {
        Field field = new Field(3, 3);
        field.setCell(0, 0, '@');
        field.setCell(0, 1, '@');
        field.setCell(0, 2, '@');

        int cleared = MatchChecker.checkAndClear(field);
        assertEquals(3, cleared);
    }

    @Test
    void runBrokenByDifferentSymbol() {
        Field field = new Field(5, 1);
        field.setCell(0, 0, '^');
        field.setCell(0, 1, '^');
        field.setCell(0, 2, '*');
        field.setCell(0, 3, '^');
        field.setCell(0, 4, '^');

        int cleared = MatchChecker.checkAndClear(field);
        assertEquals(0, cleared);
    }

    @Test
    void runBrokenByEmptyCell() {
        Field field = new Field(5, 1);
        field.setCell(0, 0, '^');
        field.setCell(0, 1, '^');
        // (0,2) is empty
        field.setCell(0, 3, '^');
        field.setCell(0, 4, '^');

        int cleared = MatchChecker.checkAndClear(field);
        assertEquals(0, cleared);
    }

    @Test
    void crossMatchCountsOverlapOnce() {
        Field field = new Field(5, 5);
        // Horizontal: row 2, cols 0-4
        for (int c = 0; c < 5; c++) field.setCell(2, c, '*');
        // Vertical: col 2, rows 0-4
        for (int r = 0; r < 5; r++) field.setCell(r, 2, '*');

        int cleared = MatchChecker.checkAndClear(field);
        assertEquals(9, cleared); // 5 + 5 - 1 overlap
    }

    @Test
    void matchFromPdfExample() {
        // Simulates the final state before match clearing in the PDF example:
        // Brick 1 (H^^*) at row 7, cols 2,3,4
        // Brick 2 (V*@^) at col 1, rows 5,6,7
        // Row 7: ^ at col 1, ^ at col 2, ^ at col 3, * at col 4
        Field field = new Field(5, 8);
        field.setCell(5, 1, '*'); // V brick top
        field.setCell(6, 1, '@'); // V brick middle
        field.setCell(7, 1, '^'); // V brick bottom
        field.setCell(7, 2, '^'); // H brick symbol 1
        field.setCell(7, 3, '^'); // H brick symbol 2
        field.setCell(7, 4, '*'); // H brick symbol 3

        int cleared = MatchChecker.checkAndClear(field);
        assertEquals(3, cleared); // three ^ at row 7, cols 1,2,3

        assertEquals('*', field.getCell(5, 1));
        assertEquals('@', field.getCell(6, 1));
        assertEquals(Field.EMPTY, field.getCell(7, 1));
        assertEquals(Field.EMPTY, field.getCell(7, 2));
        assertEquals(Field.EMPTY, field.getCell(7, 3));
        assertEquals('*', field.getCell(7, 4));
    }

    @Test
    void match4RequiresFourInARow() {
        Field field = new Field(5, 5);
        field.setCell(4, 1, '^');
        field.setCell(4, 2, '^');
        field.setCell(4, 3, '^');

        assertEquals(0, MatchChecker.checkAndClear(field, 4));
        assertEquals('^', field.getCell(4, 1));

        field.setCell(4, 4, '^');
        assertEquals(4, MatchChecker.checkAndClear(field, 4));
    }

    @Test
    void match2ClearsPair() {
        Field field = new Field(3, 3);
        field.setCell(2, 0, '*');
        field.setCell(2, 1, '*');

        assertEquals(2, MatchChecker.checkAndClear(field, 2));
        assertEquals(Field.EMPTY, field.getCell(2, 0));
        assertEquals(Field.EMPTY, field.getCell(2, 1));
    }

    @Test
    void checkAndClearDefaultSameAsExplicitThree() {
        Field field = new Field(5, 5);
        field.setCell(4, 1, '^');
        field.setCell(4, 2, '^');
        field.setCell(4, 3, '^');

        Field copy = new Field(5, 5);
        copy.setCell(4, 1, '^');
        copy.setCell(4, 2, '^');
        copy.setCell(4, 3, '^');

        assertEquals(MatchChecker.checkAndClear(field), MatchChecker.checkAndClear(copy, 3));
    }

    @Test
    void checkAndClearInvalidMinRunLengthThrows() {
        Field field = new Field(3, 3);
        assertThrows(IllegalArgumentException.class, () -> MatchChecker.checkAndClear(field, 1));
        assertThrows(IllegalArgumentException.class, () -> MatchChecker.checkAndClear(field, 0));
        assertThrows(IllegalArgumentException.class, () -> MatchChecker.checkAndClear(field, -1));
    }
}
