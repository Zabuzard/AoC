// AOC Year 2024 Day 9
fun main() {
    val lines = {}::class.java.getResourceAsStream("input.txt")!!.bufferedReader().readLines()

    val diskRawCompact = lines.first()
    val disk = mutableListOf<Int>()

    var isFile = true
    var fileId = 0
    diskRawCompact.forEach {
        val blockId = if (isFile) fileId else -1
        repeat(it.digitToInt()) {
            disk += blockId
        }
        isFile = !isFile
        if (isFile) fileId++
    }

    disk.printDisk()

    disk.defragment()
    disk.printDisk()

    println("Checksum is: ${disk.checksum()}")
}

fun List<Int>.printDisk() = println(joinToString(separator = "") { if (it == -1) "." else it.toString() })
fun MutableList<Int>.defragment() {
    var leftIndex = 0
    var rightIndex = lastIndex

    while (true) {
        // Find empty spot
        while (this[leftIndex] != -1) {
            leftIndex++
            if (leftIndex >= rightIndex) return
        }
        // Find file block
        while (this[rightIndex] == -1) {
            rightIndex--
            if (leftIndex >= rightIndex) return
        }

        // Move block
        this[leftIndex] = this[rightIndex]
        this[rightIndex] = -1
    }
}

fun List<Int>.checksum() = map { if (it == -1) 0 else it }.mapIndexed { i, id -> i * id.toLong() }.sum()