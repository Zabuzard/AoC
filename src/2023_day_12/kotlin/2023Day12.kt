// AOC Year 2023 Day 12
fun main() {
    val lines = {}::class.java.getResourceAsStream("input.txt")!!.bufferedReader().readLines()

    val sum = lines.mapIndexed { i, line ->
        line.split(' ').let { (springs, brokenGroups) ->
            springs.toList() to brokenGroups.split(',').map { it.toInt() }.toList()
        }.let {
            it.first.repeat('?') to it.second.repeat()
        }.let { countSolutions(it.first, it.second) }.also {
            println("Line ${i + 1}: $it")
        }
    }.sum()

    println("Sum is: $sum")
}

val memorizedSolutions = mutableMapOf<Pair<List<Char>, List<Int>>, Long>()

fun countSolutions(springs: List<Char>, brokenGroups: List<Int>): Long {
    // println(" ".repeat(10 - springs.size) + springs.joinToString(separator = "") + " $brokenGroups")

    val problemKey = springs to brokenGroups
    val solution = memorizedSolutions[problemKey]
    if (solution != null) {
        return solution
    }

    if (brokenGroups.isEmpty()) {
        return if (springs.all { it.couldBeWorking() }) 1 else 0
    }
    if (springs.isEmpty()) {
        return 0
    }

    val brokenGroup = brokenGroups.first()

    var sum = 0L
    var firstBroken = springs.indexOfFirst { it == '#' }
    if (firstBroken == -1) firstBroken = springs.size - brokenGroup
    for (start in 0..firstBroken.coerceAtMost(springs.size - brokenGroup)) {
        val end = start + brokenGroup
        val window = springs.subList(start, end)

        val isPossible = window.all { it.couldBeBroken() }
                && springs.getOrElse(end) { '.' }.couldBeWorking()
        if (!isPossible) {
            continue
        }

        sum += countSolutions(
            springs.subListOrEmpty(end + 1),
            brokenGroups.subList(1, brokenGroups.size)
        )
    }

    memorizedSolutions[problemKey] = sum
    return sum
}

fun Char.couldBeBroken() = this != '.'
fun Char.couldBeWorking() = this != '#'
fun <E> List<E>.subListOrEmpty(fromIndex: Int) = if (fromIndex in indices)
    subList(fromIndex, size) else emptyList()

fun <E> List<E>.repeat(delimiter: E? = null): List<E> {
    val result = toMutableList()
    repeat(4) {
        if (delimiter != null) result += delimiter
        result += this
    }

    return result
}