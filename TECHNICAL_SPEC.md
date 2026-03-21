# Match-3 Falling Bricks Game — Technical Specification

## 1. Overview

A console-based falling-bricks puzzle game written in Java 17. The player controls
three-block bricks that fall onto a 2D grid. Each block carries a symbol (`~`, `^`,
`*`, `@`). When a brick lands, any horizontal or vertical line of 3+ matching symbols
is cleared. The game ends when all bricks are placed or a new brick cannot spawn.

**Build system:** Maven  
**Entry point:** `com.gic.assessment.match3.FallingBricksGame`  
**Package:** `com.gic.assessment.match3` (10 classes across 4 sub-packages)

---

## 2. Application Lifecycle

```
┌──────────────────────────────────────────────────────────┐
│               FallingBricksGame.main()                   │
│                                                          │
│  Print "Welcome to Match-3 game!"                        │
│                                                          │
│  ┌─── Outer replay loop ─────────────────────────────┐   │
│  │  Create new Game(scanner, out)                    │   │
│  │  game.run()                                       │   │
│  │    ├─ initialize()   ← prompt for field + bricks  │   │
│  │    ├─ gameLoop()     ← frame-by-frame play        │   │
│  │    └─ print "Game Over."                          │   │
│  │                                                   │   │
│  │  promptRestart() → S = loop again, Q = exit       │   │
│  └───────────────────────────────────────────────────┘   │
│                                                          │
│  Print "Thank you for playing Match-3!"                  │
└──────────────────────────────────────────────────────────┘
```

---

## 3. Coordinate System

```
         col 0   col 1   col 2   col 3   col 4
        ┌───────┬───────┬───────┬───────┬───────┐
row 0   │  .    │  .    │  .    │  .    │  .    │  ← TOP
        ├───────┼───────┼───────┼───────┼───────┤
row 1   │  .    │  .    │  .    │  .    │  .    │
        ├───────┼───────┼───────┼───────┼───────┤
  ...   │       │       │       │       │       │
        ├───────┼───────┼───────┼───────┼───────┤
row 7   │  .    │  .    │  ^    │  ^    │  *    │  ← BOTTOM
        └───────┴───────┴───────┴───────┴───────┘
```

- **row** increases downward (row 0 = top, row height-1 = bottom).
- **col** increases rightward (col 0 = left, col width-1 = right).
- Grid is stored as `char[height][width]`, accessed as `grid[row][col]`.
- Empty cells contain `'.'` (`Field.EMPTY`).

---

## 4. Class Architecture

### 4.1 Package Diagram

```
com.gic.assessment.match3                          ← entry point
    FallingBricksGame               (outer replay loop)

com.gic.assessment.match3.engine                   ← game logic
    Game                            (orchestrates one round)
    MatchChecker                    (detects and clears 3-in-a-row)

com.gic.assessment.match3.model                    ← domain objects
    Orientation (enum)              (HORIZONTAL, VERTICAL)
    Command     (enum)              (LEFT, RIGHT, DROP)
    Brick       (record)            (immutable: orientation + 3 symbols)
    Position    (record)            (immutable (row, col) coordinate)
    Field                           (2D grid of chars)
    ActiveBrick                     (mutable position wrapper around Brick)

com.gic.assessment.match3.io                       ← input / output
    InputParser                     (parses init line + frame commands)
    FieldRenderer                   (builds display string)
```

### 4.2 Dependency Flow

```
    FallingBricksGame (com.gic.assessment.match3)
      │
      ▼
    Game (com.gic.assessment.match3.engine)
      │
      ├──► InputParser, FieldRenderer   (com.gic.assessment.match3.io)
      ├──► MatchChecker                 (com.gic.assessment.match3.engine)
      └──► Field, Brick, ActiveBrick,   (com.gic.assessment.match3.model)
           Command
```

### 4.3 Class Details

| Package | Class | Type | Responsibility |
|---|---|---|---|
| `com.gic.assessment.match3` | `FallingBricksGame` | Class | Application entry point. Manages the outer replay loop (S/Q prompt). Creates one `Game` per round. |
| `com.gic.assessment.match3.engine` | `Game` | Class | Orchestrates a single round: initialisation, brick-by-brick game loop, game-over output. |
| `com.gic.assessment.match3.engine` | `MatchChecker` | Class (static) | Scans the field for horizontal/vertical runs of 3+ identical symbols and clears them. |
| `com.gic.assessment.match3.model` | `Orientation` | Enum | `HORIZONTAL` or `VERTICAL`. Converts from char (`'H'`/`'V'`). |
| `com.gic.assessment.match3.model` | `Command` | Enum | `LEFT`, `RIGHT`, or `DROP`. Converts from char (`'L'`/`'R'`/`'D'`); returns `null` for invalid chars. |
| `com.gic.assessment.match3.model` | `Brick` | Record | Immutable definition of a brick: one `Orientation` + three `char` symbols. Validates symbols on construction. |
| `com.gic.assessment.match3.model` | `Position` | Record | Immutable `(row, col)` coordinate. Used by `ActiveBrick.getOccupiedCells()` for type-safe cell references. |
| `com.gic.assessment.match3.model` | `Field` | Class | The playing grid. Stores a `char[height][width]` array. Provides bounds checking and cell read/write. |
| `com.gic.assessment.match3.model` | `ActiveBrick` | Class | Wraps a `Brick` with a mutable `(row, col)` anchor position. Handles movement, collision checks, dropping, and placement. |
| `com.gic.assessment.match3.io` | `InputParser` | Class (static) | Parses the initialisation line (`"5 8 H^^* V*@^"`) and per-frame command strings (`"LL"`, `"DR"`). |
| `com.gic.assessment.match3.io` | `FieldRenderer` | Class (static) | Renders the field + active brick to a multi-line `String` for console output. |

