import java.awt.SystemColor.window
import java.util.stream.Collectors.toSet

// AOC Day 6
fun main() {
    val lines = {}::class.java.getResourceAsStream("input.txt")!!.bufferedReader().readLines()

    val text = lines.map(::findMarkerEnd).map { "Marker ends: $it" }

    text.forEach(::println)
}

private fun findMarkerEnd(line: String): Int {
    val marker = line.windowedSequence(14)
        .withIndex()
        .first { (_, window) -> isAllDifferent(window) }
    return marker.index + marker.value.length
}

private fun isAllDifferent(window: String): Boolean =
    window.toSet().size == window.length
