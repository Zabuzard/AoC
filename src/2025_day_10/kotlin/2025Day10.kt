// AOC Year 2025 Day 10
fun main() {
    val lines = {}::class.java.getResourceAsStream("input.txt")!!.bufferedReader().readLines()
    
    val machines = lines.map { it.toMachine() }

    val resultPart1 = machines.sumOf { part1(it) }
    println("Part1 result: $resultPart1")
}

fun part1(machine: Machine): Long {
    val lightsAmount = machine.lights.state.size
    val src = Lights(BooleanArray(lightsAmount) { false })
    val dest = machine.lights

    val graph = machine.toGraph() // .also { println(it) }
    return shortestPathLength(graph, src, dest)
}

fun shortestPathLength(graph: Graph, src: Lights, dest: Lights): Long {
    val toProcess = ArrayDeque<Pair<Lights, Long>>()
    toProcess.add(src to 0)
    val visited = mutableSetOf<Lights>()
    while (toProcess.isNotEmpty()) {
        val (node, depth) = toProcess.removeFirst()
        visited += node // .also { println("Visited: $node") }
        if (node == dest) return depth

        val neighbors = graph.nodeToOutgoingEdges[node] ?: emptySet()
        // println("  Neighbors: $neighbors")
        neighbors
            .filter { it !in visited }
            .forEach { toProcess.add(it to depth + 1) }
    }
    return -1
}

fun Machine.toGraph(): Graph {
    val graph = Graph()
    val nodes = createAllPermutationsOfLights(lights.state.size)

    buttons.forEach { button -> nodes.forEach { src ->
        val destState = src.state.copyOf()
        button.lights.forEach { lightIndex ->
            destState[lightIndex] = !src.state[lightIndex]
        }
        val dest = Lights(destState)
        graph.addEdge(src, dest)
    }}
    return graph
}

fun createAllPermutationsOfLights(size: Int): List<Lights> {
    val results = mutableListOf<Lights>()
    val total = 1 shl size

    for (i in 0 until total) {
        val state = BooleanArray(size) { bitIndex ->
            (i and (1 shl bitIndex)) != 0
        }
        results.add(Lights(state))
    }

    return results
}

fun String.toMachine() = Regex("\\[(.+)\\] (.+) \\{(.+)\\}")
    .matchEntire(this)!!.destructured
    .let { (lights, buttons, joltans) ->
        Machine(lights.toLights(), buttons.toButtons())
    }

fun String.toLights() = this.map { it == '#' }.toBooleanArray()
    .let { Lights(it) }
data class Lights(val state: BooleanArray) {
    override fun toString() = buildString {
        append("[")
        state.forEach { append(if (it) "#" else ".") }
        append("]")
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Lights) return false
        return state.contentEquals(other.state)
    }

    override fun hashCode(): Int {
        return state.contentHashCode()
    }
}

fun String.toButtons() = this.split(" ").map {
    it.removeSurrounding("(", ")")
    .split(",").map { it.toInt() }.let { nums -> Button(nums) }
}
data class Button(val lights: List<Int>)

class Machine(val lights: Lights, val buttons: List<Button>) {
    override fun toString() = "Machine(lights=$lights, buttons=$buttons)"
}

data class Graph(val nodeToOutgoingEdges: MutableMap<Lights, MutableSet<Lights>> = mutableMapOf()) {
    fun addEdge(src: Lights, dest: Lights) {
        nodeToOutgoingEdges.getOrPut(src) { mutableSetOf() } += dest
    }

    override fun toString() = buildString {
        append("Graph:\n")
        append("nodes:\n")
        nodeToOutgoingEdges.keys.forEach { append("  $it\n") }
        append("edges:\n")
        nodeToOutgoingEdges.forEach { (src, dests) ->
            append(" from $src: $dests\n")
        }
    }
}
