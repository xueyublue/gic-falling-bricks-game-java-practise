# Match-3 Falling Bricks Game

A console-based falling-bricks puzzle game written in Java 17 with Maven. The player controls three-block bricks that fall onto a 2D grid. Each block carries one of four symbols (`~`, `^`, `*`, `@`). When a brick lands, any horizontal or vertical line of 3+ matching symbols is cleared.

## Prerequisites

- Java 17+
- Maven 3.6+

## Build & Run

```bash
mvn clean package
java -jar target/falling-bricks-game-1.0-SNAPSHOT.jar
```

## Run Tests

```bash
mvn test
```

154 tests across 11 test classes covering models, engine logic, I/O parsing, and full integration scenarios.

## How to Play

1. Enter the field dimensions and up to 5 brick definitions:
   ```
   5 8 H^^* V*@^
   ```
   - `5 8` = width 5, height 8
   - `H^^*` = horizontal brick with symbols `^`, `^`, `*`
   - `V*@^` = vertical brick with symbols `*`, `@`, `^`

2. Each frame, enter up to 2 commands:
   - `L` — move left
   - `R` — move right
   - `D` — drop to the lowest position

3. After commands, the brick auto-drops one row. When it can no longer fall, it is placed on the field, matches are checked, and the next brick spawns.

4. The game ends when all bricks are placed or a new brick cannot spawn.

## Design Highlights

- **Immutable domain objects** — `Brick` and `Position` are Java records; `ActiveBrick` wraps a `Brick` with mutable position state
- **Testable I/O** — `Scanner` and `PrintStream` are injected, enabling full integration tests without System.in/out
- **Deferred match removal** — `MatchChecker` marks all matches before clearing, correctly handling overlapping patterns (T-shapes, crosses)
- **No gravity after clearing** — cleared cells become empty; floating symbols remain in place per the game rules

## Project Structure

```
src/main/java/com/gic/assessment/match3/
├── FallingBricksGame.java          Entry point + replay loop
├── engine/
│   ├── Game.java                   Round orchestrator
│   └── MatchChecker.java           3-in-a-row detection & clearing
├── model/
│   ├── Orientation.java            H / V enum
│   ├── Command.java                L / R / D enum
│   ├── Brick.java                  Immutable brick definition (record)
│   ├── Position.java               Immutable (row, col) coordinate (record)
│   ├── Field.java                  2D grid model
│   └── ActiveBrick.java            Falling brick with mutable position
└── io/
    ├── InputParser.java            Input parsing utilities
    └── FieldRenderer.java          Console display builder
```

See [TECHNICAL_SPEC.md](TECHNICAL_SPEC.md) for the full specification, algorithm details, and example walkthrough.
