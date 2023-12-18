import java.util.PriorityQueue

// AOC Year 2023 Day 17
fun main() {
    val lines = {}::class.java.getResourceAsStream("input.txt")!!.bufferedReader().readLines()

    val grid = lines.map { row -> row.map { it.digitToInt() } }

    val itemsToProcess = PriorityQueue<Item>()
    itemsToProcess += Item(0, 0, Direction.RIGHT, 0, 0, null)
    itemsToProcess += Item(0, 0, Direction.DOWN, 0, 0, null)
    itemsToProcess += Item(0, 0, Direction.UP, 0, 0, null)
    itemsToProcess += Item(0, 0, Direction.LEFT, 0, 0, null)

    val destinationY = grid.lastIndex
    val destinationX = grid[destinationY].lastIndex

    val tentativeHeatLosses = mutableMapOf<Index, Pair<Int, Index?>>()

    while (itemsToProcess.isNotEmpty()) {
        val item = itemsToProcess.poll()!!
        val index = item.toIndex()

        val tentativeHeatLoss = tentativeHeatLosses[index]?.first ?: Int.MAX_VALUE
        if (item.heatLoss >= tentativeHeatLoss) continue

        tentativeHeatLosses[index] = item.heatLoss to item.parent

        Direction.entries.mapNotNull { item.moveInto(it, grid) }.forEach { itemsToProcess += it }
    }

    val minHeatLoss = Direction.entries.map { Index(destinationX, destinationY, it) }
        .mapNotNull { tentativeHeatLosses[it] }
        .minBy { it.first }

    val pathNodes = mutableSetOf(destinationX to destinationY)
    var currentNode = minHeatLoss
    while (true) {
        val parent = currentNode.second ?: break

        pathNodes += parent.x to parent.y
        currentNode = tentativeHeatLosses[parent]!!
    }

    grid.forEachIndexed { y, row ->
        row.mapIndexed { x, c -> if (x to y in pathNodes) '#' else '.' }
            .joinToString(separator = "")
            .also { println(it) }
    }

    println("Min heat loss: ${minHeatLoss.first}")
}

enum class Direction {
    LEFT,
    RIGHT,
    UP,
    DOWN;

    fun isOpposite(other: Direction) = this == LEFT && other == RIGHT ||
            this == RIGHT && other == LEFT ||
            this == UP && other == DOWN ||
            this == DOWN && other == UP
}

data class Index(val x: Int, val y: Int, val direction: Direction)

data class Item(
    val x: Int,
    val y: Int,
    val direction: Direction,
    val travelledIntoDirection: Int,
    val heatLoss: Int,
    val parent: Index?
) :
    Comparable<Item> {
    fun moveInto(nextDirection: Direction, grid: List<List<Int>>): Item? {
        if (direction.isOpposite(nextDirection)) return null

        val (nextX, nextY) = when (nextDirection) {
            Direction.RIGHT -> x + 1 to y
            Direction.LEFT -> x - 1 to y
            Direction.UP -> x to y - 1
            Direction.DOWN -> x to y + 1
        }

        if (nextY !in grid.indices || nextX !in grid[nextY].indices) return null
        val nextTravelled = if (direction == nextDirection) travelledIntoDirection + 1 else 1
        if (nextTravelled >= 3) return null

        return Item(nextX, nextY, nextDirection, nextTravelled, heatLoss + grid[nextY][nextX], toIndex())
    }

    override fun compareTo(other: Item) = heatLoss.compareTo(other.heatLoss)

    fun toIndex() = Index(x, y, direction)
}