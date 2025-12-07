import io.github.zabuzard.maglev.external.algorithms.ShortestPathComputationBuilder
import io.github.zabuzard.maglev.external.graph.simple.SimpleEdge
import io.github.zabuzard.maglev.external.graph.simple.SimpleGraph

// AOC Year 2025 Day 7
fun main() {
    val lines = {}::class.java.getResourceAsStream("input.txt")!!.bufferedReader().readLines()
    val grid = Grid(lines.map { it.toList() })
    val graph = setupGraph(grid)

    part1(grid, graph)
    part2(grid)
}

fun part1(grid: Grid, graph: SimpleGraph<Pair<Int, Int>, SimpleEdge<Pair<Int, Int>>>) {
    val result = ShortestPathComputationBuilder(graph).build()
        .shortestPathReachable(grid.startPos)
    val reachableSplitters = result.reachableNodes
        .filter { (x, y) -> grid.isSplitter(x, y) }.count()
    println("Reachable splitters Part1: $reachableSplitters")
}

fun part2Bruteforce(grid: Grid, graph: SimpleGraph<Pair<Int, Int>, SimpleEdge<Pair<Int, Int>>>) {
    var timelines = 0

    val toProcess = ArrayDeque<Pair<Int, Int>>()
    toProcess += grid.startPos

    var i = 0
    while (toProcess.isNotEmpty()) {
        val node = toProcess.removeLast()

        val neighbors =
            graph.getOutgoingEdges(node)
                .map { it.destination }
                .toList().toList()
        if (neighbors.isEmpty()) {
            timelines++
        }
        neighbors.forEach { toProcess += it }

        i++
        if (i % 10_000_000 == 0) {
            println("\t${toProcess.size}")
        }
    }

    println("Timelines Part2: $timelines")
}

fun part2(grid: Grid) {
    val height = grid.gridRaw.size
    val width = grid.gridRaw.first().size
    val timelineGrid = Array(height) { LongArray(width) }

    timelineGrid[grid.startPos] = 1
    for (y in 1..<height) {
        for (x in 0..<width) {
            val timelines = timelineGrid[x, y - 1]
            if (timelines == 0L) {
                continue
            }

            if (grid.isSplitter(x, y)) {
                listOf(x - 1 to y, x + 1 to y)
                    .filter { grid.isValid(it.first, it.second) }
                    .forEach {
                        timelineGrid[it] += timelines
                    }
            } else {
                timelineGrid[x, y] += timelines
            }
        }
    }

    val total = timelineGrid.last().sum()
    println("Total timelines Part2: $total")
}

fun setupGraph(grid: Grid) = SimpleGraph<Pair<Int, Int>, SimpleEdge<Pair<Int, Int>>>().apply {
    for (y in grid.rowIndices) {
        for (x in grid.colIndices) {
            addNode(x to y)
        }
    }

    for (src in nodes) {
        val (x, y) = src

        val edgeDestinations = if (grid.isSplitter(x, y)) {
            listOf(
                x - 1 to y,
                x + 1 to y,
            )
        } else {
            listOf(x to y + 1)
        }

        edgeDestinations.filter { (x, y) -> grid.isValid(x, y) }
            .map { dest -> SimpleEdge(src, dest, 1.0) }
            .forEach { addEdge(it) }
    }
}

data class Grid(val gridRaw: List<List<Char>>) {
    val rowIndices = gridRaw.indices
    val colIndices = gridRaw.first().indices
    val startPos = gridRaw.first().withIndex().first { it.value == 'S' }.let { it.index to 0 }

    fun isValid(x: Int, y: Int) = x in colIndices && y in rowIndices
    fun isSplitter(x: Int, y: Int) = gridRaw[y][x] == '^'
}

operator fun Array<LongArray>.get(x: Int, y: Int) = this[y][x]
operator fun Array<LongArray>.get(pos: Pair<Int, Int>) = this[pos.second][pos.first]
operator fun Array<LongArray>.set(x: Int, y: Int, value: Long) {
    this[y][x] = value
}

operator fun Array<LongArray>.set(pos: Pair<Int, Int>, value: Long) {
    this[pos.second][pos.first] = value
}
