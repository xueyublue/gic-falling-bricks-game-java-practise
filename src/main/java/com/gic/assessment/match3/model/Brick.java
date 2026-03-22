package com.gic.assessment.match3.model;

/**
 * Immutable definition of a brick: an orientation plus exactly three symbols.
 *
 * A brick is the game's fundamental piece.  It is always composed of three
 * blocks, each carrying one of the allowed symbols (~, ^, *, @).
 *
 * This is a Java record, so the constructor parameters (orientation, symbol1,
 * symbol2, symbol3) automatically become final fields with generated getters.
 *
 * @param orientation whether this brick is HORIZONTAL or VERTICAL
 * @param symbol1     the first  block's symbol (leftmost / topmost)
 * @param symbol2     the second block's symbol (middle)
 * @param symbol3     the third  block's symbol (rightmost / bottommost)
 */
public record Brick(Orientation orientation, char symbol1, char symbol2, char symbol3) {

    // The four characters that are allowed as block symbols
    private static final String ALLOWED_SYMBOLS = "~^*@";

    /**
     * Compact constructor — validates all three symbols at construction time.
     * If any symbol is not in ALLOWED_SYMBOLS, an exception is thrown.
     */
    public Brick {
        validateSymbol(symbol1);
        validateSymbol(symbol2);
        validateSymbol(symbol3);
    }

    /**
     * Returns the symbol at the given positional index (0, 1, or 2).
     *
     * For a horizontal brick: 0 = left, 1 = center, 2 = right.
     * For a vertical   brick: 0 = top,  1 = middle, 2 = bottom.
     *
     * @param index 0-based position within the brick (must be 0, 1, or 2)
     * @return the character symbol at that position
     */
    public char symbolAt(int index) {
        return switch (index) {
            case 0 -> symbol1;
            case 1 -> symbol2;
            case 2 -> symbol3;
            default -> throw new IndexOutOfBoundsException("Symbol index must be 0-2, got: " + index);
        };
    }

    /** @return true if this brick's orientation is HORIZONTAL */
    public boolean isHorizontal() {
        return orientation == Orientation.HORIZONTAL;
    }

    /** @return true if this brick's orientation is VERTICAL */
    public boolean isVertical() {
        return orientation == Orientation.VERTICAL;
    }

    /**
     * Checks that a character is one of the allowed symbols.
     *
     * @param c the character to validate
     * @throws IllegalArgumentException if c is not in ALLOWED_SYMBOLS
     */
    private static void validateSymbol(char c) {
        if (ALLOWED_SYMBOLS.indexOf(c) < 0) {
            throw new IllegalArgumentException("Invalid symbol: " + c + ". Allowed: " + ALLOWED_SYMBOLS);
        }
    }

    /**
     * Returns a new Brick with the orientation flipped (H↔V),
     * keeping the same symbols in the same order.
     *
     * @return a rotated copy of this brick
     */
    public Brick rotated() {
        Orientation flipped = isHorizontal() ? Orientation.VERTICAL : Orientation.HORIZONTAL;
        return new Brick(flipped, symbol1, symbol2, symbol3);
    }

    /**
     * Returns a human-readable string such as "H^^*" or "V*@^".
     * The first character is the orientation letter, followed by the three symbols.
     */
    @Override
    public String toString() {
        char orientationChar = isHorizontal() ? 'H' : 'V';
        return String.valueOf(orientationChar) + symbol1 + symbol2 + symbol3;
    }
}
