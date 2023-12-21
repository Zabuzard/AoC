// AOC Year 2023 Day 21
fun main() {
    val lines = {}::class.java.getResourceAsStream("input.txt")!!.bufferedReader().readLines()

    val grid = lines.map { it.toMutableList() }
    val height = grid.size
    val width = grid.first().size

    val start = (0..<height).flatMap { y -> (0..<width).map { x -> x to y } }
        .first { grid[it.first][it.second] == 'S' }
    val steps = 64

    val nodesToProcess = ArrayDeque<Node>()
    val nodesVisited = mutableSetOf<Node>()
    nodesToProcess += Node(start.first, start.second, 0)
    while (nodesToProcess.isNotEmpty()) {
        val node = nodesToProcess.removeFirst()

        val wasVisitedAlready = !nodesVisited.add(node)
        if (wasVisitedAlready) continue

        if (node.distance >= steps) continue

        listOf(
            node.x + 1 to node.y,
            node.x - 1 to node.y,
            node.x to node.y + 1,
            node.x to node.y - 1,
        ).filter { (x, y) -> y in grid.indices && x in grid.first().indices }
            .filterNot { (x, y) -> grid[y][x] == '#' }
            .map { (x, y) -> Node(x, y, node.distance + 1) }
            .forEach { nodesToProcess += it }
    }

    val nodesAtSteps = nodesVisited.filter { it.distance == steps }

    nodesAtSteps.forEach { grid[it.y][it.x] = 'O' }
    grid.forEach { println(it.joinToString(separator = "")) }

    println("Nodes at $steps steps: ${nodesAtSteps.size}")
}

data class Node(val x: Int, val y: Int, val distance: Int)
