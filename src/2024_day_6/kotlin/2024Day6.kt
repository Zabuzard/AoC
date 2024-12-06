// AOC Year 2024 Day 6
fun main() {
    val lines = {}::class.java.getResourceAsStream("input.txt")!!.bufferedReader().readLines()

    val grid = lines.map { it.toList() }

    var guardPosition =
        grid.flatMapIndexed { y, row -> row.mapIndexed { x, c -> Position(x, y) to c } }
            .find { it.second == '^' }!!.first
    var guardDirection = Direction.UP
    var guardPositionWithDirection = PositionWithDirection(guardPosition.x, guardPosition.y, guardDirection)

    val guardRoute = mutableSetOf<Position>()
    val guardRouteWithDirection = mutableSetOf<PositionWithDirection>()
    val possibleBlockPositions = mutableSetOf<Position>()
    while (true) {
        guardRoute += guardPosition
        guardRouteWithDirection += guardPositionWithDirection
        val nextPosition = guardPosition.move(guardDirection)

        val directionIfBlocked = guardDirection.turnRight()
        val positionIfBlocked = guardPosition.move(directionIfBlocked)
        val guardIfBlocked = PositionWithDirection(positionIfBlocked.x, positionIfBlocked.y, directionIfBlocked)
        if (guardRouteWithDirection.contains(guardIfBlocked)) {
            possibleBlockPositions += nextPosition
        }

        when (grid.getOrNull(nextPosition.y)?.getOrNull(nextPosition.x)) {
            '#' -> guardDirection = guardDirection.turnRight()
            '.', '^' -> {
                guardPosition = nextPosition
                guardPositionWithDirection = PositionWithDirection(guardPosition.x, guardPosition.y, guardDirection)
            }
            null -> break
        }
    }

    printGrid(grid, guardRoute)
    println("Guard route: ${guardRoute.size}")
    println("Possible block positions: ${possibleBlockPositions.size}")
}

fun printGrid(grid: List<List<Char>>, guardRoute: Set<Position>) {
    grid.mapIndexed { y, row ->
        row.mapIndexed { x, c ->
            if (guardRoute.contains(Position(x, y))) 'X' else c
        }.joinToString(separator = "")
    }.forEach { println(it) }
}

enum class Direction {
    UP,
    DOWN,
    LEFT,
    RIGHT;

    fun turnRight() = when (this) {
        UP -> RIGHT
        DOWN -> LEFT
        LEFT -> UP
        RIGHT -> DOWN
    }
}

data class Position(val x: Int, val y: Int) {
    fun move(direction: Direction) =
        when (direction) {
            Direction.UP -> 0 to -1
            Direction.DOWN -> 0 to 1
            Direction.LEFT -> -1 to 0
            Direction.RIGHT -> 1 to 0
        }.let { Position(x + it.first, y + it.second) }
}

data class PositionWithDirection(val x: Int, val y: Int, val direction: Direction) {
    fun move(direction: Direction) =
        when (direction) {
            Direction.UP -> 0 to -1
            Direction.DOWN -> 0 to 1
            Direction.LEFT -> -1 to 0
            Direction.RIGHT -> 1 to 0
        }.let { PositionWithDirection(x + it.first, y + it.second, direction) }
}
