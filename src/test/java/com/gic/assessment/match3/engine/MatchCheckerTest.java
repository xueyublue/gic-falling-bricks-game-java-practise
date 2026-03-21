package com.gic.assessment.match3.engine;

import com.gic.assessment.match3.model.Field;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MatchCheckerTest {

    private static final int PPC = MatchChecker.POINTS_PER_CELL;

    @Test
    void horizontalMatchOfThree() {
        Field field = new Field(5, 5);
        field.setCell(4, 1, '^');
        field.setCell(4, 2, '^');
        field.setCell(4, 3, '^');

        var result = MatchChecker.checkAndClear(field);
        assertEquals(3, result.totalCleared());
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

        var result = MatchChecker.checkAndClear(field);
        assertEquals(3, result.totalCleared());
        assertEquals(Field.EMPTY, field.getCell(2, 1));
        assertEquals(Field.EMPTY, field.getCell(3, 1));
        assertEquals(Field.EMPTY, field.getCell(4, 1));
    }

    @Test
    void noMatchOfTwo() {
        Field field = new Field(5, 5);
        field.setCell(4, 1, '^');
        field.setCell(4, 2, '^');

        var result = MatchChecker.checkAndClear(field);
        assertEquals(0, result.totalCleared());
        assertEquals(0, result.score());
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

        var result = MatchChecker.checkAndClear(field);
        assertEquals(4, result.totalCleared());
    }

    @Test
    void multipleMatchesAtOnce() {
        Field field = new Field(5, 5);
        field.setCell(4, 0, '^');
        field.setCell(4, 1, '^');
        field.setCell(4, 2, '^');
        field.setCell(0, 4, '*');
        field.setCell(1, 4, '*');
        field.setCell(2, 4, '*');

        var result = MatchChecker.checkAndClear(field);
        assertEquals(6, result.totalCleared());
    }

    @Test
    void crossMatch() {
        Field field = new Field(5, 5);
        field.setCell(2, 0, '^');
        field.setCell(2, 1, '^');
        field.setCell(2, 2, '^');
        field.setCell(0, 1, '^');
        field.setCell(1, 1, '^');

        var result = MatchChecker.checkAndClear(field);
        assertEquals(5, result.totalCleared());
    }

    @Test
    void noMatchOnEmptyField() {
        Field field = new Field(5, 5);
        var result = MatchChecker.checkAndClear(field);
        assertEquals(0, result.totalCleared());
        assertEquals(0, result.score());
    }

    @Test
    void differentSymbolsDontMatch() {
        Field field = new Field(5, 5);
        field.setCell(4, 0, '^');
        field.setCell(4, 1, '*');
        field.setCell(4, 2, '^');

        var result = MatchChecker.checkAndClear(field);
        assertEquals(0, result.totalCleared());
    }

    @Test
    void fullRowMatchClearsEntireRow() {
        Field field = new Field(5, 3);
        for (int c = 0; c < 5; c++) {
            field.setCell(2, c, '^');
        }
        var result = MatchChecker.checkAndClear(field);
        assertEquals(5, result.totalCleared());
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
        var result = MatchChecker.checkAndClear(field);
        assertEquals(5, result.totalCleared());
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

        var result = MatchChecker.checkAndClear(field);
        assertEquals(6, result.totalCleared());
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

        var result = MatchChecker.checkAndClear(field);
        assertEquals(6, result.totalCleared());
    }

    @Test
    void tShapeMatch() {
        Field field = new Field(5, 5);
        field.setCell(2, 0, '^');
        field.setCell(2, 1, '^');
        field.setCell(2, 2, '^');
        field.setCell(3, 1, '^');
        field.setCell(4, 1, '^');

        var result = MatchChecker.checkAndClear(field);
        assertEquals(5, result.totalCleared());
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

        var result = MatchChecker.checkAndClear(field);
        assertEquals(6, result.totalCleared());
    }

    @Test
    void twoInRowTwoInColumnNoMatch() {
        Field field = new Field(3, 3);
        field.setCell(0, 0, '^');
        field.setCell(0, 1, '^');
        field.setCell(1, 0, '^');

        var result = MatchChecker.checkAndClear(field);
        assertEquals(0, result.totalCleared());
    }

    @Test
    void minimumFieldSize3x3WithMatch() {
        Field field = new Field(3, 3);
        field.setCell(0, 0, '@');
        field.setCell(0, 1, '@');
        field.setCell(0, 2, '@');

        var result = MatchChecker.checkAndClear(field);
        assertEquals(3, result.totalCleared());
    }

    @Test
    void runBrokenByDifferentSymbol() {
        Field field = new Field(5, 1);
        field.setCell(0, 0, '^');
        field.setCell(0, 1, '^');
        field.setCell(0, 2, '*');
        field.setCell(0, 3, '^');
        field.setCell(0, 4, '^');

        var result = MatchChecker.checkAndClear(field);
        assertEquals(0, result.totalCleared());
    }

    @Test
    void runBrokenByEmptyCell() {
        Field field = new Field(5, 1);
        field.setCell(0, 0, '^');
        field.setCell(0, 1, '^');
        field.setCell(0, 3, '^');
        field.setCell(0, 4, '^');

        var result = MatchChecker.checkAndClear(field);
        assertEquals(0, result.totalCleared());
    }

    @Test
    void crossMatchCountsOverlapOnce() {
        Field field = new Field(5, 5);
        for (int c = 0; c < 5; c++) field.setCell(2, c, '*');
        for (int r = 0; r < 5; r++) field.setCell(r, 2, '*');

        var result = MatchChecker.checkAndClear(field);
        assertEquals(9, result.totalCleared());
    }

    @Test
    void gravityAfterClearDropsFloatingCells() {
        Field field = new Field(1, 5);
        field.setCell(0, 0, '^');
        field.setCell(2, 0, '^');
        field.setCell(3, 0, '^');
        field.setCell(4, 0, '^');

        var result = MatchChecker.checkAndClear(field);
        assertEquals(3, result.totalCleared());
        assertEquals('^', field.getCell(4, 0));
        for (int r = 0; r < 4; r++) {
            assertEquals(Field.EMPTY, field.getCell(r, 0));
        }
    }

    @Test
    void chainReactionFromGravity() {
        Field field = new Field(3, 4);
        field.setCell(0, 1, '*');
        field.setCell(1, 1, '*');
        field.setCell(2, 0, '^');
        field.setCell(2, 1, '^');
        field.setCell(2, 2, '^');
        field.setCell(3, 1, '*');

        var result = MatchChecker.checkAndClear(field);
        assertEquals(6, result.totalCleared());
        for (int r = 0; r < 4; r++)
            for (int c = 0; c < 3; c++)
                assertEquals(Field.EMPTY, field.getCell(r, c));
    }

    @Test
    void noChainWhenGravityDoesNotCreateMatch() {
        Field field = new Field(3, 3);
        field.setCell(0, 0, '*');
        field.setCell(0, 1, '@');
        field.setCell(0, 2, '~');
        field.setCell(2, 0, '^');
        field.setCell(2, 1, '^');
        field.setCell(2, 2, '^');

        var result = MatchChecker.checkAndClear(field);
        assertEquals(3, result.totalCleared());
        assertEquals('*', field.getCell(2, 0));
        assertEquals('@', field.getCell(2, 1));
        assertEquals('~', field.getCell(2, 2));
    }

    @Test
    void matchFromPdfExample() {
        Field field = new Field(5, 8);
        field.setCell(5, 1, '*');
        field.setCell(6, 1, '@');
        field.setCell(7, 1, '^');
        field.setCell(7, 2, '^');
        field.setCell(7, 3, '^');
        field.setCell(7, 4, '*');

        var result = MatchChecker.checkAndClear(field);
        assertEquals(3, result.totalCleared());

        assertEquals(Field.EMPTY, field.getCell(5, 1));
        assertEquals('*', field.getCell(6, 1));
        assertEquals('@', field.getCell(7, 1));
        assertEquals(Field.EMPTY, field.getCell(7, 2));
        assertEquals(Field.EMPTY, field.getCell(7, 3));
        assertEquals('*', field.getCell(7, 4));
    }

    // ── Scoring tests ──────────────────────────────────────────────────

    @Test
    void scoreForSimpleMatchChain1() {
        Field field = new Field(3, 1);
        field.setCell(0, 0, '^');
        field.setCell(0, 1, '^');
        field.setCell(0, 2, '^');

        var result = MatchChecker.checkAndClear(field);
        assertEquals(3, result.totalCleared());
        assertEquals(3 * PPC * 1, result.score());
    }

    @Test
    void scoreForFourCellMatch() {
        Field field = new Field(4, 1);
        field.setCell(0, 0, '*');
        field.setCell(0, 1, '*');
        field.setCell(0, 2, '*');
        field.setCell(0, 3, '*');

        var result = MatchChecker.checkAndClear(field);
        assertEquals(4, result.totalCleared());
        assertEquals(4 * PPC * 1, result.score());
    }

    @Test
    void scoreForChainReactionWithMultiplier() {
        // Chain 1: ^^^ horizontal (3 cells * 10 * 1 = 30)
        // Gravity creates chain 2: *** vertical (3 cells * 10 * 2 = 60)
        // Total score = 90
        Field field = new Field(3, 4);
        field.setCell(0, 1, '*');
        field.setCell(1, 1, '*');
        field.setCell(2, 0, '^');
        field.setCell(2, 1, '^');
        field.setCell(2, 2, '^');
        field.setCell(3, 1, '*');

        var result = MatchChecker.checkAndClear(field);
        assertEquals(6, result.totalCleared());
        assertEquals(3 * PPC * 1 + 3 * PPC * 2, result.score());
    }

    @Test
    void scoreZeroWhenNoMatch() {
        Field field = new Field(3, 3);
        field.setCell(0, 0, '^');
        field.setCell(0, 1, '*');

        var result = MatchChecker.checkAndClear(field);
        assertEquals(0, result.totalCleared());
        assertEquals(0, result.score());
    }

    @Test
    void scoreForMultipleMatchesSamePass() {
        // Two independent matches cleared in the same pass count as chain 1
        Field field = new Field(6, 1);
        field.setCell(0, 0, '^');
        field.setCell(0, 1, '^');
        field.setCell(0, 2, '^');
        field.setCell(0, 3, '*');
        field.setCell(0, 4, '*');
        field.setCell(0, 5, '*');

        var result = MatchChecker.checkAndClear(field);
        assertEquals(6, result.totalCleared());
        assertEquals(6 * PPC * 1, result.score());
    }

    @Test
    void scoreFromPdfExample() {
        // 3 cells cleared at chain level 1 → 3 * 10 * 1 = 30
        Field field = new Field(5, 8);
        field.setCell(5, 1, '*');
        field.setCell(6, 1, '@');
        field.setCell(7, 1, '^');
        field.setCell(7, 2, '^');
        field.setCell(7, 3, '^');
        field.setCell(7, 4, '*');

        var result = MatchChecker.checkAndClear(field);
        assertEquals(3 * PPC * 1, result.score());
    }
}
