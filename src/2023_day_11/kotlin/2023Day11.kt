import io.github.zabuzard.maglev.external.algorithms.ShortestPathComputationBuilder
import io.github.zabuzard.maglev.external.graph.simple.SimpleEdge
import io.github.zabuzard.maglev.external.graph.simple.SimpleGraph
import kotlin.math.abs
import kotlin.time.TimeMark
import kotlin.time.TimeSource

// AOC Year 2023 Day 11
fun main() {
    val lines = {}::class.java.getResourceAsStream("input.txt")!!.bufferedReader().readLines()

    val universe = lines.map { it.toMutableList() }.toMutableList()
    val expandEmptyBy = 1_000_000

    // Expand empty rows
    val emptyRows = mutableSetOf<Int>()
    for (i in universe.indices.reversed()) {
        val row = universe[i]
        val isEmpty = row.all { it == '.' }
        if (isEmpty) {
            // Insert after
            //universe.add(i, row.toMutableList())
            emptyRows += i
        }
    }

    // Expand empty columns
    val emptyColumns = mutableSetOf<Int>()
    for (i in universe.first().indices.reversed()) {
        val isEmpty = universe.all { it[i] == '.' }
        if (isEmpty) {
            //universe.forEach { it.add(i, '.') }
            emptyColumns += i
        }
    }

    // universe.map { it.joinToString(separator = "") }.forEach { println(it) }

    val grid = mutableMapOf<Pair<Int, Int>, Point>()
    for (y in universe.indices) {
        for (x in universe[y].indices) {
            grid[x to y] = Point(x, y)
        }
    }

    val galaxyPoints = mutableListOf<Point>()
    for (y in universe.indices) {
        for (x in universe[y].indices) {
            if (universe[y][x] == '#') galaxyPoints += grid[x to y]!!
        }
    }
    val galaxies = galaxyPoints.mapIndexed { id, point -> Galaxy(id, point) }

    println("Found ${galaxies.size} galaxies")

    val graph = SimpleGraph<Point, SimpleEdge<Point>>()
    grid.values.forEach { graph.addNode(it) }

    for (y in universe.indices) {
        for (x in universe[y].indices) {
            val up = x to y - 1
            val down = x to y + 1
            val left = x - 1 to y
            val right = x + 1 to y

            val source = grid[x to y]!!
            val cost = if (x in emptyColumns || y in emptyRows) expandEmptyBy else 1
            listOf(
                up,
                down,
                left,
                right
            ).filter { it.first in universe.first().indices && it.second in universe.indices }
                .forEach {
                    val destination = grid[it]!!
                    graph.addEdge(SimpleEdge(source, destination, cost.toDouble()))
                }
        }
    }

    val computation = ShortestPathComputationBuilder(graph)
        .setAmountOfLandmarks(10)
        .setLandmarkProvider { size -> galaxies.map { it.point }.take(size) }
        .build()

    val pairs = (0..<galaxies.size - 1)
        .flatMap { i -> (i + 1..<galaxies.size).map { galaxies[i] to galaxies[it] } }
    println("Found ${pairs.size} pairs")

    val timeSource = TimeSource.Monotonic
    val startTime = timeSource.markNow()
    val sum = pairs.shuffled().mapIndexed { i, (first, second) ->
        if (i % 500 == 0) {
            val percentage = ((i / pairs.size.toDouble()) * 100).toInt()

            val currentTime = timeSource.markNow()
            val duration = currentTime - startTime
            val remainingTime = (duration / i.toDouble()) * (pairs.size - i).toDouble()

            println("Progress $i/${pairs.size} ($percentage %, ETA: $remainingTime)")
        }
        computation.shortestPathCost(first.point, second.point).orElseThrow().toLong()
        //first.point distTo second.point
    }.sum()

    println("Sum: $sum")
}

data class Point(val x: Int, val y: Int)
data class Galaxy(val id: Int, val point: Point)

infix fun Point.distTo(other: Point) = abs(x - other.x) + abs(y - other.y)
