import java.math.BigInteger
import kotlin.math.max

// AOC Year 2025 Day 6
fun main() {
    val lines = {}::class.java.getResourceAsStream("input.txt")!!.bufferedReader().readLines()

    val grid = lines.splitProperly()

    val totalPart1 = part1(grid)
    println("Grand Total Part1: $totalPart1")

    val totalPart2 = part2(grid)
    println("Grand Total Part2: $totalPart2")
}

fun List<String>.splitProperly(): List<List<String>> {
    val splitIndices = last().withIndex().filter { it.value != ' ' }.map { it.index }

    return map { line ->
        val result = splitIndices.zipWithNext()
            .map { (start, end) -> line.substring(start, end - 1) }
            .toMutableList()
        result += line.substring(splitIndices.last())
        result
    }
}

fun part1(grid: List<List<String>>): Long {
    val height = grid.size
    val width = grid.first().size

    var total = 0L
    for (x in 0..<width) {
        var result = grid[0][x].trim().toLong()

        val op = grid[height - 1][x].trim().first()
        for (y in 1..<height - 1) {
            val num = grid[y][x].trim().toLong()

            when (op) {
                '+' -> result += num
                '-' -> result -= num
                '*' -> result *= num
                '/' -> result /= num
            }
        }

        total += result
    }

    return total
}

fun part2(grid: List<List<String>>): Long {
    val height = grid.size
    val width = grid.first().size

    var total = 0L

    for (x in 0..<width) {
        var digits = 0
        for (y in 0..<height - 1) {
            digits = max(digits, grid[y][x].trim().length)
        }

        val op = grid[height - 1][x].trim().first()
        var result = if (op == '*' || op == '/') 1L else 0L
        for (d in 0..<digits) {
            var numText = ""
            for (y in 0..<height - 1) {
                val c = grid[y][x].padStart(digits)[d]
                if (c == ' ') {
                    continue
                }
                numText += c
            }

            val num = numText.toLong()
            when (op) {
                '+' -> result += num
                '-' -> result -= num
                '*' -> result *= num
                '/' -> result /= num
            }
        }

        total += result
    }

    return total
}
