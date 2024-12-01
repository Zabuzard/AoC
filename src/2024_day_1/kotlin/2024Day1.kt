import kotlin.math.abs

// AOC Year 2024 Day 1
fun main() {
    val lines = {}::class.java.getResourceAsStream("input.txt")!!.bufferedReader().readLines()

    val originalPairs = lines.map { it.split(Regex("\\s+")) }
        .map { it[0].toInt() to it[1].toInt() }

    val leftSide = originalPairs.map { it.first }.sorted()
    val rightSide = originalPairs.map { it.second }.sorted()

    val sum = leftSide.zip(rightSide)
        .sumOf { abs(it.first - it.second) }

    println("Sum is $sum")
}