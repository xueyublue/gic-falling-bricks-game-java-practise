package com.gic.assessment.match3.model;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Immutable definition of a brick: a set of cells given as offsets from an anchor
 * (top-left of the brick's bounding box), plus one symbol per cell.
 *
 * <p>Legacy 3-cell line bricks use {@link Orientation#HORIZONTAL} or {@link Orientation#VERTICAL}.
 * Additional shapes: {@link #lShape(char, char, char, char)}, {@link #tShape(char, char, char, char)},
 * {@link #square2x2(char, char, char, char)}.
 */
public final class Brick {

    /** How this brick is classified for parsing, {@link #isHorizontal()}, and {@link #isVertical()}. */
    public enum Kind {
        LINE_HORIZONTAL,
        LINE_VERTICAL,
        L_SHAPE,
        T_SHAPE,
        SQUARE_2X2
    }

    private static final String ALLOWED_SYMBOLS = "~^*@";

    private final List<Position> relativeOffsets;
    private final char[] symbols;
    private final Kind kind;

    /**
     * Legacy constructor: a 3-cell horizontal or vertical line (same as the original record).
     */
    public Brick(Orientation orientation, char symbol1, char symbol2, char symbol3) {
        this(
                orientation == Orientation.HORIZONTAL
                        ? List.of(new Position(0, 0), new Position(0, 1), new Position(0, 2))
                        : List.of(new Position(0, 0), new Position(1, 0), new Position(2, 0)),
                new char[]{symbol1, symbol2, symbol3},
                orientation == Orientation.HORIZONTAL ? Kind.LINE_HORIZONTAL : Kind.LINE_VERTICAL);
    }

    private Brick(List<Position> relativeOffsets, char[] symbols, Kind kind) {
        validateSymbols(symbols);
        Objects.requireNonNull(relativeOffsets, "offsets");
        if (relativeOffsets.size() != symbols.length) {
            throw new IllegalArgumentException("Offsets and symbols length mismatch");
        }
        this.relativeOffsets = List.copyOf(relativeOffsets);
        this.symbols = symbols.clone();
        this.kind = kind;
    }

    /**
     * L-shape (4 cells), opening to the top-right:
     * <pre>
     * X
     * X
     * X X
     * </pre>
     * Symbols map top-to-bottom on the stem, then the corner cell: (0,0), (1,0), (2,0), (2,1).
     */
    public static Brick lShape(char top, char mid, char stemBottom, char corner) {
        List<Position> o = List.of(
                new Position(0, 0),
                new Position(1, 0),
                new Position(2, 0),
                new Position(2, 1));
        return new Brick(o, new char[]{top, mid, stemBottom, corner}, Kind.L_SHAPE);
    }

    /**
     * T-shape (4 cells):
     * <pre>
     * . X .
     * X X X
     * </pre>
     * Symbols in order: top centre, then bottom row left to right.
     */
    public static Brick tShape(char top, char bottomLeft, char bottomMid, char bottomRight) {
        List<Position> o = List.of(
                new Position(0, 1),
                new Position(1, 0),
                new Position(1, 1),
                new Position(1, 2));
        return new Brick(o, new char[]{top, bottomLeft, bottomMid, bottomRight}, Kind.T_SHAPE);
    }

    /**
     * 2×2 square (4 cells), row-major: top-left, top-right, bottom-left, bottom-right.
     */
    public static Brick square2x2(char topLeft, char topRight, char bottomLeft, char bottomRight) {
        List<Position> o = List.of(
                new Position(0, 0),
                new Position(0, 1),
                new Position(1, 0),
                new Position(1, 1));
        return new Brick(o, new char[]{topLeft, topRight, bottomLeft, bottomRight}, Kind.SQUARE_2X2);
    }

    public Kind kind() {
        return kind;
    }

    /** Number of cells (symbols) in this brick. */
    public int cellCount() {
        return symbols.length;
    }

    /** Width of the bounding box in columns (anchor is top-left of this box). */
    public int boundingWidth() {
        int maxCol = 0;
        for (Position p : relativeOffsets) {
            maxCol = Math.max(maxCol, p.col());
        }
        return maxCol + 1;
    }

    /** Height of the bounding box in rows. */
    public int boundingHeight() {
        int maxRow = 0;
        for (Position p : relativeOffsets) {
            maxRow = Math.max(maxRow, p.row());
        }
        return maxRow + 1;
    }

    public List<Position> relativeOffsets() {
        return relativeOffsets;
    }

    public Position relativeOffset(int index) {
        return relativeOffsets.get(index);
    }

    /**
     * Absolute field positions when the brick's anchor is at {@code (anchorRow, anchorCol)}.
     */
    public List<Position> absoluteCells(int anchorRow, int anchorCol) {
        return relativeOffsets.stream()
                .map(p -> new Position(anchorRow + p.row(), anchorCol + p.col()))
                .toList();
    }

    /**
     * Symbol at index {@code i} matches {@link #relativeOffset(int)} and {@link #getOccupiedCells}
     * order for a given anchor.
     */
    public char symbolAt(int index) {
        if (index < 0 || index >= symbols.length) {
            throw new IndexOutOfBoundsException("Symbol index must be 0.." + (symbols.length - 1) + ", got: " + index);
        }
        return symbols[index];
    }

    public boolean isHorizontal() {
        return kind == Kind.LINE_HORIZONTAL;
    }

    public boolean isVertical() {
        return kind == Kind.LINE_VERTICAL;
    }

    private static void validateSymbols(char[] syms) {
        for (char c : syms) {
            if (ALLOWED_SYMBOLS.indexOf(c) < 0) {
                throw new IllegalArgumentException("Invalid symbol: " + c + ". Allowed: " + ALLOWED_SYMBOLS);
            }
        }
    }

    private static char kindLetter(Kind k) {
        return switch (k) {
            case LINE_HORIZONTAL -> 'H';
            case LINE_VERTICAL -> 'V';
            case L_SHAPE -> 'L';
            case T_SHAPE -> 'T';
            case SQUARE_2X2 -> 'Q';
        };
    }

    /**
     * Compact token form: {@code H}/{@code V} + 3 symbols, or {@code L}/{@code T}/{@code Q} + 4 symbols.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(kindLetter(kind));
        for (char c : symbols) {
            sb.append(c);
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Brick brick = (Brick) o;
        return kind == brick.kind
                && relativeOffsets.equals(brick.relativeOffsets)
                && Arrays.equals(symbols, brick.symbols);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(kind, relativeOffsets);
        result = 31 * result + Arrays.hashCode(symbols);
        return result;
    }
}
