import io.github.zabuzard.maglev.external.algorithms.ShortestPathComputationBuilder
import io.github.zabuzard.maglev.external.graph.simple.SimpleEdge
import io.github.zabuzard.maglev.external.graph.simple.SimpleGraph

// AOC Year 2023 Day 10
fun main() {
    val lines = {}::class.java.getResourceAsStream("input.txt")!!.bufferedReader().readLines()

    val coordToNodes = mutableMapOf<Pair<Int, Int>, Point>()
    var animalPosition = Point(-1, -1)

    val graph = SimpleGraph<Point, SimpleEdge<Point>>()
    val width = lines.first().length
    val height = lines.size
    for (x in 0..<width) {
        for (y in 0..<height) {
            Point(x, y).let {
                graph.addNode(it)
                coordToNodes[x to y] = it
            }
        }
    }
    for (x in 0..<width) {
        for (y in 0..<height) {
            val pipe = lines[y][x]
            // | is a vertical pipe connecting north and south.
            // - is a horizontal pipe connecting east and west.
            // L is a 90-degree bend connecting north and east.
            // J is a 90-degree bend connecting north and west.
            // 7 is a 90-degree bend connecting south and west.
            // F is a 90-degree bend connecting south and east.
            // . is ground; there is no pipe in this tile.
            // S is the starting position of the animal;

            val current = coordToNodes[x to y]!!
            val north = x to y - 1
            val south = x to y + 1
            val west = x - 1 to y
            val east = x + 1 to y

            val neighbors = when (pipe) {
                '|' -> listOf(north, south)
                '-' -> listOf(west, east)
                'L' -> listOf(north, east)
                'J' -> listOf(west, north)
                '7' -> listOf(west, south)
                'F' -> listOf(south, east)
                'S' -> {
                    animalPosition = current
                    listOf()
                }

                else -> listOf()
            }

            neighbors.forEach {
                val neighbor = coordToNodes[it] ?: return@forEach

                graph.addEdge(SimpleEdge(current, neighbor, 1.0))
            }
        }
    }

    graph.getIncomingEdges(animalPosition).forEach {
        graph.addEdge(SimpleEdge(it.destination, it.source, 1.0))
    }

    val nodeToCost = ShortestPathComputationBuilder(graph).build()
        .shortestPathCostsReachable(animalPosition)

    val maxCost = nodeToCost.maxOf { it.value.pathCost }.toInt()
    println("Max cost: $maxCost")

    val loopNodes = nodeToCost.keys

    val cornerNodes = mutableListOf<Point>()
    for (x in 0..<width) {
        cornerNodes += Point(x, -1)
        cornerNodes += Point(x, height)
    }
    for (y in 0..<height) {
        cornerNodes += Point(-1, y)
        cornerNodes += Point(width, y)
    }

    val areaGraph = SimpleGraph<Point, SimpleEdge<Point>>()
    graph.nodes.forEach { areaGraph.addNode(it) }
    cornerNodes.forEach { areaGraph.addNode(it) }
    areaGraph.nodes.filterNot { it in loopNodes }.forEach { nonLoopNode ->
        val (x, y) = nonLoopNode
        val north = (x to y - 1) to '-'
        val south = (x to y + 1) to '-'
        val west = (x - 1 to y) to '|'
        val east = (x + 1 to y) to '|'

        listOf(north, south, west, east).forEach neighbor@{
            val neighbor = coordToNodes[it.first] ?: Point(it.first.first, it.first.second)
            if (neighbor !in areaGraph.nodes) return@neighbor
            if (neighbor in loopNodes && lines[it.first.second][it.first.first] == it.second) return@neighbor

            areaGraph.addEdge(SimpleEdge(nonLoopNode, neighbor, 1.0))
        }
    }

    areaGraph.reverse()
    val outsideNodes = ShortestPathComputationBuilder(areaGraph).build()
        .shortestPathCostsReachable(cornerNodes).keys

    val enclosedNodes = graph.nodes.filterNot { it in loopNodes }.filterNot { it in outsideNodes }

    println("Enclosed nodes: ${enclosedNodes.size}")

    val table = lines.map { it.toCharArray() }.toTypedArray()
    outsideNodes.filter { it in graph.nodes }.forEach {
        table[it.y][it.x] = 'O'
    }
    enclosedNodes.filter { it in graph.nodes }.forEach {
        table[it.y][it.x] = 'I'
    }
    table.map { it.concatToString() }.forEach { println(it) }
}

data class Point(val x: Int, val y: Int)