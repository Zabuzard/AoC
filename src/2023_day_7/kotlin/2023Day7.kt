// AOC Year 2023 Day 7
fun main() {
    val lines = {}::class.java.getResourceAsStream("input.txt")!!.bufferedReader().readLines()

    val cards = lines.map { it.split(" ") }.map { Hand(it[0].toList(), it[1].toInt()) }
    val totalWinnings = cards.sorted().mapIndexed { i, card -> (i + 1) * card.bid }.sum()

    println("Total winnings: $totalWinnings")
}

sealed class HandType(private val strength: Int) : Comparable<HandType> {
    override fun compareTo(other: HandType) = strength.compareTo(other.strength)
    abstract fun isOfType(cardCountsDescending: List<Pair<Char, Int>>, jokerCount: Int): Boolean
}

data object HighCard : HandType(1) {
    override fun isOfType(cardCountsDescending: List<Pair<Char, Int>>, jokerCount: Int) =
        cardCountsDescending.all { it.second == 1 }
}

data object OnePair : HandType(2) {
    override fun isOfType(cardCountsDescending: List<Pair<Char, Int>>, jokerCount: Int) =
        (cardCountsDescending.first().second == 2 && cardCountsDescending[1].second == 1)
                || (cardCountsDescending.first().second == 1 && jokerCount >= 1)
}

data object TwoPair : HandType(3) {
    override fun isOfType(cardCountsDescending: List<Pair<Char, Int>>, jokerCount: Int) =
        (cardCountsDescending.first().second == 2 && cardCountsDescending[1].second == 2)
                || (cardCountsDescending.first().second == 1 && jokerCount >= 2)
}

data object ThreeOfKind : HandType(4) {
    override fun isOfType(cardCountsDescending: List<Pair<Char, Int>>, jokerCount: Int) =
        (cardCountsDescending.first().second == 3 && cardCountsDescending[1].second == 1)
                || (cardCountsDescending.first().second + jokerCount >= 3)
}

data object FullHouse : HandType(5) {
    override fun isOfType(cardCountsDescending: List<Pair<Char, Int>>, jokerCount: Int) =
        (cardCountsDescending.first().second == 3 && cardCountsDescending[1].second == 2)
                || (cardCountsDescending.first().second + cardCountsDescending[1].second + jokerCount >= 5)
}

data object FourOfKind : HandType(6) {
    override fun isOfType(cardCountsDescending: List<Pair<Char, Int>>, jokerCount: Int) =
        cardCountsDescending.first().second == 4
                || cardCountsDescending.first().second + jokerCount == 4
}

data object FiveOfKind : HandType(7) {
    override fun isOfType(cardCountsDescending: List<Pair<Char, Int>>, jokerCount: Int) =
        cardCountsDescending.size <= 1
                || cardCountsDescending.first().second + jokerCount == 5
}

val HAND_TYPES_DESCENDING = listOf(
    HighCard,
    OnePair,
    TwoPair,
    ThreeOfKind,
    FullHouse,
    FourOfKind,
    FiveOfKind
).sortedDescending()

data class Hand(val cards: List<Char>, val bid: Int) : Comparable<Hand> {
    private val cardCountsDescending: List<Pair<Char, Int>> = cards.filterNot { it == 'J' }
        .groupingBy { it }.eachCount()
        .toList().sortedByDescending { it.second }
    private val jokerCount = cards.count { it == 'J' }
    private val type: HandType =
        HAND_TYPES_DESCENDING.first { it.isOfType(cardCountsDescending, jokerCount) }

    override fun compareTo(other: Hand): Int {
        val first = type.compareTo(other.type)
        if (first != 0) return first

        for (i in cards.indices) {
            val second = cards[i].cardValue().compareTo(other.cards[i].cardValue())
            if (second != 0) return second
        }

        return 0
    }
}

fun Char.cardValue() = when (this) {
    'A' -> 14
    'K' -> 13
    'Q' -> 12
    'J' -> 1
    'T' -> 10
    else -> digitToInt()
}
