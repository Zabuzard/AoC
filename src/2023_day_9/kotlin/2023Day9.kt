// AOC Year 2023 Day 9
fun main() {
    val lines = {}::class.java.getResourceAsStream("input.txt")!!.bufferedReader().readLines()

    val estimateSum = lines.map { it.toHistory() }.sumOf { it.estimateNext() }
    println("Estimate sum: $estimateSum")
}

data class History(val values: List<Int>) {
    fun estimateNext(): Int {
        val lines = mutableListOf(values)
        while (true) {
            var allZero = true
            val line = lines.last().windowed(2) {
                (it[1] - it[0]).also { diff ->
                    if (diff != 0) allZero = false
                }
            }.toList()

            lines += line
            if (allZero) break
        }

        val estimate = lines.foldRight(0) { line, lineEstimate ->
            (line.first() - lineEstimate)
        }
        return estimate
    }
}

fun String.toHistory() = split(' ').map { it.toInt() }.toList().let { History(it) }
