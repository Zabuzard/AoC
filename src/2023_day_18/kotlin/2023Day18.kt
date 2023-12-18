import kotlin.math.abs
import kotlin.math.absoluteValue

// AOC Year 2023 Day 18
fun main() {
    val lines = {}::class.java.getResourceAsStream("input.txt")!!.bufferedReader().readLines()

    var pos = 0L to 0L
    val polyLines = lines.map {
        it.toLine(pos).also { line -> pos = line.end }
    }

    /*
    val outlines = polyLines.flatMap { listOf(it.start, it.start, it.end, it.end) }
    val maxX = outlines.maxOf { it.first }
    val minX = outlines.minOf { it.first }
    val maxY = outlines.maxOf { it.second }
    val minY = outlines.minOf { it.second }

    val grid = (minY..maxY).map { ".".repeat((maxX - minX) + 1).toMutableList() }

    polyLines.forEach { it.path.forEach { (x, y) -> grid[y - minY][x - minX] = '#' } }

    grid.forEach { row ->
        row.joinToString(separator = "").also { println(it) }
    }
    */

    val outline = polyLines.map { it.start } + (0L to 0L)
    val outlineLength = outline.zipWithNext { a, b -> abs(b.first - a.first) + abs(b.second - a.second) }.sum()
    println("Outline length: $outlineLength")

    // Shoelace theorem
    val area = outline.map { it.first to it.second }.zipWithNext()
        .sumOf { (left, right) -> left.first * right.second - right.first * left.second }.absoluteValue / 2
    println("Area is: $area")

    // Picks theorem (I = A - (R/2) + 1)
    val pointsInside = area - (outlineLength / 2) + 1
    println("Points inside: $pointsInside")

    val totalPoints = outlineLength + pointsInside
    println("Total points: $totalPoints")
}

data class Line(val start: Pair<Long, Long>, val end: Pair<Long, Long>)

fun String.toLine(pos: Pair<Long, Long>): Line {
    var (_, dir, countText, color) = "([RLDU]) (\\d+) \\((#.+)\\)".toRegex()
        .matchEntire(this)!!.groupValues
    var count = countText.toLong()

    val fromColor = color.toInstruction()
    dir = fromColor.first
    count = fromColor.second

    val end = when (dir) {
        "R" -> pos.first + count to pos.second
        "L" -> pos.first - count to pos.second
        "U" -> pos.first to pos.second - count
        else -> pos.first to pos.second + count
    }

    return Line(pos, end)
}

fun String.toInstruction(): Pair<String, Long> {
    val dir = when (last()) {
        '0' -> "R"
        '1' -> "D"
        '2' -> "L"
        else -> "U"
    }

    val count = drop(1).dropLast(1).toLong(radix = 16)
    return dir to count
}