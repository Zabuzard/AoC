// AOC Year 2024 Day 3
fun main() {
    val lines = {}::class.java.getResourceAsStream("input.txt")!!.bufferedReader().readLines()

    val pattern = "mul\\((\\d{1,3}),(\\d{1,3})\\)".toRegex()

    val result = lines.asSequence()
        .flatMap { pattern.findAll(it) }
        .map { it.groupValues }
        .map { it[1].toInt() to it[2].toInt() }
        .map { (a, b) -> Instruction(a, b) }
        .sumOf { it.multiply() }

    println("Result is: $result")
}

data class Instruction(val a: Int, val b: Int) {
    fun multiply() = a * b
}
