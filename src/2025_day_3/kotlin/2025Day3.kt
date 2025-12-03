// AOC Year 2025 Day 3
fun main() {
    val lines = {}::class.java.getResourceAsStream("input.txt")!!.bufferedReader().readLines()

    val banks = lines.map { it.toBank() }
    val totalJoltagePart1 = banks.sumOf { it.findMaxJoltagePart1() }
    val totalJoltagePart2 = banks.sumOf { it.findMaxJoltagePart2(12) }

    println("Total Joltage Part1: $totalJoltagePart1")
    println("Total Joltage Part2: $totalJoltagePart2")
}

fun String.toBank() = map { it.digitToInt() }

fun List<Int>.findMaxJoltagePart1(): Int {
    val first = dropLast(1).withIndex().maxBy { it.value }
    val second = drop(first.index + 1).max()

    return (first.value.toString() + second.toString()).toInt()
}

fun List<Int>.findMaxJoltagePart2(joltageSize: Int): Long {
    val digits = mutableListOf<IndexedValue<Int>>()
    repeat(joltageSize) {
        val windowStart = digits.lastOrNull()?.index?.plus(1) ?: 0
        val windowEnd = size - joltageSize + digits.size + 1

        val digit = subList(windowStart, windowEnd).withIndex().maxBy { it.value }
        digits += IndexedValue(windowStart + digit.index, digit.value)
    }

    return digits.map { it.value }.joinToString(separator = "") { it.toString() }.toLong()
}
