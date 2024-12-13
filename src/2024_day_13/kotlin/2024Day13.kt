import java.util.function.Predicate

// AOC Year 2024 Day 13
fun main() {
    val lines = {}::class.java.getResourceAsStream("input.txt")!!.bufferedReader().readLines()

    val games = lines.chunked { it.isEmpty() }
        .map { (buttonA, buttonB, prize) -> Game(buttonA.toButtonMoves(), buttonB.toButtonMoves(), prize.toPrize()) }

    var i = 0
    val tokensNeeded = games.mapNotNull {
        it.solve()
            .also { i++; println("Games solved $i/${games.size}") }
    }.sum()
    println("Tokens needed to win most games: $tokensNeeded")
}

fun List<String>.chunked(splitWhen: Predicate<String>) = flatMapIndexed { index, line ->
    when {
        index == 0 || index == lastIndex -> listOf(index)
        splitWhen.test(line) -> listOf(index - 1, index + 1)
        else -> emptyList()
    }
}.windowed(size = 2, step = 2) { (from, to) -> slice(from..to) }

fun String.toButtonMoves() = "X([+-]\\d+), Y([+-]\\d+)".toRegex()
    .find(this)
    .let { it!!.groupValues }
    .let { ButtonMoves(it[1].toInt(), it[2].toInt()) }

fun String.toPrize() = "X=(\\d+), Y=(\\d+)".toRegex()
    .find(this)
    .let { it!!.groupValues }
    .let { Prize(it[1].toInt(), it[2].toInt()) }

data class ButtonMoves(val x: Int, val y: Int)
data class Prize(val x: Int, val y: Int)
data class Solution(val buttonAPresses: Int, val buttonBPresses: Int) {
    fun tokens() = 3 * buttonAPresses + buttonBPresses
}

data class Game(val buttonA: ButtonMoves, val buttonB: ButtonMoves, val prize: Prize) {
    // 94a + 22b = 8400
    // 34a + 67b = 5400

    fun solve(): Int? {
        val solutions = bruteforce()
        return solutions.minOfOrNull { it.tokens() }
    }

    private fun bruteforce() =
        (0..100).asSequence().flatMap { a -> (0..100).asSequence().map { b -> a to b } }
            .filter { (a, b) ->
                a * buttonA.x + b * buttonB.x == prize.x
                        && a * buttonA.y + b * buttonB.y == prize.y
            }.map { Solution(it.first, it.second) }
            .toList()
}
