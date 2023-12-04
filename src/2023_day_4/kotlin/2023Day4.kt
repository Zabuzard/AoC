import java.math.BigInteger

// AOC Year 2023 Day 4
fun main() {
    val lines = {}::class.java.getResourceAsStream("input.txt")!!.bufferedReader().readLines()

    val idToCard = lines.map { it.toCard() }.associateBy { it.id }

    val queue = ArrayDeque(idToCard.values)
    var cardsWon = 0L
    while (queue.isNotEmpty()) {
        val card = queue.removeLast()
        cardsWon++

        card.rewardIds().map { idToCard[it]!! }.forEach {
            queue.add(it)
        }

        if (cardsWon % 10_000L == 0L) {
            println("Cards processed: $cardsWon (queue: ${queue.size})")
        }
    }

    println("Cards won: $cardsWon")
}

data class Card(val id: Int, val winningNumbers: Set<Int>, val numbersOnCard: Set<Int>) {
    fun points(): BigInteger = matchCount().let {
        if (it == 0) BigInteger.ZERO else BigInteger.TWO.pow(it - 1)
    }

    private fun matchCount() = winningNumbers.count { numbersOnCard.contains(it) }

    fun rewardIds() = matchCount().let {
        if (it == 0) emptyList() else id + 1..id + it
    }
}

fun String.toNumbers() = split("\\s+".toRegex()).filter { it.isNotEmpty() }.map { it.toInt() }.toSet()

fun String.toCard() = "Card\\s+(\\d+): (.+) \\| (.+)".toRegex()
    .matchEntire(this)!!
    .groupValues.let { matches ->
        val id = matches[1].toInt()
        val winningNumbers = matches[2].toNumbers()
        val numbersOnCard = matches[3].toNumbers()
        Card(id, winningNumbers, numbersOnCard)
    }
//.also { println(it) }