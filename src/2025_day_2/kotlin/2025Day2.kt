// AOC Year 2025 Day 2
fun main() {
    val lines = {}::class.java.getResourceAsStream("input.txt")!!.bufferedReader().readLines()

    val ranges = lines.first().split(",").map { it.toRange() }
    val sumPart1 = ranges.flatMap { it.findInvalidIdsPart1() }.sum()
    val sumPart2 = ranges.flatMap { it.findInvalidIdsPart2() }.sum()

    println("Sum of invalid Ids Part1: $sumPart1")
    println("Sum of invalid Ids Part2: $sumPart2")
}

fun String.toRange() = split("-")
    .let { IdRange(it[0].toLong(), it[1].toLong()) }

fun IdRange.findInvalidIdsPart1(): List<Long> {
    val invalidIds = mutableListOf<Long>()

    for (id in start..end) {
        if (id.isInvalidIdPart1()) {
            invalidIds += id
        }
    }

    return invalidIds
}

fun IdRange.findInvalidIdsPart2(): List<Long> {
    val invalidIds = mutableListOf<Long>()

    for (id in start..end) {
        if (id.isInvalidIdPart2()) {
            invalidIds += id
        }
    }

    return invalidIds
}

fun Long.isInvalidIdPart1() = toString().let {
    val i = it.length / 2
    it.take(i) to it.substring(i)
}.let { it.first == it.second && it.first[0] != '0' }

fun Long.isInvalidIdPart2(): Boolean {
    toString().let { id ->
        for (sequenceLength in id.length / 2 downTo 1) {
            if (id.length % sequenceLength != 0) continue

            val sequence = id.take(sequenceLength)
            val isInvalid = id.windowedSequence(sequenceLength, sequenceLength).all { it == sequence }
            if (isInvalid) return true
        }
        return false
    }
}

data class IdRange(val start: Long, val end: Long)
