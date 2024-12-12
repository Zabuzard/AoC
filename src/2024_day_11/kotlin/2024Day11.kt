// AOC Year 2024 Day 11
fun main() {
    val lines = {}::class.java.getResourceAsStream("input.txt")!!.bufferedReader().readLines()

    var stones = lines.first()
        .split(" ")
        .map { it.toLong() }
        .map { Stone(it) }
        .toList()

    val blinks = 25
    repeat(blinks) { i -> stones = stones.blink().also { println("Blinks ($i/$blinks)") } }

    println("Stones after $blinks blinks: ${stones.size}")
}

fun List<Stone>.blink() = flatMap { it.blink() }

data class Stone(val value: Long) {
    fun blink() =
        if (value == 0L)
            listOf(Stone(1))
        else if (value.toString().length % 2 == 0)
            split()
        else
            listOf(Stone(value * 2024))

    fun split(): List<Stone> {
        val valueText = value.toString()
        val middle = valueText.length / 2

        return listOf(
            valueText.substring(0..<middle),
            valueText.substring(middle)
        ).map { it.dropWhile { c -> c == '0' } }
            .map { it.ifEmpty { "0" } }
            .map { it.toLong() }
            .map { Stone(it) }
    }
}