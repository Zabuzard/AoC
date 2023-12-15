import java.util.BitSet
import java.util.function.Predicate

// AOC Year 2023 Day 13
fun main() {
    val lines = {}::class.java.getResourceAsStream("input.txt")!!.bufferedReader().readLines()

    val patterns = lines.chunked { it.isEmpty() }.map { Pattern(it) }

    val sum = patterns.sumOf { it.computeValue() }
    println("Sum is: $sum")
}

class Pattern(lines: List<String>) {
    private val width: Int = lines.first().length
    private val height: Int = lines.size

    private val rows: List<BitSet> = lines.map {
        val row = BitSet(width)
        it.forEachIndexed { i, c -> row[i] = c == '#' }
        row
    }
    private val columns: List<BitSet> = (0..<width).map { x ->
        val column = BitSet(height)
        (0..<height).forEach { y -> column[y] = lines[y][x] == '#' }
        column
    }

    fun computeValue(): Int {
        val colMirror = columns.findMirrorIndex()
        if (colMirror != null) {
            return colMirror
        }
        val rowMirror = rows.findMirrorIndex()!!
        return rowMirror * 100
    }
}

// Mirror line is right after the index, for index 5 the mirror would be between 5 and 6
fun List<BitSet>.findMirrorIndex(): Int? {
    return indices.zipWithNext { i, j -> i to j }
        //.filter { (i, j) -> this[i] == this[j] }
        .firstOrNull { (i, j) -> isSmudgedMirror(i, j) }
        ?.first?.plus(1)
}

fun List<BitSet>.isMirror(i: Int, j: Int): Boolean {
    val mirrorLength = i.coerceAtMost(lastIndex - j)
    return (1..mirrorLength).map { i - it to j + it }
        .all { (x, y) -> this[x] == this[y] }
}

fun List<BitSet>.isSmudgedMirror(i: Int, j: Int): Boolean {
    val mirrorLength = i.coerceAtMost(lastIndex - j)

    var totalDifferences = 0
    for (offset in (0..mirrorLength)) {
        val x = i - offset
        val y = j + offset
        val temp = this[x].clone() as BitSet
        temp.xor(this[y])
        val differences = temp.cardinality()
        totalDifferences += differences

        if (totalDifferences > 1) return false
    }
    return totalDifferences == 1
}

fun List<String>.chunked(splitWhen: Predicate<String>) = flatMapIndexed { index, line ->
    when {
        index == 0 || index == lastIndex -> listOf(index)
        splitWhen.test(line) -> listOf(index - 1, index + 1)
        else -> emptyList()
    }
}.windowed(size = 2, step = 2) { (from, to) -> slice(from..to) }