// AOC Year 2025 Day 11
fun main() {
    val lines = {}::class.java.getResourceAsStream("input.txt")!!.bufferedReader().readLines()
    
    val nodeToOutgoingEdges = lines.map {
        val (src, destsStr) = it.split(": ")
        val dests = destsStr.split(" ").toSet()
        src to dests
    }.toMap()

    val pathsPart1 = part1(nodeToOutgoingEdges)
    println("Part1 result: $pathsPart1")

    val pathsPart2 = part2(nodeToOutgoingEdges)
    println("Part2 result: $pathsPart2")
}

fun part1(nodeToOutgoingEdges: Map<String, Set<String>>): Int {
    var pathsToDest = 0
    val toProcess = ArrayDeque<String>()
    toProcess += "you"

    while (toProcess.isNotEmpty()) {
        val current = toProcess.removeLast()
        if (current == "out") {
            pathsToDest++
            continue
        }

        val neighbors = nodeToOutgoingEdges[current] ?: emptySet()
        toProcess += neighbors
    }

    return pathsToDest
}

fun part2(nodeToOutgoingEdges: Map<String, Set<String>>): Int {
    var pathsToDest = 0
    val toProcess = ArrayDeque<ExplorationNode>()
    toProcess += ExplorationNode("svr")

    while (toProcess.isNotEmpty()) {
        val current = toProcess.removeLast()
        if (current.node == "out") {
            if ("dac" in current.visitedNodes && "fft" in current.visitedNodes) {
                pathsToDest++
            }
            continue
        }

        val neighbors = nodeToOutgoingEdges[current.node] ?: emptySet()
        toProcess += neighbors
            // .filter { it !in current.visitedNodes } // loop
            .map { current + it }
    }

    return pathsToDest
}

data class ExplorationNode(val node: String, val visitedNodes: Set<String>) {
    constructor(node: String) : this(node, setOf(node))

    operator fun plus(next: String)
        = ExplorationNode(next, visitedNodes + next)
}