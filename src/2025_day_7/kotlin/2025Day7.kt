import io.github.zabuzard.maglev.external.algorithms.ShortestPathComputationBuilder
import io.github.zabuzard.maglev.external.graph.simple.SimpleEdge
import io.github.zabuzard.maglev.external.graph.simple.SimpleGraph

// AOC Year 2025 Day 7
fun main() {
    val lines = {}::class.java.getResourceAsStream("input.txt")!!.bufferedReader().readLines()
    val grid = Grid(lines.map { it.toList() })

    val graph = SimpleGraph<Pair<Int, Int>, SimpleEdge<Pair<Int, Int>>>()

    for (y in grid.rowIndices) {
        for (x in grid.colIndices) {
            graph.addNode(x to y)
        }
    }

    for (src in graph.nodes) {
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
            .forEach { graph.addEdge(it) }
    }

    val result = ShortestPathComputationBuilder(graph).build()
        .shortestPathReachable(grid.startPos)
    val reachableSplitters = result.reachableNodes
        .filter { (x, y) -> grid.isSplitter(x, y) }.count()
    println("Reachable splitters Part1: $reachableSplitters")
}

data class Grid(val gridRaw: List<List<Char>>) {
    val rowIndices = gridRaw.indices
    val colIndices = gridRaw.first().indices
    val startPos = gridRaw.first().withIndex().first { it.value == 'S' }.let { it.index to 0 }

    fun isValid(x: Int, y: Int) = x in colIndices && y in rowIndices
    fun isSplitter(x: Int, y: Int) = gridRaw[y][x] == '^'
}
