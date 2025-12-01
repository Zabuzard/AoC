// AOC Year 2025 Day 1
fun main() {
    val lines = {}::class.java.getResourceAsStream("input.txt")!!.bufferedReader().readLines()

    var passwordCount = 0
    val safe = Safe()
    lines.map { it.toTurns() }.forEach {
        safe.turnRight(it)
        if (safe.pos == 0) {
            passwordCount++
        }
    }

    println("Password is: $passwordCount")
}

fun String.toTurns() =
    this.substring(1).toInt()
        .let { if (this[0] == 'L') -it else it }

class Safe() {
    var pos = 50
        private set

    fun turnRight(turns: Int) {
        pos += turns
        pos %= 100
        if (pos < 0) {
            pos += 100
        }
    }
}
