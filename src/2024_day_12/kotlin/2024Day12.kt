import kotlin.math.abs

// AOC Year 2024 Day 12
fun main() {
    val lines = {}::class.java.getResourceAsStream("input.txt")!!.bufferedReader().readLines()

    val garden = lines.map { it.toList() }

    val plotsWithoutRegion = garden.flatMapIndexed { y, row -> row.indices.map { x -> x to y }.map { it.toPlot() } }
        .toMutableSet()

    val regions = mutableListOf<Region>()
    while (plotsWithoutRegion.isNotEmpty()) {
        val startPlot = plotsWithoutRegion.removeFirst()
        val region = Region(garden[startPlot])
        region.growFrom(startPlot, garden)

        regions += region
        plotsWithoutRegion -= region.plots
    }

    val totalFencePrice = regions.sumOf { it.fencePrice() }
    println("Total Fence Price: $totalFencePrice")

    val totalBulkFencePrice = regions.sumOf { it.bulkFencePrice() }
    println("Total Bulk Fence Price: $totalBulkFencePrice")
}

fun Pair<Int, Int>.toPlot() = Plot(first, second)
data class Plot(val x: Int, val y: Int)
data class Fence(val inside: Plot, val outside: Plot) {
    fun isInSameLineWith(other: Fence) =
        inside.isAdjacentTo(other.inside) && outside.isAdjacentTo(other.outside)
}

fun <E> MutableSet<E>.removeFirst() = first().also { remove(it) }
operator fun <E> List<List<E>>.get(x: Int, y: Int) = this[y][x]
operator fun <E> List<List<E>>.get(plot: Plot) = this[plot.x, plot.y]
fun <E> List<List<E>>.getOrNull(x: Int, y: Int) = getOrNull(y)?.getOrNull(x)
fun Plot.neighbors() = listOf(
    x + 1 to y,
    x - 1 to y,
    x to y + 1,
    x to y - 1,
).map { it.toPlot() }

fun Plot.isAdjacentTo(other: Plot) =
    abs(x - other.x) + abs(y - other.y) == 1

data class Region(val plantType: Char) {
    val plots = mutableSetOf<Plot>()

    operator fun plusAssign(plot: Plot) {
        plots += plot
    }

    fun growFrom(startPlot: Plot, garden: List<List<Char>>) {
        val plotsToExplore = mutableSetOf<Plot>()
        plotsToExplore += startPlot

        while (plotsToExplore.isNotEmpty()) {
            val plot = plotsToExplore.removeFirst()
            plots += plot

            plotsToExplore += plot.neighbors()
                .filter { (x, y) -> garden.getOrNull(x, y) == plantType }
                .filterNot { plots.contains(it) }
        }
    }

    private fun fences() = plots.flatMap { inside ->
        inside.neighbors().filterNot { plots.contains(it) }.map { Fence(inside, it) }
    }

    fun area() = plots.size
    fun perimeter() = fences().size
    fun connectedPerimeter(): Int {
        val unconnectedFences = fences().toMutableSet()
        val sides = mutableListOf<MutableSet<Fence>>()
        while (unconnectedFences.isNotEmpty()) {
            val fence = unconnectedFences.removeFirst()

            val connectedSide = sides.find { side -> side.any { fence.isInSameLineWith(it) } }
            if (connectedSide != null) {
                connectedSide += fence
            } else {
                sides += mutableSetOf(fence)
            }
        }
        return sides.size
    }

    fun fencePrice() = area() * perimeter()
    fun bulkFencePrice() = area() * connectedPerimeter()
}