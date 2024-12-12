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

        println("Regions: ${regions.size}, Plots left: ${plotsWithoutRegion.size}")
    }

    println("Regions: ${regions.size}")
    regions.forEach { println("Region '${it.plantType}': area=${it.area()}, perimeter=${it.perimeter()}, price=${it.fencePrice()}") }

    val totalFencePrice = regions.sumOf { it.fencePrice() }
    println("Total Fence Price: $totalFencePrice")
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

data class Region(val plantType: Char) {
    val plots = mutableSetOf<Pair<Int, Int>>()

    operator fun plusAssign(plot: Pair<Int, Int>) {
        plots += plot
    }

    fun growFrom(startPlot: Pair<Int, Int>, garden: List<List<Char>>) {
        val plotsToExplore = mutableSetOf<Pair<Int, Int>>()
        plotsToExplore += startPlot

        var i = 0
        while (plotsToExplore.isNotEmpty()) {
            val plot = plotsToExplore.removeFirst()
            plots += plot

            plotsToExplore += plot.neighbors()
                .filter { (x, y) -> garden.getOrNull(x, y) == plantType }
                .filterNot { plots.contains(it) }

            i++
            if (i % 1_000_000 == 0) println("\t${plotsToExplore.size}")
        }
    }

    fun area() = plots.size
    fun perimeter() = plots.flatMap { plot ->
        plot.neighbors().filterNot { plots.contains(it) }
    }.size

    fun fencePrice() = area() * perimeter()
}