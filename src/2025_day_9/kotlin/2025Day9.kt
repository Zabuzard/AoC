// AOC Year 2025 Day 9
fun main() {
    val lines = {}::class.java.getResourceAsStream("input.txt")!!.bufferedReader().readLines()

    val floor = lines.map { it.toPos() }.toSet().let{ Floor(it) }

    val areaPart1 = part1(floor)
    println("Part1 area: $areaPart1")
}

fun part1(floor: Floor): Long {
    val largestRectangle = floor.redTiles.flatMap { upperLeftCorner ->
        floor.redTiles.map{lowerRightCorner ->
            upperLeftCorner to lowerRightCorner
        }
    }.filter { it.first != it.second }
    .flatMap {
        listOf(
            it,
            Pos(it.first.x, it.second.y) to Pos(it.second.x, it.first.y)
        )
    }.maxBy { it.area() }
    return largestRectangle.also{ println(it)}.area()
}

fun Pair<Pos, Pos>.area(): Long {
    return kotlin.math.abs(
        (second.x - first.x + 1).toLong() *
        (second.y - first.y + 1).toLong()
    )
}

fun String.toPos() = split(",").let { Pos(it[0].toInt(), it[1].toInt()) }
data class Pos(val x: Int, val y: Int) {
    infix fun distTo(other: Pos): Int {
        val xDiff = kotlin.math.abs(x - other.x)
        val yDiff = kotlin.math.abs(y - other.y)
        return kotlin.math.sqrt((xDiff * xDiff + yDiff * yDiff).toDouble()).toInt()
    }
}

class Floor(val redTiles: Set<Pos>) {
    val width = redTiles.maxOf { it.x } + 1
    val height = redTiles.maxOf { it.y } + 1
    
    override fun toString(): String {
        return (0 until height).joinToString("\n") { y ->
            (0 until width).joinToString("") { x ->
                if (redTiles.contains(Pos(x, y))) "#" else "."
            }
        }
    }
}