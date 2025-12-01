import kotlin.math.abs

// AOC Year 2025 Day 1
fun main() {
    val lines = {}::class.java.getResourceAsStream("input.txt")!!.bufferedReader().readLines()

    var passwordCountPart1 = 0
    val safe = Safe()
    lines.map { it.toTurns() }.forEach {
        safe.turnRight(it)
        if (safe.pos == 0) {
            passwordCountPart1++
        }
    }

    println("Part 1 Password is: $passwordCountPart1")
    println("Part 2 Password is: ${safe.movedThroughZero}")
}

fun String.toTurns() =
    this.substring(1).toInt()
        .let { if (this[0] == 'L') -it else it }

class Safe() {
    var movedThroughZero = 0
        private set
    var pos = 50
        private set

    fun turnRight(turns: Int) {
        //val posBefore = pos

        val fullRotations = abs(turns) / 100
        movedThroughZero += fullRotations

        val wasAtZero = pos == 0
        val remainingTurns = turns % 100
        pos += remainingTurns

        if (pos !in 1..<100) {
            movedThroughZero++
        }
        if (wasAtZero && pos < 0) {
            movedThroughZero--
        }

        pos %= 100
        if (pos < 0) {
            pos += 100
        }

        //println("Turns $turns, Pos $posBefore -> $pos, moved zero $movedThroughZero")
    }
}
