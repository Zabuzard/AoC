// AOC Year 2024 Day 7
fun main() {
    val lines = {}::class.java.getResourceAsStream("input.txt")!!.bufferedReader().readLines()

    val equations = lines.map { it.toEquation() }

    val results = equations.filter { it.isSolvable() }.sumOf { it.result }
    println("Solvable results: $results")
}

fun String.toEquation() = split(": ")
    .let { (result, valuesText) ->
        Equation(
            result.toLong(),
            valuesText.split(" ").map { it.toInt() })
    }

data class Equation(val result: Long, val values: List<Int>) {
    fun isSolvable() =
        if (values.size == 1) values.first().toLong() == result
        else isSolvable(values.first().toLong(), listOf('+'))
                || isSolvable(values.first().toLong(), listOf('*'))
                || isSolvable(values.first().toLong(), listOf('|'))

    private fun isSolvable(tempResult: Long, operators: List<Char>): Boolean {
        val operandIndex = operators.size
        val operand = values[operandIndex]
        val operator = operators.last()

        val nextTempResult = when (operator) {
            '+' -> tempResult + operand
            '*' -> tempResult * operand
            '|' -> (tempResult.toString() + operand.toString()).toLong()

            else -> throw AssertionError()
        }
        val reachedEnd = operators.size >= values.size - 1
        return if (reachedEnd) {
            nextTempResult == result
        } else if (nextTempResult > result) {
            false
        } else {
            isSolvable(nextTempResult, operators + '+')
                    || isSolvable(nextTempResult, operators + '*')
                    || isSolvable(nextTempResult, operators + '|')
        }
    }
}
