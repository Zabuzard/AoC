import java.awt.SystemColor.text

// AOC Year 2024 Day 4
fun main() {
    val lines = {}::class.java.getResourceAsStream("input.txt")!!.bufferedReader().readLines()

    val grid = lines.toGrid()

    val rows = (0..<grid.height)
        .map { grid.getRow(it) }
    val columns = (0..<grid.width)
        .map { grid.getColumn(it) }
    val diagonals1 = (0..<grid.width)
        .map { it to 0 }.map { (x, y) -> grid.getDiagonalTopLeftToBottomRight(x, y) }
    val diagonals2 = (1..<grid.height)
        .map { 0 to it }.map { (x, y) -> grid.getDiagonalTopLeftToBottomRight(x, y) }
    val diagonals3 = (0..<grid.height)
        .map { 0 to it }.map { (x, y) -> grid.getDiagonalBottomLeftToTopRight(x, y) }
    val diagonals4 = (1..<grid.width)
        .map { it to grid.height - 1 }.map { (x, y) -> grid.getDiagonalBottomLeftToTopRight(x, y) }

    val count = sequenceOf(rows, columns, diagonals1, diagonals2, diagonals3, diagonals4).flatten()
        .flatMap { sequenceOf(it, it.reversed()) }
        .sumOf { it.countOccurrences("XMAS") }

    println("Count: $count")
}

fun String.countOccurrences(needle: String) = windowedSequence(needle.length).count { it == needle }

fun List<String>.toGrid() = Grid(map { it.toList() }.toList())

data class Grid(private val grid: List<List<Char>>) {
    val width = grid.first().size
    val height = grid.size

    fun isInBounds(x: Int, y: Int) = x in 0..<width && y in 0..<height
    private fun Pair<Int, Int>.isInBounds() = isInBounds(first, second)
    operator fun get(x: Int, y: Int) = grid[y][x]

    fun getRow(y: Int) = grid[y].joinToString(separator = "")
    fun getColumn(x: Int) = grid.map { it[x] }.joinToString(separator = "")
    fun getDiagonalTopLeftToBottomRight(startX: Int, startY: Int) =
        generateSequence(startX to startY) { (x, y) ->
            (x + 1 to y + 1).takeIf { it.isInBounds() }
        }.map { (x, y) -> this[x, y] }.joinToString(separator = "")

    fun getDiagonalBottomLeftToTopRight(startX: Int, startY: Int) =
        generateSequence(startX to startY) { (x, y) ->
            (x + 1 to y - 1).takeIf { it.isInBounds() }
        }.map { (x, y) -> this[x, y] }.joinToString(separator = "")
}
