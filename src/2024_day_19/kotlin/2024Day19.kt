// AOC Year 2024 Day 19
fun main() {
    val lines = {}::class.java.getResourceAsStream("input.txt")!!.bufferedReader().readLines()

    availablePatterns = lines.first().split(", ")
    possibleDesigns += availablePatterns

    val desiredDesigns = lines.drop(2)

    val possibleDesignCount = desiredDesigns.count { isDesignPossible(it) }
    println("Possible designs: $possibleDesignCount")
}

var availablePatterns = listOf<String>()
val possibleDesigns = mutableSetOf<String>()

fun isDesignPossible(design: String): Boolean {
    if (design in possibleDesigns) return true

    val subDesigns = availablePatterns.filter { design.startsWith(it) }
        .map { design.substring(it.length) }
    val isPossible = subDesigns.any { isDesignPossible(it) }
    if (isPossible) possibleDesigns += design
    return isPossible
}