---

## 5. Key Variables Reference

### 5.1 Game

| Variable | Type | Description |
|---|---|---|
| `scanner` | `Scanner` | Reads user input from stdin (shared across the entire application). |
| `out` | `PrintStream` | Output destination (stdout). Injected for testability. |
| `field` | `Field` | The game grid for this round. |
| `bricks` | `List<Brick>` | Ordered list of brick definitions (1-5) to process. |
| `currentBrickIndex` | `int` | 0-based index of the next brick to spawn from the `bricks` list. |
| `activeBrick` | `ActiveBrick` | The brick currently falling; `null` between placements. |
| `frameNumber` | `int` | Monotonically increasing frame counter (starts at 0, first displayed frame is 1). |

### 5.2 Field

| Variable | Type | Description |
|---|---|---|
| `EMPTY` | `char` (`'.'`) | Constant representing an unoccupied cell. |
| `width` | `int` | Number of columns. |
| `height` | `int` | Number of rows. |
| `grid` | `char[][]` | The cell storage, indexed `grid[row][col]`. |

### 5.3 ActiveBrick

| Variable | Type | Description |
|---|---|---|
| `brick` | `Brick` | The immutable brick definition this object wraps. |
| `row` | `int` | Anchor row (top-left cell). Mutated by `moveDown()`. |
| `col` | `int` | Anchor column (top-left cell). Mutated by `moveLeft()`/`moveRight()`. |

### 5.4 Brick (record fields)

| Field | Type | Description |
|---|---|---|
| `orientation` | `Orientation` | `HORIZONTAL` or `VERTICAL`. |
| `symbol1` | `char` | First symbol (leftmost for H, topmost for V). |
| `symbol2` | `char` | Second symbol (middle). |
| `symbol3` | `char` | Third symbol (rightmost for H, bottommost for V). |

---

## 6. Game Loop — Detailed Flow

### 6.1 Initialisation

```
User input:  "5 8 H^^* V*@^"
                │ │  │      │
                │ │  │      └── Brick 2: Vertical, symbols *, @, ^
                │ │  └── Brick 1: Horizontal, symbols ^, ^, *
                │ └── height = 8
                └── width = 5
```

`InputParser.parseInitInput()` splits on whitespace, extracts width/height,
and parses each remaining token as a `Brick`.

### 6.2 Per-Brick Processing

```
For each brick in the list:

  1. SPAWN
     ├─ Horizontal: startCol = (width - 3) / 2,  startRow = 0
     └─ Vertical:   startCol = (width - 1) / 2,  startRow = 0
     If starting cells are blocked → game over (break).

  2. FRAME LOOP (processBrick)
     ┌──────────────────────────────────────────────┐
     │  frameNumber++                               │
     │  Print "Frame N"                             │
     │  Print rendered field (with active brick)    │
     │  Prompt user for commands                    │
     │  Read input → parse up to 2 commands         │
     │                                              │
     │  Apply each command:                         │
     │    L → move left  (if space is free)         │
     │    R → move right (if space is free)         │
     │    D → drop all the way down                 │
     │                                              │
     │  AUTO-DROP:                                  │
     │    Can move down 1 row?                      │
     │    ├─ YES → moveDown(), loop to next frame   │
     │    └─ NO  → brick is STATIONARY, exit loop   │
     └──────────────────────────────────────────────┘

  3. PLACE — write brick symbols onto the field grid.

  4. MATCH CHECK — scan for 3+ runs, clear matched cells.

  5. Next brick (currentBrickIndex++).
```

### 6.3 Final Frame & Game Over

After all bricks are placed (or a brick cannot spawn):

```
  frameNumber++
  Print "Frame N"
  Print rendered field (no active brick — shows final state)
  Print "Game Over."
```

---

## 7. Brick Spawn Position

The brick always starts at **row 0** (top of field), horizontally centred:

| Orientation | Formula | Example (width=5) | Result |
|---|---|---|---|
| Horizontal | `col = (width - 3) / 2` | `(5-3)/2 = 1` | Occupies cols 1, 2, 3 |
| Vertical | `col = (width - 1) / 2` | `(5-1)/2 = 1` | Occupies col 2, rows 0-2 |

