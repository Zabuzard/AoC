// AOC Year 2023 Day 8
fun main() {
    val lines = {}::class.java.getResourceAsStream("input.txt")!!.bufferedReader().readLines()

    val sequence = lines.first().toList()
    val maze = lines.drop(2).map { it.toNode() }.toList().let { Maze(it) }

    val cycleLengths = maze.nodes.filter { it.name.endsWith('A') }
        .also { println("Paths: ${it.size}") }
        .mapIndexed { i, pathStart ->
        var currentNode = pathStart

        var sequenceIndex = 0
        while (!currentNode.name.endsWith('Z')) {
            when (sequence[sequenceIndex]) {
                'L' -> currentNode.leftNeighbor
                else -> currentNode.rightNeighbor
            }.let { maze.nodeByName(it)!! }
                .let { currentNode = it }

            sequenceIndex = (sequenceIndex + 1) % sequence.size
        }
        // Found end node

        var cycleLength = 0
        do {
            when (sequence[sequenceIndex]) {
                'L' -> currentNode.leftNeighbor
                else -> currentNode.rightNeighbor
            }.let { maze.nodeByName(it)!! }
                .let { currentNode = it }

            sequenceIndex = (sequenceIndex + 1) % sequence.size
            cycleLength++
        } while (!currentNode.name.endsWith('Z'))

        cycleLength.also {
            println("Done path $i")
        }
    }.toList()

    println("Cycle lengths: $cycleLengths")
    // Now compute LCM of that, done
    // 14299763833181
}

data class Node(val name: String, val leftNeighbor: String, val rightNeighbor: String)

// AAA = (BBB, CCC)
fun String.toNode() = "(.+) = \\((.+), (.+)\\)".toRegex()
    .matchEntire(this)!!.groupValues
    .let { Node(it[1], it[2], it[3]) }

data class Maze(val nodes: Iterable<Node>) {
    private val nameToNode = nodes.associateBy { it.name }

    fun nodeByName(name: String) = nameToNode[name]
}
