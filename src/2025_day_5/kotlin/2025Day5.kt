import java.util.function.Predicate
import kotlin.math.max
import kotlin.math.min

// AOC Year 2025 Day 5
fun main() {
    val lines = {}::class.java.getResourceAsStream("input.txt")!!.bufferedReader().readLines()

    val (freshRangesRaw, ingredients) = lines.chunked { it.isEmpty() }
    val freshRanges = freshRangesRaw.map { it.toFreshRange() }

    val freshCountPart1 =
        ingredients.map { it.toLong() }.count { ingredient -> freshRanges.any { ingredient in it } }
    println("Fresh ingredients Part1: $freshCountPart1")

    val totalFreshIdsPart2 = freshRanges.merged().sumOf { it.size }
    println("Sum of all possible fresh ingredients Part2: $totalFreshIdsPart2")
}

fun List<String>.chunked(splitWhen: Predicate<String>) = flatMapIndexed { index, line ->
    when {
        index == 0 || index == lastIndex -> listOf(index)
        splitWhen.test(line) -> listOf(index - 1, index + 1)
        else -> emptyList()
    }
}.windowed(size = 2, step = 2) { (from, to) -> slice(from..to) }

data class FreshRange(val start: Long, val endInclusive: Long) {
    val size = endInclusive - start + 1

    operator fun contains(value: Long) = value in start..endInclusive

    fun overlapsWith(other: FreshRange) =
        start in other.start..other.endInclusive
                || endInclusive in other.start..other.endInclusive
                || other.start in start..endInclusive
                || other.endInclusive in start..endInclusive

    fun asMergedWith(other: FreshRange) =
        FreshRange(min(start, other.start), max(endInclusive, other.endInclusive))

    override fun toString() = "$start-$endInclusive"
}

fun String.toFreshRange() = split('-').let { FreshRange(it[0].toLong(), it[1].toLong()) }

fun List<FreshRange>.merged(): List<FreshRange> {
    var result = this
    while (true) {
        val resultAttempt = mutableListOf<FreshRange>()

        val alreadyMerged = mutableSetOf<FreshRange>()
        for (i in result.indices) {
            val freshRange = result[i]

            if (freshRange in alreadyMerged) {
                continue
            }
            alreadyMerged += freshRange

            var merged = freshRange
            for (j in i + 1..<result.size) {
                val other = result[j]
                if (other in alreadyMerged) {
                    continue
                }

                if (merged.overlapsWith(other)) {
                    merged = merged.asMergedWith(other)
                    alreadyMerged += other
                }
            }
            resultAttempt += merged
        }

        if (result.size == resultAttempt.size) {
            result = resultAttempt
            break
        }
        result = resultAttempt
    }

    return result
}
