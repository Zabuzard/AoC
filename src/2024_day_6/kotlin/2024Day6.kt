// AOC Year 2024 Day 6
fun main() {
    val lines = {}::class.java.getResourceAsStream("input.txt")!!.bufferedReader().readLines()

    val grid = lines.map { it.toList() }

    val guardPosition =
        grid.flatMapIndexed { y, row -> row.mapIndexed { x, c -> Position(x, y) to c } }
            .find { it.second == '^' }!!
            .first.withDirection(Direction.UP)
    val guardRoute = findGuardRoute(grid, guardPosition)!!


    printGrid(grid, guardRoute)
    println("Guard route: ${guardRoute.size}")

    val possibleBlockPositions = guardRoute.toMutableSet() - guardPosition.withoutDirection()
    val blockPositions = possibleBlockPositions.mapNotNull { blockPos ->
        val gridWithBlock = grid.map { it.toMutableList() }.toList()
        gridWithBlock[blockPos.y][blockPos.x] = '#'
        if (findGuardRoute(gridWithBlock, guardPosition) == null) {
            blockPos
        } else {
            null
        }
    }.toSet()

    println("Possible block positions: ${blockPositions.size}")
}

fun findGuardRoute(grid: List<List<Char>>, guardStart: PositionWithDirection): Set<Position>? {
    var guardPosition = guardStart.withoutDirection()
    var guardDirection = guardStart.direction
    var guardPositionWithDirection = guardStart

    val guardRoute = mutableSetOf<Position>()
    val guardRouteWithDirection = mutableSetOf<PositionWithDirection>()
    val possibleBlockPositions = mutableSetOf<Position>()
    while (true) {
        guardRoute += guardPosition
        guardRouteWithDirection += guardPositionWithDirection
        val nextPosition = guardPosition.move(guardDirection)

        when (grid.getOrNull(nextPosition.y)?.getOrNull(nextPosition.x)) {
            '#' -> guardDirection = guardDirection.turnRight()
            '.', '^' -> {
                guardPosition = nextPosition
                guardPositionWithDirection = guardPosition.withDirection(guardDirection)
                possibleBlockPositions += nextPosition

                if (guardPositionWithDirection in guardRouteWithDirection) {
                    // Cycle detected
                    return null
                }
            }

            null -> break
        }
    }
    return guardRoute
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

    fun withDirection(direction: Direction) = PositionWithDirection(x, y, direction)
}

data class PositionWithDirection(val x: Int, val y: Int, val direction: Direction) {
    fun move(direction: Direction) =
        when (direction) {
            Direction.UP -> 0 to -1
            Direction.DOWN -> 0 to 1
            Direction.LEFT -> -1 to 0
            Direction.RIGHT -> 1 to 0
        }.let { PositionWithDirection(x + it.first, y + it.second, direction) }

    fun withoutDirection() = Position(x, y)
}
