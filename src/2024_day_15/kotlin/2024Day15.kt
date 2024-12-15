// AOC Year 2024 Day 15
fun main() {
    val lines = {}::class.java.getResourceAsStream("input.txt")!!.bufferedReader().readLines()

    val storehouse = lines.takeWhile { it.isNotEmpty() }.toStorehouse()
    val robotSequence = lines.takeLastWhile { it.isNotEmpty() }
        .joinToString(separator = "")
        .map { it.toDirection() }

    println("Before:")
    storehouse.print()

    robotSequence.forEach {
        storehouse.moveRobot(it)
    }

    println()
    println("After:")
    storehouse.print()
    println()

    val gpsSum = storehouse.gpsSum()
    println("GPS sum: $gpsSum")
}

enum class Entity(val c: Char) {
    ROBOT('@'),
    BOX('O'),
    WALL('#'),
    EMPTY('.')
}

fun List<String>.toStorehouse() = Storehouse(map {
    it.map { c -> c.toEntity() }.toMutableList()
})

fun Char.toEntity() = when (this) {
    Entity.WALL.c -> Entity.WALL
    Entity.BOX.c -> Entity.BOX
    Entity.ROBOT.c -> Entity.ROBOT
    Entity.EMPTY.c -> Entity.EMPTY
    else -> throw AssertionError()
}

data class Pos(val x: Int, val y: Int, val entity: Entity)

class Storehouse(private val grid: List<MutableList<Entity>>) {
    private var robotPos: Pair<Int, Int>

    private fun List<List<Entity>>.flatPos() = flatMapIndexed { y, row ->
        row.mapIndexed { x, entity -> Pos(x, y, entity) }
    }

    private operator fun List<List<Entity>>.get(pos: Pair<Int, Int>) = this[pos.second][pos.first]
    private operator fun List<MutableList<Entity>>.set(pos: Pair<Int, Int>, entity: Entity) {
        this[pos.second][pos.first] = entity
    }

    private fun Pair<Int, Int>.next(dir: Direction) = when (dir) {
        Direction.UP -> first to second - 1
        Direction.DOWN -> first to second + 1
        Direction.LEFT -> first - 1 to second
        Direction.RIGHT -> first + 1 to second
    }

    init {
        val pos = grid.flatPos().find { it.entity == Entity.ROBOT }!!
        robotPos = pos.x to pos.y
    }

    fun print() {
        for (y in grid.indices) {
            for (x in grid[y].indices) {
                print(grid[y][x].c)
            }
            println()
        }
    }

    fun gpsSum() = grid.flatPos()
        .filter { it.entity == Entity.BOX }
        .sumOf { it.y * 100 + it.x }

    fun moveRobot(dir: Direction) {
        val posToMove = generateSequence(robotPos) { pos ->
            pos.next(dir).takeUnless { nextPos ->
                grid[nextPos].let { it == Entity.EMPTY || it == Entity.WALL }
            }
        }.toList()

        val stoppingPos = posToMove.last().next(dir)
        if (grid[stoppingPos] == Entity.WALL) return // Nothing can be moved

        posToMove.asReversed().forEach { pos ->
            val nextPos = pos.next(dir)
            grid[nextPos] = grid[pos]
            grid[pos] = Entity.EMPTY
        }

        if (posToMove.isNotEmpty()) {
            robotPos = robotPos.next(dir)
        }
    }
}

fun Char.toDirection() = when (this) {
    '^' -> Direction.UP
    '<' -> Direction.LEFT
    '>' -> Direction.RIGHT
    'v' -> Direction.DOWN
    else -> throw AssertionError()
}

enum class Direction {
    UP,
    DOWN,
    LEFT,
    RIGHT
}