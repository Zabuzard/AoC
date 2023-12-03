// AOC Year 2023 Day 2
fun main() {
    val lines = {}::class.java.getResourceAsStream("input.txt")!!.bufferedReader().readLines()

    val games = lines.map { line ->
        val results = "Game (\\d+): (.+)".toRegex().find(line)!!.groupValues
        val id = results[1].toInt()

        val sessions = results[2].split(';').map { session ->
            var red = 0
            var blue = 0
            var green = 0
            session.split(',').forEach {
                val cubes = "(\\d+) \\D+".toRegex().find(it)!!.groupValues[1].toInt()
                if (it.contains("blue")) {
                    blue = cubes
                } else if (it.contains("green")) {
                    green = cubes
                } else {
                    red = cubes
                }
            }

            Session(red, blue, green)
        }

        sessions.toGame(id)
    }

    val powerSum = games.sumOf { it.power() }

    println("Power sum is $powerSum")
}

data class Session(val red: Int, val blue: Int, val green: Int)
data class Game(val id: Int, val red: Int, val blue: Int, val green: Int) {
    fun power() = red * blue * green
}

fun Iterable<Session>.toGame(id: Int) = Game(
    id,
    red = maxOf { it.red },
    blue = maxOf { it.blue },
    green = maxOf { it.green },
)