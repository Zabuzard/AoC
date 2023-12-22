import kotlin.math.max

// AOC Year 2023 Day 22
fun main() {
    val lines = {}::class.java.getResourceAsStream("input.txt")!!.bufferedReader().readLines()

    val blocks = lines.map { it.toBlock() }.sortedBy { it.height }

    // Move all down
    while (true) {
        var allSettled = true
        val heightToBlock = blocks.groupBy { it.height }
        for (block in blocks) {
            if (block.isSettled) continue
            if (block.height == 1) {
                block.isSettled = true
                continue
            }

            val blocksBelow = heightToBlock[block.height - 1] ?: listOf()
            val isSupported = blocksBelow.any { it.supportsBlock(block) }
            if (isSupported) {
                block.isSettled = true
                continue
            }

            block.moveDown().also { println("Moving down: $block") }
            allSettled = false
        }

        if (allSettled) break
    }

    blocks.forEach { println(it) }

    // Check blocks below
    var canBeRemoved = 0
    val heightToBlock = blocks.groupBy { it.height }
    for (block in blocks) {
        val blocksAbove = heightToBlock[block.height + 1] ?: listOf()
        val isSoleSupporter = blocksAbove.filterNot { it == block }.count { block.supportsBlock(it) } == 1

        if (!isSoleSupporter) {
            canBeRemoved++
        }
    }

    println("Safe to disintegrate: $canBeRemoved")
}

data class Point(val x: Int, val y: Int, var z: Int)

class Block(start: Point, end: Point) {
    var isSettled = false
    var height = max(start.z, end.z)
    private val points = (start.x..end.x).flatMap { x ->
        (start.y..end.y).flatMap { y ->
            (start.z..end.z).map { z -> Point(x, y, z) }
        }
    }
    private val surface = points.map { it.x to it.y }.toSet()

    fun supportsBlock(other: Block) = height + 1 == other.height && surface.intersect(other.surface).isNotEmpty()

    fun moveDown() {
        if (height == 1) return
        points.forEach { it.z-- }
        height--
    }

    override fun toString() = "settled: $isSettled, height: $height, points: $points"
}

fun String.toPoint() = split(',').map { it.toInt() }.let { Point(it[0], it[1], it[2]) }

fun String.toBlock() = split('~').map { it.toPoint() }.let { Block(it[0], it[1]) }
