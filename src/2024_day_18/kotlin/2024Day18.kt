import io.github.zabuzard.maglev.external.algorithms.ShortestPathComputationBuilder
import io.github.zabuzard.maglev.external.graph.simple.SimpleEdge
import io.github.zabuzard.maglev.external.graph.simple.SimpleGraph

// AOC Year 2024 Day 18
fun main() {
    val lines = {}::class.java.getResourceAsStream("input.txt")!!.bufferedReader().readLines()

    val width = 71 // 7
    val height = 71 // 7
    val bytesToFall = 1024 // 12
    val allBytesToFall = lines.map { line -> line.split(",").map { it.toInt() } }
        .map { (x, y) -> Node(x, y) }
    val fallenBytes = allBytesToFall.take(bytesToFall).toMutableSet()

    val start = Node(0, 0)
    val end = Node(width - 1, height - 1)
    val graph = SimpleGraph<Node, SimpleEdge<Node>>()

    for (x in 0..<width) {
        for (y in 0..<height) {
            val src = Node(x, y)
            if (src in fallenBytes) continue

            val destinations = src.neighbors()
                .filterNot { it in fallenBytes }
                .filter { it.x in 0..<width && it.y in 0..<height }

            graph.addNode(src)
            destinations.forEach { dest ->
                graph.addNode(dest)
                graph.addEdge(SimpleEdge(src, dest, 1.0))
            }
        }
    }

    val algo = ShortestPathComputationBuilder(graph).build()
    val path = algo.shortestPath(start, end).orElseThrow()
    val pathNodes = path.map { it.edge.destination!! }.toSet().plus(start)

    // Print graph
    for (y in 0..<height) {
        for (x in 0..<width) {
            val node = Node(x, y)
            val c = when (node) {
                in fallenBytes -> '#'
                in pathNodes -> 'O'
                else -> '.'
            }
            print(c)
        }
        println()
    }

    println("Steps to reach goal: ${path.totalCost.toInt()}")

    // Part 2
    val extraBytesToFall = allBytesToFall.drop(bytesToFall).toMutableSet()
    for (fallenByte in extraBytesToFall) {
        // Block position
        graph.getOutgoingEdges(fallenByte).toList().forEach { graph.removeEdge(it) }
        graph.getIncomingEdges(fallenByte).toList().forEach { graph.removeEdge(it) }
        graph.removeNode(fallenByte)

        // Re-attempt path
        val algoPart2 = ShortestPathComputationBuilder(graph).build()
        val pathPart2 = algoPart2.shortestPath(start, end)
        if (pathPart2.isEmpty) {
            println("Byte that blocked the path: ${fallenByte.x},${fallenByte.y}")
            break
        }
    }
}

data class Node(val x: Int, val y: Int) {
    fun neighbors() = listOf(
        x + 1 to y,
        x - 1 to y,
        x to y + 1,
        x to y - 1,
    ).map { (x, y) -> Node(x, y) }
}