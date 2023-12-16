// AOC Year 2023 Day 14
fun main() {
    val lines = {}::class.java.getResourceAsStream("input.txt")!!.bufferedReader().readLines()

    val grid: MutableList<MutableList<Tile>> = lines.map {
        it.map { c ->
            when (c) {
                'O' -> Tile.ROLLING_ROCK
                '#' -> Tile.SOLID_ROCK
                else -> Tile.EMPTY
            }
        }.toMutableList()
    }.toMutableList()

    val rocksToMove = ArrayDeque<RollingRock>()
    grid.forEachIndexed { y, row ->
        row.forEachIndexed { x, tile ->
            if (tile == Tile.ROLLING_ROCK)
                rocksToMove += RollingRock(x, y)
        }
    }
    val allRocks = rocksToMove.toList()

    while (rocksToMove.isNotEmpty()) {
        val rock = rocksToMove.removeFirst()
        val wasMovedUp = rock.attemptMoveUp(grid)
        if (wasMovedUp) {
            rocksToMove.addLast(rock)
        }
    }

    /*
    grid.forEach { row ->
        row.map { tile ->
            when (tile) {
                Tile.SOLID_ROCK -> '#'
                Tile.ROLLING_ROCK -> 'O'
                Tile.EMPTY -> '.'
            }.let { print(it) }
        }.also { println() }
    }
    */

    val height = grid.size
    val sum = allRocks.sumOf { height - it.y }
    println("Sum is: $sum")
}

data class RollingRock(var x: Int, var y: Int) {
    fun attemptMoveUp(grid: MutableList<MutableList<Tile>>): Boolean {
        if (y == 0) return false
        val tileAbove = grid[y - 1][x]
        if (tileAbove != Tile.EMPTY) return false

        grid[y - 1][x] = Tile.ROLLING_ROCK
        grid[y][x] = Tile.EMPTY
        y--
        return true
    }
}

enum class Tile {
    ROLLING_ROCK,
    SOLID_ROCK,
    EMPTY
}