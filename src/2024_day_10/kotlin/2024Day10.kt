import io.github.zabuzard.maglev.external.algorithms.ShortestPathComputation
import io.github.zabuzard.maglev.external.algorithms.ShortestPathComputationBuilder
import io.github.zabuzard.maglev.external.graph.simple.SimpleEdge
import io.github.zabuzard.maglev.external.graph.simple.SimpleGraph

// AOC Year 2024 Day 10
fun main() {
    val lines = {}::class.java.getResourceAsStream("input.txt")!!.bufferedReader().readLines()

    val map = lines.mapIndexed { y, row ->
        row.toList().mapIndexed { x, height ->
            TopoEntry(x, y, height.digitToInt())
        }
    }

    val graph = SimpleGraph<TopoEntry, SimpleEdge<TopoEntry>>()
    map.flatten().forEach { graph.addNode(it) }

    map.flatten().flatMap { start ->
        listOf(
            start.x - 1 to start.y,
            start.x + 1 to start.y,
            start.x to start.y - 1,
            start.x to start.y + 1,
        ).mapNotNull { (x, y) -> map.getOrNull(x, y) }
            .filter { end -> start.canWalkTo(end) }
            .map { end -> SimpleEdge(start, end, 1.0) }
    }.forEach { graph.addEdge(it) }

    val trailHeads = map.flatten().filter { it.isTrailHead() }

    val computation = ShortestPathComputationBuilder(graph).build()

    val scores = trailHeads.sumOf { it.hikingScore(computation) }
    println("Scores: $scores")

    val ratings = trailHeads.sumOf { it.hikingRating(graph) }
    println("Ratings: $ratings")
}

data class TopoEntry(val x: Int, val y: Int, val height: Int) {
    fun canWalkTo(other: TopoEntry) = other.height - height == 1
    fun isTrailHead() = height == 0
    fun isMountainTop() = height == 9
    fun hikingScore(computation: ShortestPathComputation<TopoEntry, SimpleEdge<TopoEntry>>) =
        computation.shortestPathReachable(this)
            .reachableNodes
            .filter { it.isMountainTop() }
            .count()

    fun hikingRating(graph: SimpleGraph<TopoEntry, SimpleEdge<TopoEntry>>): Int {
        var pathsToMountainTops = 0
        val nodesToExplore = ArrayDeque<TopoEntry>()
        nodesToExplore += this

        while (nodesToExplore.isNotEmpty()) {
            val node = nodesToExplore.removeFirst()
            if (node.isMountainTop()) pathsToMountainTops++
            graph.getOutgoingEdges(node).forEach { nodesToExplore += it.destination }
        }

        return pathsToMountainTops
    }
}

operator fun List<List<TopoEntry>>.get(x: Int, y: Int) = this[y][x]
fun List<List<TopoEntry>>.getOrNull(x: Int, y: Int) = getOrNull(y)?.getOrNull(x)