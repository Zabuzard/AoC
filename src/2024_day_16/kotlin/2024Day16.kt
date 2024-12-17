import io.github.zabuzard.maglev.external.algorithms.ShortestPathComputationBuilder
import io.github.zabuzard.maglev.external.graph.simple.SimpleEdge
import io.github.zabuzard.maglev.external.graph.simple.SimpleGraph

// AOC Year 2024 Day 16
fun main() {
    val lines = {}::class.java.getResourceAsStream("input.txt")!!.bufferedReader().readLines()

    val grid = lines.map { it.toList() }

    val graph = SimpleGraph<Node, SimpleEdge<Node>>()
    var start: Node? = null
    var ends: List<Node>? = null

    // Nodes
    for (y in grid.indices) {
        for (x in grid[y].indices) {
            val c = grid[x, y]
            if (c == '#') continue

            if (c == 'S') {
                start = Node(x, y, Direction.EAST)
                graph.addNode(start)
                Direction.entries.filter { it != Direction.EAST }
                    .map { Node(x, y, it) }
                    .forEach { graph.addNode(it) }
            } else {
                val nodes = Direction.entries.map { Node(x, y, it) }
                if (c == 'E') {
                    ends = nodes
                }
                nodes.forEach { graph.addNode(it) }
            }
        }
    }

    // Edges
    graph.nodes.forEach { src ->
        // Same direction moves forward
        src.neighborInDirection()
            .takeIf { it.isValid(grid) }
            ?.let { dest -> SimpleEdge(src, dest, 1.0) }
            ?.also { graph.addEdge(it) }

        // Other directions rotate without moving
        Direction.entries.filterNot { it == src.direction }
            .map { Node(src.x, src.y, it) }
            .map { dest -> SimpleEdge(src, dest, src.direction.rotationDistanceTo(dest.direction) * 1_000.0) }
            .forEach { graph.addEdge(it) }
    }

    // 1-* by implicit reverse and then *-1
    graph.reverse()
    val algo = ShortestPathComputationBuilder(graph).build()
    val cost = algo.shortestPathCost(ends!!, start!!).orElseThrow().toInt()

    println("Smallest score is: $cost")
}

enum class Direction {
    NORTH,
    EAST,
    SOUTH,
    WEST;

    fun rotationDistanceTo(other: Direction) = when (this) {
        NORTH -> when (other) {
            NORTH -> 0
            EAST -> 1
            SOUTH -> 2
            WEST -> 1
        }

        EAST -> when (other) {
            NORTH -> 1
            EAST -> 0
            SOUTH -> 1
            WEST -> 2
        }

        SOUTH -> when (other) {
            NORTH -> 2
            EAST -> 1
            SOUTH -> 0
            WEST -> 1
        }

        WEST -> when (other) {
            NORTH -> 1
            EAST -> 2
            SOUTH -> 1
            WEST -> 0
        }
    }
}

data class Node(val x: Int, val y: Int, val direction: Direction) {
    fun neighborInDirection() = when (direction) {
        Direction.NORTH -> x to y - 1
        Direction.SOUTH -> x to y + 1
        Direction.WEST -> x - 1 to y
        Direction.EAST -> x + 1 to y
    }.let { (x, y) -> Node(x, y, direction) }

    fun isValid(grid: List<List<Char>>) = grid.getOrNull(x, y).let { it != null && it != '#' }
}

operator fun <E> List<List<E>>.get(x: Int, y: Int) = this[y][x]
fun <E> List<List<E>>.getOrNull(x: Int, y: Int) = this.getOrNull(y)?.getOrNull(x)