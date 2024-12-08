// AOC Year 2024 Day 8
fun main() {
    val lines = {}::class.java.getResourceAsStream("input.txt")!!.bufferedReader().readLines()

    val grid = lines.map { it.toList() }

    val allAntennas = grid.flatMapIndexed { y: Int, chars: List<Char> ->
        chars.mapIndexedNotNull { x, c ->
            if (c == '.') null else Antenna(c, x, y)
        }
    }
    val typeToAntennas = allAntennas.groupBy { it.c }
    val antennaPairs = typeToAntennas.values.flatMap { antennas ->
        antennas.flatMap { a ->
            antennas.filterNot { it == a }.map { a to it }
        }
    }

    val uniqueAntiNodes = antennaPairs.flatMap { it.findAntiNodes() }
        .filter { it.isInBounds(grid) }
        .toSet()

    println("Unique anti-nodes: ${uniqueAntiNodes.size}")
    println()

    val uniqueHarmonicAntiNodes = antennaPairs.flatMap { it.findHarmonicAntiNodes(grid) }
        .filter { it.isInBounds(grid) }
        .toSet()

    println("Unique harmonic anti-nodes: ${uniqueHarmonicAntiNodes.size}")
}

fun Pair<Antenna, Antenna>.findAntiNodes(): List<Pair<Int, Int>> {
    val deltaX = first.x - second.x
    val deltaY = first.y - second.y

    return listOf(
        first.x + deltaX to first.y + deltaY,
        second.x - deltaX to second.y - deltaY
    )
}

fun Pair<Antenna, Antenna>.findHarmonicAntiNodes(grid: List<List<Char>>): List<Pair<Int, Int>> {
    val deltaX = first.x - second.x
    val deltaY = first.y - second.y

    val posDir = generateSequence(first.x to first.y) { prev ->
        (prev.first + deltaX to prev.second + deltaY).takeIf { it.isInBounds(grid) }
    }
    val negDir = generateSequence(first.x to first.y) { prev ->
        (prev.first - deltaX to prev.second - deltaY).takeIf { it.isInBounds(grid) }
    }

    return sequenceOf(posDir, negDir).flatten().toList()
}

fun Pair<Int, Int>.isInBounds(grid: List<List<Char>>): Boolean {
    val height = grid.size
    val width = grid.first().size

    return first in 0..<width && second in 0..<height
}

fun List<List<Char>>.printWith(antiNodes: Set<Pair<Int, Int>>) {
    forEachIndexed { y, chars ->
        chars.mapIndexed { x, c ->
            if (x to y in antiNodes) '#' else c
        }.joinToString(separator = "").let { println(it) }
    }
}

data class Antenna(val c: Char, val x: Int, val y: Int)