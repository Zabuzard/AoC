// AOC Year 2024 Day 3
fun main() {
    val lines = {}::class.java.getResourceAsStream("input.txt")!!.bufferedReader().readLines()

    val pattern = "mul\\((\\d{1,3}),(\\d{1,3})\\)|do\\(\\)|don't\\(\\)".toRegex()

    var areInstructionsEnabled = true

    val result = lines.asSequence()
        .flatMap { pattern.findAll(it) }
        .map { it.groupValues }
        .mapNotNull {
            when (it[0]) {
                "do()" -> {
                    areInstructionsEnabled = true
                    null
                }

                "don't()" -> {
                    areInstructionsEnabled = false
                    null
                }

                else -> {
                    if (areInstructionsEnabled) {
                        Instruction(it[1].toInt(), it[2].toInt())
                    } else {
                        null
                    }
                }
            }
        }.sumOf { it.multiply() }

    println("Result is: $result")
}


data class Instruction(val a: Int, val b: Int) {
    fun multiply() = a * b
}
