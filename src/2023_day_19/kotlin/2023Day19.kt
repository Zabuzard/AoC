import java.util.function.Predicate

// AOC Year 2023 Day 19
fun main() {
    val lines = {}::class.java.getResourceAsStream("input.txt")!!.bufferedReader().readLines()

    val (rulesRaw, partsRaw) = lines.chunked { it.isEmpty() }

    val nameToRuleSet = rulesRaw.map { it.toRuleSet() }.associateBy { it.name }
    val parts = partsRaw.map { it.toPart() }

    val partsToProcess = ArrayDeque<Pair<Part, String>>()
    parts.forEach { partsToProcess += it to "in" }

    val acceptedParts = mutableSetOf<Part>()
    val rejectedParts = mutableSetOf<Part>()
    while (partsToProcess.isNotEmpty()) {
        val (part, ruleSetName) = partsToProcess.removeFirst()

        when (val destination = nameToRuleSet[ruleSetName]!!.processPart(part)) {
            "A" -> acceptedParts += part
            "R" -> rejectedParts += part
            else -> partsToProcess.addLast(part to destination)
        }
    }

    val totalRating = acceptedParts.sumOf { it.rating() }
    println("Total Rating: $totalRating")
}

data class RuleSet(val name: String, val rules: List<Rule>) {
    fun processPart(part: Part) = rules.first { it.predicate.test(part) }.destination
}

fun String.toRuleSet() = """(.+)\{(.+)}""".toRegex().matchEntire(this)!!.groupValues.let { (_, name, rulesRaw) ->
    val rules = rulesRaw.split(',').map { it.toRule() }
    RuleSet(name, rules)
}

data class Rule(val predicate: Predicate<Part>, val destination: String)

fun String.toRule() = split(':').let {
    if (it.size == 1) return Rule({ true }, it.first())
    val (predicateRaw, destination) = it
    val (_, piece, order, number) = """([xmas])([<>])(\d+)""".toRegex().matchEntire(predicateRaw)!!.groupValues
    Rule({ part ->
        val currentNumber = part.pieceNumbers[piece.first()]!!
        when (order) {
            ">" -> currentNumber > number.toInt()
            else -> currentNumber < number.toInt()
        }
    }, destination)
}

data class Part(val pieceNumbers: Map<Char, Int>) {
    fun rating() = pieceNumbers.values.sum()
}

fun String.toPart() = drop(1).dropLast(1).split(',').associate {
    val (piece, number) = it.split('=')
    piece.first() to number.toInt()
}.let { Part(it) }

fun List<String>.chunked(splitWhen: Predicate<String>) = flatMapIndexed { index, line ->
    when {
        index == 0 || index == lastIndex -> listOf(index)
        splitWhen.test(line) -> listOf(index - 1, index + 1)
        else -> emptyList()
    }
}.windowed(size = 2, step = 2) { (from, to) -> slice(from..to) }