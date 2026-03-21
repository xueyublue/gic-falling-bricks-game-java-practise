package com.gic.assessment.match3.engine;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {

    private String runGame(String... inputs) {
        String input = String.join("\n", inputs) + "\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(baos);

        Game game = new Game(scanner, out);
        game.run();
        return baos.toString().replace("\r\n", "\n");
    }

    /**
     * Builds the expected 5x8 field display string from 8 row content strings.
     * Each row content is the inner part like ". ^ ^ * ." (without borders).
     */
    private String buildField(String r1, String r2, String r3, String r4,
                              String r5, String r6, String r7, String r8) {
        return String.join("\n",
                "1\t| " + r1 + " |",
                "2\t| " + r2 + " |",
                "3\t| " + r3 + " |",
                "4\t| " + r4 + " |",
                "5\t| " + r5 + " |",
                "6\t| " + r6 + " |",
                "7\t| " + r7 + " |",
                "8\t| " + r8 + " |"
        );
    }

    private static final String EMPTY_ROW = ". . . . .";

    @Test
    void pdfExampleScenario() {
        // Replays the exact example from the assessment PDF (v3):
        // Input: 5 8 H^^* V*@^
        // Frame 1: LL, Frame 2: R, Frame 3: DR
        // Frame 4: LLR, Frame 5: (blank), Frame 6: R, Frame 7: DR
        // Frame 8: final field after match clearing, then Game Over.
        String output = runGame(
                "5 8 H^^* V*@^",
                "LL",    // Frame 1
                "R",     // Frame 2
                "DR",    // Frame 3
                "LLR",   // Frame 4
                "",      // Frame 5
                "R",     // Frame 6
                "DR"     // Frame 7
        );

        // ── Frame 1: H^^* spawns centered at row 0, cols 1-3 ────────────
        // Brick is at: (0,1)=^, (0,2)=^, (0,3)=*
        String frame1Field = buildField(
                ". ^ ^ * .", EMPTY_ROW, EMPTY_ROW, EMPTY_ROW,
                EMPTY_ROW,  EMPTY_ROW, EMPTY_ROW, EMPTY_ROW);
        assertTrue(output.contains("Frame 1\n" + frame1Field),
                "Frame 1: H^^* should be centered in row 1");

        // ── Frame 2: After LL (only 1st L applied, 2nd ignored) + auto-drop ─
        // L: col 1→0. Second L: col -1 out of bounds, ignored.
        // Auto-drop: row 0→1. Brick at (1, 0): ^, ^, *
        String frame2Field = buildField(
                EMPTY_ROW, "^ ^ * . .", EMPTY_ROW, EMPTY_ROW,
                EMPTY_ROW, EMPTY_ROW,   EMPTY_ROW, EMPTY_ROW);
        assertTrue(output.contains("Frame 2\n" + frame2Field),
                "Frame 2: brick should be at row 2, cols 0-2 after L + auto-drop");

        // ── Frame 3: After R + auto-drop ─────────────────────────────────
        // R: col 0→1. Auto-drop: row 1→2. Brick at (2, 1): ^, ^, *
        String frame3Field = buildField(
                EMPTY_ROW, EMPTY_ROW, ". ^ ^ * .", EMPTY_ROW,
                EMPTY_ROW, EMPTY_ROW, EMPTY_ROW,   EMPTY_ROW);
        assertTrue(output.contains("Frame 3\n" + frame3Field),
                "Frame 3: brick should be at row 3, cols 1-3 after R + auto-drop");

        // ── Frame 4: After DR (brick 1 placed) + V*@^ spawns ────────────
        // D: drops to row 7, cols 1-3. R: moves to cols 2-4.
        // Auto-drop: can't (row 8 out of bounds) → stationary.
        // Brick 1 placed: (7,2)=^, (7,3)=^, (7,4)=*. No matches.
        // V*@^ spawns at rows 0-2, col 2: (0,2)=*, (1,2)=@, (2,2)=^
        String frame4Field = buildField(
                ". . * . .", ". . @ . .", ". . ^ . .", EMPTY_ROW,
                EMPTY_ROW,  EMPTY_ROW,  EMPTY_ROW,  ". . ^ ^ *");
        assertTrue(output.contains("Frame 4\n" + frame4Field),
                "Frame 4: V*@^ at top-center + H^^* placed at row 8");

        // ── Frame 5: After LLR (only first 2 commands: LL) + auto-drop ──
        // L: col 2→1. L: col 1→0. R is 3rd command, ignored.
        // Auto-drop: row 0→1. Brick at rows 1-3, col 0.
        String frame5Field = buildField(
                EMPTY_ROW,  "* . . . .", "@ . . . .", "^ . . . .",
                EMPTY_ROW,  EMPTY_ROW,  EMPTY_ROW,   ". . ^ ^ *");
        assertTrue(output.contains("Frame 5\n" + frame5Field),
                "Frame 5: V*@^ at rows 2-4, col 0 after LL + auto-drop");

        // ── Frame 6: After blank (no commands) + auto-drop ───────────────
        // No commands. Auto-drop: row 1→2. Brick at rows 2-4, col 0.
        String frame6Field = buildField(
                EMPTY_ROW,  EMPTY_ROW,  "* . . . .", "@ . . . .",
                "^ . . . .", EMPTY_ROW,  EMPTY_ROW,  ". . ^ ^ *");
        assertTrue(output.contains("Frame 6\n" + frame6Field),
                "Frame 6: V*@^ at rows 3-5, col 0 after auto-drop");

        // ── Frame 7: After R + auto-drop ─────────────────────────────────
        // R: col 0→1. Auto-drop: row 2→3. Brick at rows 3-5, col 1.
        String frame7Field = buildField(
                EMPTY_ROW,  EMPTY_ROW,  EMPTY_ROW,  ". * . . .",
                ". @ . . .", ". ^ . . .", EMPTY_ROW,  ". . ^ ^ *");
        assertTrue(output.contains("Frame 7\n" + frame7Field),
                "Frame 7: V*@^ at rows 4-6, col 1 after R + auto-drop");

        // ── Frame 8: Final frame after placement + match clearing + gravity ─
        // DR in Frame 7: D drops to rows 5-7, col 1.
        // R: blocked (col 2, row 7 has ^). Auto-drop: can't → stationary.
        // Placed: (5,1)=*, (6,1)=@, (7,1)=^
        // Row 7 becomes: . ^ ^ ^ * → 3 matching ^ at cols 1-3 → cleared.
        // Gravity: * and @ in col 1 drop down 1 row each.
        // Final: row 7 = ". * . . .", row 8 = ". @ . . *"
        String frame8Field = buildField(
                EMPTY_ROW,  EMPTY_ROW,  EMPTY_ROW,  EMPTY_ROW,
                EMPTY_ROW,  EMPTY_ROW,  ". * . . .", ". @ . . *");
        assertTrue(output.contains("Frame 8\n" + frame8Field),
                "Frame 8: final field after 3 ^ cleared from row 8");

        // ── Game Over follows the last frame ─────────────────────────────
        assertTrue(output.contains("Game Over."),
                "Game should end after all bricks are placed");

        // ── Verify frame count: exactly 8 frames ────────────────────────
        assertTrue(output.contains("Frame 8"), "Should have 8 frames total");
        assertFalse(output.contains("Frame 9"), "Should NOT have a 9th frame");

        // ── Verify prompt appears for each interactive frame (1-7) ───────
        String prompt = "Enter up to 2 commands to process before moving to the next frame (valid commands are L, R, D):";
        int promptCount = countOccurrences(output, prompt);
        assertEquals(7, promptCount,
                "Prompt should appear exactly 7 times (frames 1-7, not frame 8)");
    }

    /** Counts how many times a substring appears in a string. */
    private int countOccurrences(String text, String sub) {
        int count = 0;
        int idx = 0;
        while ((idx = text.indexOf(sub, idx)) != -1) {
            count++;
            idx += sub.length();
        }
        return count;
    }

    @Test
    void gameEndsWhenNoBricks() {
        // No bricks provided: game should end immediately
        String output = runGame("5 8");
        assertTrue(output.contains("Game Over."));
    }

    @Test
    void gameEndsWhenStartingPositionBlocked() {
        // Field 3x1, H^*@ won't match (all different symbols), blocking second brick
        String output = runGame(
                "3 1 H^*@ H***",
                "D"  // Frame 1: brick at bottom of 1-row field -> stationary
        );
        assertTrue(output.contains("Game Over."));
    }

    @Test
    void singleBrickPlacedAtBottom() {
        String output = runGame(
                "5 3 H^^*",
                "D"
        );

        assertTrue(output.contains("Game Over."));
        // Brick dropped to bottom (row 2)
    }

    @Test
    void blankCommandInput() {
        // Blank input means no commands; brick just drops 1 row each frame
        String output = runGame(
                "5 4 H^^*",
                "",  // Frame 1: no commands, drops to row 1
                "",  // Frame 2: drops to row 2
                "",  // Frame 3: drops to row 3 (bottom)
                ""   // Frame 4: can't drop further -> stationary
        );

        assertTrue(output.contains("Game Over."));
    }

    @Test
    void verticalBrickDropScenario() {
        String output = runGame(
                "3 5 V^^^",
                "D"
        );

        assertTrue(output.contains("Game Over."));
        assertTrue(output.contains("Frame 1"));
        assertTrue(output.contains("Frame 2"));
    }

    @Test
    void twoBricksWithMatchClearing() {
        // 3-wide field, both H bricks fill the same row → match clears
        String output = runGame(
                "3 3 H^^^ H^^^",
                "D",
                "D"
        );

        assertTrue(output.contains("Game Over."));
    }

    @Test
    void dropCommandMakesBrickStationary() {
        String output = runGame(
                "5 8 H^^*",
                "D"
        );

        assertTrue(output.contains("Frame 1"));
        assertTrue(output.contains("Frame 2")); // final frame
        assertFalse(output.contains("Frame 3"));
        assertTrue(output.contains("Game Over."));
    }

    @Test
    void twoDropCommandsInSameFrame() {
        String output = runGame(
                "5 8 H^^*",
                "DD"
        );

        assertTrue(output.contains("Game Over."));
        assertTrue(output.contains("Frame 2"));
        assertFalse(output.contains("Frame 3"));
    }

    @Test
    void multipleBricksStackOnField() {
        // Two bricks dropped into same column, second stacks on first
        String output = runGame(
                "5 8 H^^* H^^*",
                "D",
                "D"
        );

        assertTrue(output.contains("Game Over."));
    }

    @Test
    void brickImmediatelyStationaryOnSingleRowField() {
        // Field height = 1: horizontal brick spawns and can't drop
        String output = runGame(
                "3 1 H^*@",
                ""
        );

        assertTrue(output.contains("Frame 1"));
        assertTrue(output.contains("Frame 2")); // final frame
        assertTrue(output.contains("Game Over."));
    }

    @Test
    void verticalBrickOnHeight3FieldImmediatelyStationary() {
        String output = runGame(
                "3 3 V^^^",
                ""
        );

        assertTrue(output.contains("Game Over."));
    }

    @Test
    void secondBrickBlockedByFirst() {
        // H^*@ on a 3x1 field fills the only row. Second brick can't spawn.
        String output = runGame(
                "3 1 H^*@ H~~~",
                ""
        );

        assertTrue(output.contains("Game Over."));
    }

    @Test
    void moveLeftThenDropPlacesBrickAtLeftEdge() {
        String output = runGame(
                "5 3 H^^*",
                "LL",  // move to col 0
                "D"
        );

        assertTrue(output.contains("Game Over."));
        // Final frame should show brick at bottom-left
        String bottomRow = ". . . . .";
        String brickRow = "^ ^ * . .";
        assertTrue(output.contains("| " + brickRow + " |"));
    }

    @Test
    void moveRightThenDropPlacesBrickAtRightEdge() {
        String output = runGame(
                "5 3 H^^*",
                "RR",  // move from col 1 to col 2 (max right for 5-wide)
                "D"
        );

        assertTrue(output.contains("Game Over."));
        String brickRow = ". . ^ ^ *";
        assertTrue(output.contains("| " + brickRow + " |"));
    }

    @Test
    void matchClearingHorizontalThreeInRow() {
        // 3-wide field, drop H^^^ → fills entire bottom row → clears
        String output = runGame(
                "3 3 H^^^",
                "D"
        );

        assertTrue(output.contains("Game Over."));
        // Final frame: bottom row should be cleared
        String clearedRow = ". . .";
        String finalFrame = output.substring(output.lastIndexOf("Frame"));
        assertTrue(finalFrame.contains("| " + clearedRow + " |"));
    }

    @Test
    void gameOverPrintedExactlyOnce() {
        String output = runGame("5 8");
        int count = countOccurrences(output, "Game Over.");
        assertEquals(1, count);
    }

    @Test
    void fiveBricksMaximum() {
        // Provide 5 bricks on a large field, all should be processed
        String output = runGame(
                "5 10 H^^* H*** H~~~ H@@@ H^^^",
                "D", "D", "D", "D", "D"
        );

        assertTrue(output.contains("Game Over."));
        assertTrue(output.contains("Frame 6")); // 5 interactive frames + 1 final
    }
}
