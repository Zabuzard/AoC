// AOC Year 2024 Day 19
fun main() {
    val lines = {}::class.java.getResourceAsStream("input.txt")!!.bufferedReader().readLines()

    availablePatterns = lines.first().split(", ")
    availablePatterns.forEach { possibleDesigns.getOrCreate(it) += Combinations(listOf(it)) }

    val desiredDesigns = lines.drop(2)
    desiredDesigns.forEachIndexed { i, design ->
        findPossibleDesigns(design)
            .also { println("\t${i + 1}/${desiredDesigns.size}") }
    }

    val possibleDesignCount = desiredDesigns.count { isDesignPossible(it) }
    println("Possible designs: $possibleDesignCount")
    println()

    // Part 2
    desiredDesigns.filter { isDesignPossible(it) }.forEach {
        println("$it: ${possibleDesigns[it]}")
    }
    val combinationSum = desiredDesigns.filter { isDesignPossible(it) }
        .sumOf { possibleDesigns[it]!!.size }
    println("Combination sum: $combinationSum")
}

var availablePatterns = listOf<String>()
val possibleDesigns = mutableMapOf<String, MutableSet<Combinations>>()

fun <K, V> MutableMap<K, MutableSet<V>>.getOrCreate(key: K) = getOrPut(key) { mutableSetOf() }

fun isDesignPossible(design: String) = possibleDesigns[design]?.isNotEmpty() == true

fun findPossibleDesigns(design: String) {
    // if (isDesignPossible(design)) return

    val patternAndSubDesign = availablePatterns.filter { design.startsWith(it) }
        .map { it to design.substring(it.length) }
    patternAndSubDesign.forEach { findPossibleDesigns(it.second) }
    val possiblePatternAndSubDesigns = patternAndSubDesign.filter { isDesignPossible(it.second) }
    possiblePatternAndSubDesigns.forEach { (pattern, subDesign) ->
        val subDesignCombinations = possibleDesigns[subDesign]!!
        possibleDesigns.getOrCreate(design) += subDesignCombinations.map { Combinations(listOf(pattern) + it.list) }
    }
}

class Combinations(val list: List<String>) {
    val text = list.joinToString()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Combinations
        return text == other.text
    }

    override fun hashCode() = text.hashCode()
    override fun toString(): String {
        return "[$text]"
    }
}