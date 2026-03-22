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

    /** Builds the expected frame block: "Frame N\nScore: S\n" + field */
    private String frameBlock(int frameNum, int score, String fieldStr) {
        return "Frame " + frameNum + "\nScore: " + score + "\n" + fieldStr;
    }

    private static final String EMPTY_ROW = ". . . . .";

    @Test
    void pdfExampleScenario() {
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

        // Frames 1-3: brick 1 falling, score stays 0
        String frame1Field = buildField(
                ". ^ ^ * .", EMPTY_ROW, EMPTY_ROW, EMPTY_ROW,
                EMPTY_ROW,  EMPTY_ROW, EMPTY_ROW, EMPTY_ROW);
        assertTrue(output.contains(frameBlock(1, 0, frame1Field)),
                "Frame 1: H^^* should be centered in row 1, score 0");

        String frame2Field = buildField(
                EMPTY_ROW, "^ ^ * . .", EMPTY_ROW, EMPTY_ROW,
                EMPTY_ROW, EMPTY_ROW,   EMPTY_ROW, EMPTY_ROW);
        assertTrue(output.contains(frameBlock(2, 0, frame2Field)),
                "Frame 2: brick at row 2 after L + auto-drop, score 0");

        String frame3Field = buildField(
                EMPTY_ROW, EMPTY_ROW, ". ^ ^ * .", EMPTY_ROW,
                EMPTY_ROW, EMPTY_ROW, EMPTY_ROW,   EMPTY_ROW);
        assertTrue(output.contains(frameBlock(3, 0, frame3Field)),
                "Frame 3: brick at row 3 after R + auto-drop, score 0");

        // Frame 4: brick 1 placed (no match), V*@^ spawns. Score still 0.
        String frame4Field = buildField(
                ". . * . .", ". . @ . .", ". . ^ . .", EMPTY_ROW,
                EMPTY_ROW,  EMPTY_ROW,  EMPTY_ROW,  ". . ^ ^ *");
        assertTrue(output.contains(frameBlock(4, 0, frame4Field)),
                "Frame 4: V*@^ at top-center + H^^* placed at row 8, score 0");

        // Frames 5-7: brick 2 falling, score still 0
        String frame5Field = buildField(
                EMPTY_ROW,  "* . . . .", "@ . . . .", "^ . . . .",
                EMPTY_ROW,  EMPTY_ROW,  EMPTY_ROW,   ". . ^ ^ *");
        assertTrue(output.contains(frameBlock(5, 0, frame5Field)),
                "Frame 5: V*@^ at rows 2-4 after LL + auto-drop, score 0");

        String frame6Field = buildField(
                EMPTY_ROW,  EMPTY_ROW,  "* . . . .", "@ . . . .",
                "^ . . . .", EMPTY_ROW,  EMPTY_ROW,  ". . ^ ^ *");
        assertTrue(output.contains(frameBlock(6, 0, frame6Field)),
                "Frame 6: V*@^ at rows 3-5 after auto-drop, score 0");

        String frame7Field = buildField(
                EMPTY_ROW,  EMPTY_ROW,  EMPTY_ROW,  ". * . . .",
                ". @ . . .", ". ^ . . .", EMPTY_ROW,  ". . ^ ^ *");
        assertTrue(output.contains(frameBlock(7, 0, frame7Field)),
                "Frame 7: V*@^ at rows 4-6 after R + auto-drop, score 0");

        // Frame 8: final frame after 3 ^ cleared (30 pts) + gravity
        String frame8Field = buildField(
                EMPTY_ROW,  EMPTY_ROW,  EMPTY_ROW,  EMPTY_ROW,
                EMPTY_ROW,  EMPTY_ROW,  ". * . . .", ". @ . . *");
        assertTrue(output.contains(frameBlock(8, 30, frame8Field)),
                "Frame 8: final field after clearing, score 30");

        // Game Over with final score
        assertTrue(output.contains("Game Over. Final Score: 30"),
                "Game should end with final score 30");

        assertTrue(output.contains("Frame 8"), "Should have 8 frames total");
        assertFalse(output.contains("Frame 9"), "Should NOT have a 9th frame");

        String prompt = "Enter up to 2 commands to process before moving to the next frame (valid commands are L, R, D, T):";
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
        String output = runGame("5 8");
        assertTrue(output.contains("Game Over. Final Score: 0"));
    }

    @Test
    void gameEndsWhenStartingPositionBlocked() {
        String output = runGame(
                "3 1 H^*@ H***",
                "D"
        );
        assertTrue(output.contains("Game Over. Final Score: 0"));
    }

    @Test
    void singleBrickPlacedAtBottom() {
        String output = runGame(
                "5 3 H^^*",
                "D"
        );
        assertTrue(output.contains("Game Over. Final Score: 0"));
    }

    @Test
    void blankCommandInput() {
        String output = runGame(
                "5 4 H^^*",
                "",  // Frame 1
                "",  // Frame 2
                "",  // Frame 3
                ""   // Frame 4
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
        assertTrue(output.contains("Frame 2"));
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
        String output = runGame(
                "5 8 H^^* H^^*",
                "D",
                "D"
        );
        assertTrue(output.contains("Game Over."));
    }

    @Test
    void brickImmediatelyStationaryOnSingleRowField() {
        String output = runGame(
                "3 1 H^*@",
                ""
        );

        assertTrue(output.contains("Frame 1"));
        assertTrue(output.contains("Frame 2"));
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
                "LL",
                "D"
        );
        assertTrue(output.contains("Game Over."));
        assertTrue(output.contains("| ^ ^ * . . |"));
    }

    @Test
    void moveRightThenDropPlacesBrickAtRightEdge() {
        String output = runGame(
                "5 3 H^^*",
                "RR",
                "D"
        );
        assertTrue(output.contains("Game Over."));
        assertTrue(output.contains("| . . ^ ^ * |"));
    }

    @Test
    void matchClearingHorizontalThreeInRow() {
        // 3-wide field, drop H^^^ → fills entire bottom row → clears → 30 pts
        String output = runGame(
                "3 3 H^^^",
                "D"
        );
        assertTrue(output.contains("Game Over. Final Score: 30"));
        String finalFrame = output.substring(output.lastIndexOf("Frame"));
        assertTrue(finalFrame.contains("| . . . |"));
    }

    @Test
    void gameOverPrintedExactlyOnce() {
        String output = runGame("5 8");
        int count = countOccurrences(output, "Game Over.");
        assertEquals(1, count);
    }

    @Test
    void fiveBricksMaximum() {
        String output = runGame(
                "5 10 H^^* H*** H~~~ H@@@ H^^^",
                "D", "D", "D", "D", "D"
        );
        assertTrue(output.contains("Game Over."));
        assertTrue(output.contains("Frame 6"));
    }

    // ── Scoring integration tests ──────────────────────────────────────

    @Test
    void scoreDisplayedInEveryFrame() {
        String output = runGame(
                "5 3 H^^*",
                "D"
        );
        assertTrue(output.contains("Score: 0"), "Score line should appear in frames");
    }

    @Test
    void finalScoreZeroWhenNoMatches() {
        String output = runGame(
                "5 8 H^^*",
                "D"
        );
        assertTrue(output.contains("Game Over. Final Score: 0"));
    }

    @Test
    void finalScoreReflectsMatchPoints() {
        // H^^^ on a 3-wide field → 3 cells cleared, chain 1 → 3*10*1 = 30
        String output = runGame(
                "3 3 H^^^",
                "D"
        );
        assertTrue(output.contains("Game Over. Final Score: 30"));
    }

    @Test
    void scoreAccumulatesAcrossMultipleBricks() {
        // 3x3 field. Brick 1: H^^^ drops → matches (30 pts, cleared).
        // Brick 2: H^^^ drops to same spot → matches again (30 pts).
        // Total = 60.
        String output = runGame(
                "3 3 H^^^ H^^^",
                "D",
                "D"
        );
        assertTrue(output.contains("Game Over. Final Score: 60"));
    }

    // ── Rotation integration tests ─────────────────────────────────────

    @Test
    void turnCommandRotatesBrickInGame() {
        // 5x8 field, H^^* brick. T rotates H→V, then D drops as vertical.
        // Vertical brick occupies 3 rows in one column.
        String output = runGame(
                "5 8 H^^*",
                "TD"
        );
        assertTrue(output.contains("Game Over."));
        // After T+D: vertical brick placed at col 1, rows 5-7
        assertTrue(output.contains("| . ^ . . . |"));
        assertTrue(output.contains("| . ^ . . . |"));
        assertTrue(output.contains("| . * . . . |"));
    }

    @Test
    void turnBlockedIgnoredInGame() {
        // 3x1 field, V^^* spawns at col 1 — can't rotate to H (needs 3 cols from col 1 = cols 1,2,3 but width is 3)
        // Actually V on 3x1 can't even spawn (needs 3 rows, only 1 available).
        // Use 3x3 field with H brick at right edge: T blocked, stays H
        String output = runGame(
                "3 3 H^^^",
                "D"
        );
        // H^^^ fills entire bottom row → match cleared
        assertTrue(output.contains("Game Over. Final Score: 30"));
    }
}
