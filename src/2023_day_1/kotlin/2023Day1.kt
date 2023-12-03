// AOC Year 2023 Day 1
fun main() {
    val lines = {}::class.java.getResourceAsStream("input.txt")!!.bufferedReader().readLines()

    val wordToDigit = mapOf(
        "one" to 1,
        "two" to 2,
        "three" to 3,
        "four" to 4,
        "five" to 5,
        "six" to 6,
        "seven" to 7,
        "eight" to 8,
        "nine" to 9,
    )

    val sum = lines.sumOf { line ->
        var firstLine = line
        val firstWord = firstLine.findAnyOf(wordToDigit.keys)?.second
        if (firstWord != null) {
            firstLine = firstLine.replaceFirst(firstWord, wordToDigit[firstWord].toString())
        }

        var secondLine = line.reversed()
        val lastWord = secondLine.findAnyOf(wordToDigit.keys.map { it.reversed() })?.second
        if (lastWord != null) {
            secondLine = secondLine.replaceFirst(lastWord, wordToDigit[lastWord.reversed()].toString())
        }

        val firstDigit = firstLine.first { it.isDigit() }.digitToInt()
        val lastDigit = secondLine.first { it.isDigit() }.digitToInt()
        //println("Line $firstDigit, $lastDigit")
        firstDigit * 10 + lastDigit
    }

    println("Sum is: $sum")
}