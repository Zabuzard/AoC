// AOC Year 2024 Day 3
fun main() {
    val lines = {}::class.java.getResourceAsStream("input.txt")!!.bufferedReader().readLines()

    val pattern = "mul\\((\\d{1,3}),(\\d{1,3})\\)|do\\(\\)|don't\\(\\)".toRegex()

    var areInstructionsEnabled = true

    val result = lines.asSequence()
        .flatMap { pattern.findAll(it) }
        .map { it.groupValues }
        .filter {
            areInstructionsEnabled = when (it[0]) {
                "do()" -> true
                "don't()" -> false
                else -> return@filter areInstructionsEnabled
            }
            false
        }.map { Instruction(it[1].toInt(), it[2].toInt()) }
        .sumOf { it.multiply() }

    println("Result is: $result")
}


data class Instruction(val a: Int, val b: Int) {
    fun multiply() = a * b
}
