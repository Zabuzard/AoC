import io.github.zabuzard.maglev.external.algorithms.ShortestPathComputationBuilder
import io.github.zabuzard.maglev.external.graph.simple.SimpleEdge
import io.github.zabuzard.maglev.external.graph.simple.SimpleGraph

// AOC Year 2025 Day 10
fun main() {
    val lines = {}::class.java.getResourceAsStream("input.txt")!!.bufferedReader().readLines()
    
    val machines = lines.map { it.toMachine() }

    val resultPart1 = machines.sumOf { part1(it) }
    println("Part1 result: $resultPart1")
}

fun part1(machine: Machine): Int {
    val lightsAmount = machine.lights.state.size
    val src = Lights(BooleanArray(lightsAmount) { false })
    val dest = machine.lights

    val graph = SimpleGraph<Lights, SimpleEdge<Lights>>()
    val path = ShortestPathComputationBuilder(graph).build()
        .shortestPath(src, dest).orElseThrow()
    return path.length()
}

fun Machine.toGraph(): SimpleGraph<Lights, SimpleEdge<Lights>> {
    val graph = SimpleGraph<Lights, SimpleEdge<Lights>>()
    createAllPermutationsOfLights(lights.state.size)
        .forEach { graph.addNode(it) }

    buttons.forEach { button -> graph.nodes.forEach { src ->
        val destState = src.state.copyOf()
        button.lights.forEach { lightIndex ->
            destState[lightIndex] = !src.state[lightIndex]
        }
        val dest = Lights(destState)
        val edge = SimpleEdge(src, dest, 1.0)
        graph.addEdge(edge)
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
data class Lights(val state: BooleanArray)

fun String.toButtons() = this.split(" ").map {
    it.removeSurrounding("(", ")")
    .split(",").map { it.toInt() }.let { nums -> Button(nums) }
}
data class Button(val lights: List<Int>)

class Machine(val lights: Lights, val buttons: List<Button>) {
    override fun toString() = "Machine(lights=$lights, buttons=$buttons)"
}