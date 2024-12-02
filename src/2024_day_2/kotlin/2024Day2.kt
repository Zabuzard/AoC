import kotlin.math.abs

// AOC Year 2024 Day 2
fun main() {
    val lines = {}::class.java.getResourceAsStream("input.txt")!!.bufferedReader().readLines()

    val reports = lines.map { it.parseReport() }

    val safeReports = reports.count { it.isSafe() }
    val safeReportsWithDampener = reports.count { it.isSafeWithDampener() }
    println("Safe Reports: $safeReports")
    println("Safe Reports with Dampener: $safeReportsWithDampener")
}

fun String.parseReport() = Report(split(" ").map { it.toInt() })

data class Report(val levels: List<Int>) {
    private fun areLevelsSafe(levels: List<Int>): Boolean {
        val isAscending = levels[0] < levels[1]
        return levels.zipWithNext()
            .all { (left, right) ->
                val isDistOkay = abs(left - right) in 1..3
                val isOrderOkay = if (isAscending) left < right else left > right
                isDistOkay && isOrderOkay
            }
    }

    fun isSafe() = areLevelsSafe(levels)

    fun isSafeWithDampener(): Boolean {
        if (isSafe()) return true
        return levels.indices.any { indexToRemove ->
            val dampenedLevels = levels.filterIndexed { i, _ -> i != indexToRemove }
            areLevelsSafe(dampenedLevels)
        }
    }
}
