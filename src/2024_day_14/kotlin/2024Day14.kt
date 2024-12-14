// AOC Year 2024 Day 14
fun main() {
    val lines = {}::class.java.getResourceAsStream("input.txt")!!.bufferedReader().readLines()

    val robots = lines.map { it.toRobot() }
    val room = Room(101, 103, robots)
    //val room = Room(11, 7, robots)

    println("Before:")
    room.print()

    repeat(100) { room.moveRobots() }

    println()
    println("After:")
    room.print()

    val safetyFactor = room.safetyFactor()
    println("Safety factor: $safetyFactor")
}

fun String.toRobot() = "p=(\\d+),(\\d+) v=(-?\\d+),(-?\\d+)".toRegex()
    .matchEntire(this)!!
    .groupValues.drop(1).map { it.toInt() }
    .let { (x, y, vX, vY) -> Robot(x, y, vX, vY) }

data class Robot(var x: Int, var y: Int, val vX: Int, val vY: Int) {
    fun move(roomWidth: Int, roomHeight: Int) {
        x = (x + vX).mod(roomWidth)
        y = (y + vY).mod(roomHeight)
    }
}

data class Room(val width: Int, val height: Int, val robots: List<Robot>) {
    private val centerColumn = width / 2
    private val centerRow = height / 2

    enum class Quadrant {
        TOP_LEFT,
        TOP_RIGHT,
        BOTTOM_LEFT,
        BOTTOM_RIGHT,
        MIDDLE_LINE
    }

    private fun Robot.quadrant() =
        if (x < centerColumn && y < centerRow) Quadrant.TOP_LEFT
        else if (x > centerColumn && y < centerRow) Quadrant.TOP_RIGHT
        else if (x < centerColumn && y > centerRow) Quadrant.BOTTOM_LEFT
        else if (x > centerColumn && y > centerRow) Quadrant.BOTTOM_RIGHT
        else Quadrant.MIDDLE_LINE

    fun moveRobots() = robots.forEach { it.move(width, height) }

    fun print() {
        val robotGrid = Array(width) { IntArray(height) }
        robots.forEach { robotGrid[it.x][it.y]++ }

        for (y in 0..<height) {
            for (x in 0..<width) {
                val robotsAtPos = robotGrid[x][y]
                print(robotsAtPos.takeIf { it != 0 } ?: '.')
            }
            println()
        }
    }

    fun safetyFactor() = robots.groupBy { it.quadrant() }
        .filterKeys { it != Quadrant.MIDDLE_LINE }
        .mapValues { it.value.size }
        .also { println(it) }
        .values.reduce(Int::times)
}