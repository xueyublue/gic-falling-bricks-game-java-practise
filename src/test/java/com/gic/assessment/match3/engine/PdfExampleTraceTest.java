package com.gic.assessment.match3.engine;

import com.gic.assessment.match3.model.ActiveBrick;
import com.gic.assessment.match3.model.Brick;
import com.gic.assessment.match3.model.Command;
import com.gic.assessment.match3.model.Field;
import com.gic.assessment.match3.model.Orientation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Step-by-step trace of the exact example scenario from the PDF assessment,
 * verifying brick positions and field state at each frame.
 *
 * Input: 5 8 H^^* V*@^
 * Frames: LL, R, DR, LLR, (blank), R, DR -> Game Over
 */
class PdfExampleTraceTest {

    @Test
    void traceBrick1FrameByFrame() {
        Field field = new Field(5, 8);
        Brick brick1 = new Brick(Orientation.HORIZONTAL, '^', '^', '*');

        // Brick 1 starts centered: col = (5-3)/2 = 1, row = 0
        ActiveBrick active = ActiveBrick.createAtStart(brick1, field);
        assertNotNull(active);
        assertEquals(0, active.getRow());
        assertEquals(1, active.getCol());

        // Frame 1: User enters "LL"
        // L -> col 0; second L -> would go to col -1, ignored
        active.applyCommand(Command.LEFT, field);
        assertEquals(0, active.getCol());
        active.applyCommand(Command.LEFT, field); // ignored (out of bounds)
        assertEquals(0, active.getCol());
        // Auto-drop: row 0 -> row 1
        assertTrue(active.canMove(field, 1, 0));
        active.moveDown();
        assertEquals(1, active.getRow());
        assertEquals(0, active.getCol());

        // Frame 2: User enters "R"
        // R -> col 1
        active.applyCommand(Command.RIGHT, field);
        assertEquals(1, active.getCol());
        // Auto-drop: row 1 -> row 2
        assertTrue(active.canMove(field, 1, 0));
        active.moveDown();
        assertEquals(2, active.getRow());
        assertEquals(1, active.getCol());

        // Frame 3: User enters "DR"
        // D -> drops to bottom (row 7)
        active.applyCommand(Command.DROP, field);
        assertEquals(7, active.getRow());
        assertEquals(1, active.getCol());
        // R -> col 2
        active.applyCommand(Command.RIGHT, field);
        assertEquals(2, active.getCol());
        // Auto-drop: can't (row 7 is bottom) -> stationary
        assertFalse(active.canMove(field, 1, 0));

        // Place brick 1 on field
        active.placeOnField(field);
        assertEquals('^', field.getCell(7, 2));
        assertEquals('^', field.getCell(7, 3));
        assertEquals('*', field.getCell(7, 4));

        // No matches yet (only 2 ^ horizontally)
        int cleared = MatchChecker.checkAndClear(field);
        assertEquals(0, cleared);
    }

    @Test
    void traceBrick2FrameByFrame() {
        Field field = new Field(5, 8);
        // Pre-place brick 1 result
        field.setCell(7, 2, '^');
        field.setCell(7, 3, '^');
        field.setCell(7, 4, '*');

        Brick brick2 = new Brick(Orientation.VERTICAL, '*', '@', '^');

        // Brick 2 starts centered: col = (5-1)/2 = 2, row = 0
        ActiveBrick active = ActiveBrick.createAtStart(brick2, field);
        assertNotNull(active);
        assertEquals(0, active.getRow());
        assertEquals(2, active.getCol());

        // Frame 4: User enters "LLR" (only first 2 processed)
        // L -> col 1
        active.applyCommand(Command.LEFT, field);
        assertEquals(1, active.getCol());
        // L -> col 0
        active.applyCommand(Command.LEFT, field);
        assertEquals(0, active.getCol());
        // (R would be 3rd command, ignored)
        // Auto-drop: row 0 -> row 1
        assertTrue(active.canMove(field, 1, 0));
        active.moveDown();
        assertEquals(1, active.getRow());
        assertEquals(0, active.getCol());

        // Frame 5: User enters "" (blank)
        // No commands
        // Auto-drop: row 1 -> row 2
        assertTrue(active.canMove(field, 1, 0));
        active.moveDown();
        assertEquals(2, active.getRow());

        // Frame 6: User enters "R"
        // R -> col 1
        active.applyCommand(Command.RIGHT, field);
        assertEquals(1, active.getCol());
        // Auto-drop: row 2 -> row 3
        assertTrue(active.canMove(field, 1, 0));
        active.moveDown();
        assertEquals(3, active.getRow());

        // Frame 7: User enters "DR"
        // D -> drops from row 3. Bottom block at row 5 initially.
        // Can it go to row 4? Bottom block at row 6, no obstacle. Yes.
        // Can it go to row 5? Bottom block at row 7. Check col 1 at row 7: empty. Yes.
        // Can it go to row 6? Bottom block at row 8. Out of bounds. No.
        // So drops to row 5 (anchor), bottom block at row 7.
        active.applyCommand(Command.DROP, field);
        assertEquals(5, active.getRow());
        assertEquals(1, active.getCol());

        // R -> try to move to col 2. Bottom block would be at (7, 2) which has '^' -> blocked!
        active.applyCommand(Command.RIGHT, field);
        assertEquals(1, active.getCol()); // unchanged, blocked

        // Auto-drop: can't (bottom block at row 7, row 8 out of bounds) -> stationary
        assertFalse(active.canMove(field, 1, 0));

        // Place brick 2 on field
        active.placeOnField(field);
        assertEquals('*', field.getCell(5, 1));
        assertEquals('@', field.getCell(6, 1));
        assertEquals('^', field.getCell(7, 1));

        // Now row 7 has: ^(1), ^(2), ^(3), *(4) -> three ^ horizontally at cols 1,2,3!
        assertEquals('^', field.getCell(7, 1));
        assertEquals('^', field.getCell(7, 2));
        assertEquals('^', field.getCell(7, 3));
        assertEquals('*', field.getCell(7, 4));

        int cleared = MatchChecker.checkAndClear(field);
        assertEquals(3, cleared);

        // After clearing + gravity: * and @ in col 1 drop down 1 row
        assertEquals(Field.EMPTY, field.getCell(5, 1));
        assertEquals('*', field.getCell(6, 1));
        assertEquals('@', field.getCell(7, 1));
        assertEquals(Field.EMPTY, field.getCell(7, 2));
        assertEquals(Field.EMPTY, field.getCell(7, 3));
        assertEquals('*', field.getCell(7, 4));

        // Final state (with gravity):
        // Row 7 (1-indexed): . * . . .
        // Row 8: . @ . . *
    }
}
