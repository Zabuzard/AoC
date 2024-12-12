import kotlin.math.abs

// AOC Year 2024 Day 12
fun main() {
    val lines = {}::class.java.getResourceAsStream("input.txt")!!.bufferedReader().readLines()

    val garden = lines.map { it.toList() }

    val plotsWithoutRegion = garden.flatMapIndexed { y, row -> row.indices.map { x -> x to y } }
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

fun <E> MutableSet<E>.removeFirst() = first().also { remove(it) }
operator fun <E> List<List<E>>.get(x: Int, y: Int) = this[y][x]
operator fun <E> List<List<E>>.get(coord: Pair<Int, Int>) = this[coord.first, coord.second]
fun <E> List<List<E>>.getOrNull(x: Int, y: Int) = getOrNull(y)?.getOrNull(x)
fun Pair<Int, Int>.neighbors() = listOf(
    first + 1 to second,
    first - 1 to second,
    first to second + 1,
    first to second - 1,
)

fun Pair<Int, Int>.isAdjacentTo(other: Pair<Int, Int>) =
    abs(first - other.first) + abs(second - other.second) == 1

data class Region(val plantType: Char) {
    val plots = mutableSetOf<Pair<Int, Int>>()

    operator fun plusAssign(plot: Pair<Int, Int>) {
        plots += plot
    }

    fun growFrom(startPlot: Pair<Int, Int>, garden: List<List<Char>>) {
        val plotsToExplore = mutableSetOf<Pair<Int, Int>>()
        plotsToExplore += startPlot

        while (plotsToExplore.isNotEmpty()) {
            val plot = plotsToExplore.removeFirst()
            plots += plot

            plotsToExplore += plot.neighbors()
                .filter { (x, y) -> garden.getOrNull(x, y) == plantType }
                .filterNot { plots.contains(it) }
        }
    }

    private fun perimeterPlots() = plots.flatMap { plot ->
        plot.neighbors().filterNot { plots.contains(it) }
    }

    fun area() = plots.size
    fun perimeter() = perimeterPlots().size
    fun connectedPerimeter(): Int {
        val unconnectedPlots = perimeterPlots().toMutableSet()
        val sides = mutableListOf<MutableSet<Pair<Int, Int>>>()
        while (unconnectedPlots.isNotEmpty()) {
            val plot = unconnectedPlots.removeFirst()

            val connectedSide = sides.find { side -> side.any { plot.isAdjacentTo(it) } }
            if (connectedSide != null) {
                connectedSide += plot
            } else {
                sides += mutableSetOf(plot)
            }
        }
        return sides.size
    }

    fun fencePrice() = area() * perimeter()
    fun bulkFencePrice() = area() * connectedPerimeter()
}