package com.gic.assessment.match3.model;

/**
 * Represents a single user command that can be applied to the active brick.
 *
 * LEFT  — move the brick one column to the left.
 * RIGHT — move the brick one column to the right.
 * DROP  — drop the brick as far down as possible in one step.
 */
public enum Command {
    LEFT,
    RIGHT,
    DROP;

    /**
     * Converts a character to the corresponding Command.
     *
     * @param c 'L' for LEFT, 'R' for RIGHT, 'D' for DROP (case-insensitive)
     * @return the matching Command, or null if the character is not a valid command
     */
    public static Command fromChar(char c) {
        return switch (Character.toUpperCase(c)) {
            case 'L' -> LEFT;
            case 'R' -> RIGHT;
            case 'D' -> DROP;
            default -> null; // invalid characters are silently ignored
        };
    }
}