If any starting cell is occupied by a previously placed brick, `createAtStart()` returns `null` and the game ends.

---

## 8. Command Processing

Each frame, the user may enter a string of characters. Only the first **2 valid** commands are processed.

| Command | Char | Effect | Collision Handling |
|---|---|---|---|
| LEFT | `L` | `col--` | Ignored if any cell would go out-of-bounds or overlap a placed brick |
| RIGHT | `R` | `col++` | Same |
| DROP | `D` | Repeatedly `row++` until blocked | Stops above the first obstruction or field bottom |

Commands are applied **in order**, then the automatic 1-row drop occurs.

---

## 9. Match Detection Algorithm

`MatchChecker.checkAndClear()` uses a 3-pass approach:

```
Pass 1 — Horizontal scan
  For each row, slide left-to-right counting consecutive identical symbols.
  If run >= 3, mark cells in toRemove[][].

Pass 2 — Vertical scan
  For each column, slide top-to-bottom with the same logic.

Pass 3 — Clear
  Set every marked cell to EMPTY ('.').
  Return the count of cleared cells.
```

**Key rules:**
- All matches are found **before** any clearing (deferred removal via `boolean[][]`).
- Overlapping matches (e.g. a T or cross shape) are handled correctly.
- **No gravity** is applied after clearing — floating symbols stay in place.

---

## 10. Display Format

Each row is rendered as:

```
N\t| c c c c c |
```

| Component | Description |
|---|---|
| `N` | 1-indexed row number, right-aligned (padded for multi-digit heights) |
| `\t` | Tab character |
| `\|` | Border characters |
| `c` | Cell content: symbol char or `'.'`, separated by spaces |

Example (5-wide, 8-tall field):

```
1	| . ^ ^ * . |
2	| . . . . . |
3	| . . . . . |
4	| . . . . . |
5	| . . . . . |
6	| . . . . . |
7	| . . . . . |
8	| . . . . . |
```

The `FieldRenderer` builds a **display grid** by:
1. Copying all cells from the `Field` (placed bricks).
2. Overlaying the `ActiveBrick`'s symbols at its current position (if not null).

---

## 11. Example Walkthrough

**Input:** `5 8 H^^* V*@^`

| Frame | Active Brick | User Input | What Happens |
|---|---|---|---|
| 1 | H`^^*` at (0,1) | `LL` | L moves to col 0; 2nd L ignored (would exit field). Auto-drop → row 1. |
| 2 | H`^^*` at (1,0) | `R` | R moves to col 1. Auto-drop → row 2. |
| 3 | H`^^*` at (2,1) | `DR` | D drops to row 7; R moves to col 2. Auto-drop fails → **stationary at (7, 2-4)**. Field: row 8 = `. . ^ ^ *`. No matches. Brick 2 spawns. |
| 4 | V`*@^` at (0,2) | `LLR` | L→col 1, L→col 0; R is 3rd command, ignored. Auto-drop → row 1. |
| 5 | V`*@^` at (1,0) | *(empty)* | No commands. Auto-drop → row 2. |
| 6 | V`*@^` at (2,0) | `R` | R moves to col 1. Auto-drop → row 3. |
| 7 | V`*@^` at (3,1) | `DR` | D drops to rows 5-7; R blocked (col 2 row 7 has `^`). Auto-drop fails → **stationary at (5-7, 1)**. Row 8 becomes `. ^ ^ ^ *` → 3 `^` matched → cleared. |
| 8 | *(none)* | — | Final frame shows cleared field. **Game Over.** |

**Final field state:**

```
1	| . . . . . |
2	| . . . . . |
3	| . . . . . |
4	| . . . . . |
5	| . . . . . |
6	| . * . . . |
7	| . @ . . . |
8	| . . . . * |
```

---

## 12. Project Structure

```
falling-bricks-game-java/
├── pom.xml                                    ← Maven build (Java 17, JUnit 5)
├── TECHNICAL_SPEC.md                          ← This document
└── src/
    └── main/
        └── java/
            └── com/gic/assessment/match3/
                ├── FallingBricksGame.java     ← Entry point + replay loop
                │
                ├── engine/                    ← Game logic
                │   ├── Game.java              ← Round orchestrator
                │   └── MatchChecker.java      ← 3-in-a-row detection
                │
                ├── model/                     ← Domain objects
                │   ├── Orientation.java       ← H / V enum
                │   ├── Command.java           ← L / R / D enum
                │   ├── Brick.java             ← Immutable brick record
                │   ├── Position.java          ← Immutable (row, col) coordinate
                │   ├── Field.java             ← 2D grid model
                │   └── ActiveBrick.java       ← Falling brick with position
                │
                └── io/                        ← Input / output
                    ├── InputParser.java        ← Input parsing utilities
                    └── FieldRenderer.java      ← Console display builder
```

---

## 13. Build & Run

```bash
# Compile and package
mvn clean package

# Run the game
java -jar target/falling-bricks-game-1.0-SNAPSHOT.jar

# Run tests
mvn test
```
