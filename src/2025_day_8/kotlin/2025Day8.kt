import java.util.*
import kotlin.math.sqrt

// AOC Year 2025 Day 8
fun main() {
    val lines = {}::class.java.getResourceAsStream("input.txt")!!.bufferedReader().readLines()
    val boxes = lines.map { it.toPos() }

    part1(boxes)
    part2(boxes)
}

fun part1(boxes: List<Pos>) {
    val boxToCircuit =
        boxes.map { Circuit(setOf(it)) }.associateBy { it.boxes.first() }.toMutableMap()

    connectNClosestBoxes(1_000, boxes, boxToCircuit)
    val circuits = boxToCircuit.values.distinct()

    val resultPart1 = circuits.map { it.boxes.size }
        .sorted().reversed()
        .take(3)
        .reduce { acc, i -> acc * i }

    println("Part1 result: $resultPart1")
}

fun part2(boxes: List<Pos>) {
    val boxToCircuit =
        boxes.map { Circuit(setOf(it)) }.associateBy { it.boxes.first() }.toMutableMap()

    val lastConnectedPair = connectNClosestBoxes(null, boxes, boxToCircuit)

    val wallDistance = lastConnectedPair.first.x * lastConnectedPair.second.x
    println("Part2 wall distance: $wallDistance")
}

fun connectNClosestBoxes(
    n: Int?,
    boxes: List<Pos>,
    boxToCircuit: MutableMap<Pos, Circuit>
): Pair<Pos, Pos> {
    val pairsByDist = computePairsByDist(boxes)

    var lastActuallyConnectedPair: Pair<Pos, Pos>? = null
    if (n != null) {
        repeat(n) {
            val boxesToConnect = pairsByDist.poll()
            if (boxToCircuit[boxesToConnect.first] != boxToCircuit[boxesToConnect.second]!!) {
                lastActuallyConnectedPair = boxesToConnect
            }
            connectBoxes(boxesToConnect.first, boxesToConnect.second, boxToCircuit)
        }
    } else {
        while (pairsByDist.isNotEmpty()) {
            val boxesToConnect = pairsByDist.poll()
            if (boxToCircuit[boxesToConnect.first] != boxToCircuit[boxesToConnect.second]!!) {
                lastActuallyConnectedPair = boxesToConnect
            }
            connectBoxes(boxesToConnect.first, boxesToConnect.second, boxToCircuit)
        }
    }

    return lastActuallyConnectedPair!!
}

fun computePairsByDist(
    boxes: List<Pos>,
): PriorityQueue<Pair<Pos, Pos>> {
    val pairsByDist =
        PriorityQueue<Pair<Pos, Pos>>(Comparator.comparingDouble { it.first distTo it.second })
    for (i in boxes.indices) {
        for (j in i + 1..<boxes.size) {
            val pair = boxes[i] to boxes[j]
            pairsByDist += pair
        }
    }

    return pairsByDist
}

fun connectBoxes(first: Pos, second: Pos, boxToCircuit: MutableMap<Pos, Circuit>) {
    val firstCircuit = boxToCircuit[first]!!
    val secondCircuit = boxToCircuit[second]!!

    if (firstCircuit == secondCircuit) {
        return
    }

    val mergedCircuit = Circuit(firstCircuit.boxes + secondCircuit.boxes)
    mergedCircuit.boxes.forEach { boxToCircuit[it] = mergedCircuit }
}

fun String.toPos() = split(",").let { Pos(it[0].toLong(), it[1].toLong(), it[2].toLong()) }

data class Pos(val x: Long, val y: Long, val z: Long) {
    infix fun distTo(other: Pos): Double {
        val xDiff = other.x - x
        val yDiff = other.y - y
        val zDiff = other.z - z
        val sum = xDiff * xDiff + yDiff * yDiff + zDiff * zDiff
        return sqrt(sum.toDouble())
    }
}

class Circuit(val boxes: Set<Pos>) {
    override fun toString(): String {
        return boxes.toString()
    }
}
