// AOC Year 2024 Day 1
fun main() {
    val lines = {}::class.java.getResourceAsStream("input.txt")!!.bufferedReader().readLines()

    val originalPairs = lines.map { it.split(Regex("\\s+")) }
        .map { it[0].toInt() to it[1].toInt() }

    val leftSide = originalPairs.map { it.first }
    val rightSide = originalPairs.map { it.second }.groupingBy { it }.eachCount()

    val score = leftSide.sumOf { it * (rightSide[it] ?: 0) }

    println("Score is $score")
}