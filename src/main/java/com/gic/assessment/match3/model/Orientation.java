package com.gic.assessment.match3.model;

/**
 * Represents how a brick is oriented on the field.
 *
 * HORIZONTAL — the 3 blocks are laid out in a single row  (left to right).
 * VERTICAL   — the 3 blocks are stacked in a single column (top to bottom).
 */
public enum Orientation {
    HORIZONTAL, // brick occupies 1 row  x 3 columns
    VERTICAL;   // brick occupies 3 rows x 1 column

    /**
     * Converts a character to the corresponding Orientation.
     *
     * @param c 'H' (or 'h') for HORIZONTAL, 'V' (or 'v') for VERTICAL
     * @return the matching Orientation enum value
     * @throws IllegalArgumentException if the character is not 'H' or 'V'
     */
    public static Orientation fromChar(char c) {
        return switch (Character.toUpperCase(c)) {
            case 'H' -> HORIZONTAL;
            case 'V' -> VERTICAL;
            default -> throw new IllegalArgumentException("Invalid orientation: " + c);
        };
    }
}
