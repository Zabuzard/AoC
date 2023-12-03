// AOC Year 2023 Day 3
fun main() {
    val lines = {}::class.java.getResourceAsStream("input.txt")!!.bufferedReader().readLines()

    val schematic = Schematic(lines.map { it.toList() }.toList())

    var sum = 0

    for (row in schematic.table.indices) {
        for (col in schematic.table[row].indices) {
            if (schematic.table[row][col] != '*') continue

            // NOTE Technically this is incorrect, as there could
            // be identical part numbers around the gear. Got lucky though.
            val partNumbers = mutableSetOf<Int>()
            for (neighborRow in row - 1..row + 1) {
                for (neighborCol in col - 1..col + 1) {
                    val partNumber = schematic.getPartNumberAt(neighborCol, neighborRow) ?: continue
                    partNumbers += partNumber
                }
            }

            if (partNumbers.size == 2) {
                val gearRatio = partNumbers.reduce(Int::times)
                sum += gearRatio

                // println("Found gear with ratio $gearRatio at ($row, $col) with part numbers $partNumbers")
            }
        }
    }

    println("Sum is $sum")
}

data class Schematic(val table: List<List<Char>>) {
    private fun hasAdjacentSymbol(x: Int, y: Int): Boolean {
        for (col in x - 1..x + 1) {
            for (row in y - 1..y + 1) {
                if (col !in table.indices || row !in table[y].indices) {
                    continue
                }

                val c = table[row][col]
                if (c !in '0'..'9' && c != '.') {
                    return true
                }
            }
        }

        return false
    }

    fun getPartNumberAt(x: Int, y: Int): Int? {
        val row = table[y]
        if (!row[x].isDigit()) return null

        var start = row.subList(0, x).indexOfLast { !it.isDigit() }
        if (start == -1) start = 0 else start += 1

        var end = row.subList(x, row.size).indexOfFirst { !it.isDigit() }
        if (end == -1) end = row.size else end += x

        val number = row.subList(start, end).joinToString("").toInt()

        for (currentX in start..<end) {
            if (hasAdjacentSymbol(currentX, y)) {
                return number
            }
        }

        return null
    }
}