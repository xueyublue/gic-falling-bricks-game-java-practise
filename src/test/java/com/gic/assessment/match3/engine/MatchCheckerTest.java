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
    void gravityAfterClearDropsFloatingCells() {
        // Column:  ^  .  ^  ^  ^   (top to bottom, col 0)
        //          After clearing ^^^: top ^ floats, gravity drops it
        Field field = new Field(1, 5);
        field.setCell(0, 0, '^');
        field.setCell(2, 0, '^');
        field.setCell(3, 0, '^');
        field.setCell(4, 0, '^');

        int cleared = MatchChecker.checkAndClear(field);
        assertEquals(3, cleared);
        // The floating ^ at row 0 should have dropped to the bottom
        assertEquals('^', field.getCell(4, 0));
        for (int r = 0; r < 4; r++) {
            assertEquals(Field.EMPTY, field.getCell(r, 0));
        }
    }

    @Test
    void chainReactionFromGravity() {
        // Set up so that clearing a horizontal match causes symbols to fall
        // and form a new vertical match.
        //
        // col:  0  1  2
        // row 0: .  *  .
        // row 1: .  *  .
        // row 2: ^  ^  ^   ← horizontal match (cleared first)
        // row 3: .  *  .
        //
        // After clearing row 2: gravity drops col 1 symbols →
        // col 1 becomes: .  *  *  *  → vertical match of 3 *'s
        Field field = new Field(3, 4);
        field.setCell(0, 1, '*');
        field.setCell(1, 1, '*');
        field.setCell(2, 0, '^');
        field.setCell(2, 1, '^');
        field.setCell(2, 2, '^');
        field.setCell(3, 1, '*');

        int cleared = MatchChecker.checkAndClear(field);
        // First pass: 3 (^^^), then gravity → col 1 = [*, *, *] → second pass: 3
        assertEquals(6, cleared);
        // Everything should be empty now
        for (int r = 0; r < 4; r++)
            for (int c = 0; c < 3; c++)
                assertEquals(Field.EMPTY, field.getCell(r, c));
    }

    @Test
    void noChainWhenGravityDoesNotCreateMatch() {
        // Horizontal match with different symbols above — no chain
        Field field = new Field(3, 3);
        field.setCell(0, 0, '*');
        field.setCell(0, 1, '@');
        field.setCell(0, 2, '~');
        field.setCell(2, 0, '^');
        field.setCell(2, 1, '^');
        field.setCell(2, 2, '^');

        int cleared = MatchChecker.checkAndClear(field);
        assertEquals(3, cleared);
        // After gravity, the different symbols drop to row 2 — no new match
        assertEquals('*', field.getCell(2, 0));
        assertEquals('@', field.getCell(2, 1));
        assertEquals('~', field.getCell(2, 2));
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

        // After clearing + gravity: * and @ drop down 1 row in col 1
        assertEquals(Field.EMPTY, field.getCell(5, 1));
        assertEquals('*', field.getCell(6, 1));
        assertEquals('@', field.getCell(7, 1));
        assertEquals(Field.EMPTY, field.getCell(7, 2));
        assertEquals(Field.EMPTY, field.getCell(7, 3));
        assertEquals('*', field.getCell(7, 4));
    }
}
