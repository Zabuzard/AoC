// AOC Year 2025 Day 4
fun main() {
    val lines = {}::class.java.getResourceAsStream("input.txt")!!.bufferedReader().readLines()

    val grid = Grid(lines.map { it.toMutableList() })

    val accessiblePaperRollsPart1 = grid.findAccessiblePaperRolls().size
    println("Accessible Paper Rolls Part1: $accessiblePaperRollsPart1")

    var removedPaperRolls = 0
    while (true) {
        val accessiblePaperRolls = grid.findAccessiblePaperRolls()
        if (accessiblePaperRolls.isEmpty()) {
            break
        }
        accessiblePaperRolls.forEach { (x, y) -> grid.removePaperRoll(x, y) }
        removedPaperRolls += accessiblePaperRolls.size
    }
    println("Removed Paper Rolls Part2: $removedPaperRolls")

    // grid.printMarked()
}

data class Grid(val grid: List<MutableList<Char>>) {
    val width = grid.first().size
    val height = grid.size

    fun isValidIndex(x: Int, y: Int) = x in 0..<width && y in 0..<height
    fun isPaperRoll(x: Int, y: Int) = grid[y][x] == '@'

    fun isAccessible(x: Int, y: Int) = listOf(
        x - 1 to y + 1,
        x to y + 1,
        x + 1 to y + 1,

        x - 1 to y,
        x + 1 to y,

        x - 1 to y - 1,
        x to y - 1,
        x + 1 to y - 1,
    ).filter { isValidIndex(it.first, it.second) }
        .count { isPaperRoll(it.first, it.second) } < 4

    fun findAccessiblePaperRolls() =
        grid.indices.flatMap { y -> grid.first().indices.map { x -> x to y } }
            .filter { (x, y) -> isPaperRoll(x, y) }
            .filter { (x, y) -> isAccessible(x, y) }

    fun removePaperRoll(x: Int, y: Int) {
        grid[y][x] = '.'
    }

    fun printMarked() {
        for (y in 0..<height) {
            for (x in 0..<width) {
                if (isPaperRoll(x, y) && isAccessible(x, y)) {
                    print('x')
                } else {
                    print(grid[y][x])
                }
            }
            println()
        }
    }
}
