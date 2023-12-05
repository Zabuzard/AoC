import java.util.concurrent.atomic.AtomicLong
import java.util.function.Predicate
import kotlin.time.TimeSource

// AOC Year 2023 Day 5
fun main() {
    val lines = {}::class.java.getResourceAsStream("input.txt")!!.bufferedReader().readLines()

    val groups = lines.chunked { it.isEmpty() }

    val seedRanges = groups.first().first().split("\\s+".toRegex()).drop(1).map { it.toLong() }
        .chunked(2).map {
            it[0]..<it[0] + it[1]
        }
    val idMaps = groups.drop(1).map { it.toIdMap() }

    val totalSeeds = seedRanges.sumOf { it.last - it.first }

    val timeSource = TimeSource.Monotonic
    val startTime = timeSource.markNow()

    val seedsDone = AtomicLong(0)
    val closestLocation = seedRanges.parallelStream().map {
        it.asSequence()
            .map { seed ->
                idMaps.fold(seed) { id, map -> map.mapId(id) }
                    .also {
                        seedsDone.incrementAndGet()
                        val seedsDoneFixed = seedsDone.toLong()
                        if (seedsDoneFixed % 5_000_000L == 0L) {
                            val seedsRemaining = totalSeeds - seedsDoneFixed

                            val percentage = ((seedsDoneFixed.toDouble() / totalSeeds) * 100).toInt()

                            val currentTime = timeSource.markNow()
                            val duration = currentTime - startTime
                            val remainingTime = (duration / seedsDoneFixed.toDouble()) * seedsRemaining.toDouble()

                            println("transformed seed $seedsDone/$totalSeeds ($percentage %, ETA: $remainingTime)")
                        }
                    }
            }.min()
    }.toList().min()

    println("Closest location is: $closestLocation")
}

fun List<String>.chunked(splitWhen: Predicate<String>) = flatMapIndexed { index, line ->
    when {
        index == 0 || index == lastIndex -> listOf(index)
        splitWhen.test(line) -> listOf(index - 1, index + 1)
        else -> emptyList()
    }
}.windowed(size = 2, step = 2) { (from, to) -> slice(from..to) }

class IdRange(sourceStart: Long, destinationStart: Long, private val length: Int) {
    private val source: LongRange
    private val destination: LongRange

    init {
        source = sourceStart..<sourceStart + length
        destination = destinationStart..<destinationStart + length
    }

    fun mapId(id: Long) = (id - source.first).let {
        if (it < 0 || it >= length) null else destination.first + it
    }
}

fun String.toIdRange() = split(" ").map { it.toLong() }.let {
    IdRange(it[1], it[0], it[2].toInt())
}

data class IdMap(val name: String, val idRanges: List<IdRange>) {
    fun mapId(id: Long) = idRanges.asSequence()
        .mapNotNull { it.mapId(id) }
        .firstOrNull() ?: id
}

fun List<String>.toIdMap() = IdMap(
    first().split(" ").first(),
    drop(1).map { it.toIdRange() }
)

