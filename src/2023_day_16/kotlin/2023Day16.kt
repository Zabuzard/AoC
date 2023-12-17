// AOC Year 2023 Day 16
enum class Direction {
    RIGHT,
    LEFT,
    UP,
    DOWN
}

fun main() {
    val lines = {}::class.java.getResourceAsStream("input.txt")!!.bufferedReader().readLines()

    val grid = lines.map { it.toList() }

    val startLights = (grid[0].indices).map { Light(it, -1, 0, 1) } +
            (grid[0].indices).map { Light(it, grid.size, 0, -1) } +
            (grid.indices).map { Light(-1, it, 1, 0) } +
            (grid.indices).map { Light(grid[0].size, it, -1, 0) }

    val count = startLights.maxOf { startLight ->
        val energized = grid.map { it.map { mutableSetOf<Direction>() }.toMutableList() }.toMutableList()

        val lights = ArrayDeque<Light>()
        lights.addLast(startLight)

        while (lights.isNotEmpty()) {
            val light = lights.removeFirst()

            if (light.y in grid.indices && light.x in grid[light.y].indices) {
                val direction = light.direction()
                val wasAdded = energized[light.y][light.x].add(direction)
                if (!wasAdded) {
                    continue
                }
            }

            light.x += light.speedX
            light.y += light.speedY

            if (light.y !in grid.indices || light.x !in grid[light.y].indices) {
                continue
            }

            val tile = grid[light.y][light.x]

            if (tile == '|' && light.speedX != 0) {
                lights.addLast(Light(light.x, light.y, 0, -1))
                lights.addLast(Light(light.x, light.y, 0, 1))
                continue
            } else if (tile == '-' && light.speedY != 0) {
                lights.addLast(Light(light.x, light.y, -1, 0))
                lights.addLast(Light(light.x, light.y, 1, 0))
                continue
            }

            if (tile == '/' && light.speedX > 0) {
                light.speedX = 0
                light.speedY = -1
            } else if (tile == '/' && light.speedX < 0) {
                light.speedX = 0
                light.speedY = 1
            } else if (tile == '/' && light.speedY > 0) {
                light.speedX = -1
                light.speedY = 0
            } else if (tile == '/' && light.speedY < 0) {
                light.speedX = 1
                light.speedY = 0
            } else if (tile == '\\' && light.speedX > 0) {
                light.speedX = 0
                light.speedY = 1
            } else if (tile == '\\' && light.speedX < 0) {
                light.speedX = 0
                light.speedY = -1
            } else if (tile == '\\' && light.speedY > 0) {
                light.speedX = 1
                light.speedY = 0
            } else if (tile == '\\' && light.speedY < 0) {
                light.speedX = -1
                light.speedY = 0
            }

            lights.addLast(light)
        }

        /*
        energized.map { row -> row.joinToString(separator = "") { if (it.isNotEmpty()) "#" else "." } }
            .forEach { println(it) }
         */

        energized.flatten().count { it.isNotEmpty() }
    }

    println("Energized tiles: $count")
}

data class Light(var x: Int, var y: Int, var speedX: Int, var speedY: Int) {
    fun direction() = if (speedX > 0) {
        Direction.RIGHT
    } else if (speedX < 0) {
        Direction.LEFT
    } else if (speedY > 0) {
        Direction.DOWN
    } else {
        Direction.UP
    }
}