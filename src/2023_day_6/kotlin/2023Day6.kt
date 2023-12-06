import java.math.BigInteger

// AOC Year 2023 Day 6
fun main() {
    val lines = {}::class.java.getResourceAsStream("input.txt")!!.bufferedReader().readLines()

    val (times, distances) = lines.map { line ->
        line.replace("\\s+".toRegex(), "").split(':').drop(1).map { it.toBigInteger() }
    }
    val races = times.indices.map { i -> Race(times[i], distances[i]) }

    val margin = races.map { it.winningSetups() }.reduce(BigInteger::times)

    println("Margin: $margin")
}

data class Race(val time: BigInteger, val distanceRecord: BigInteger) {
    private val LOG_EVERY = 5_000_000.toBigInteger()

    private fun isWinning(chargingTime: BigInteger): Boolean {
        if (chargingTime % LOG_EVERY == BigInteger.ZERO) {
            println("$chargingTime/$time")
        }

        if (chargingTime !in BigInteger.ONE..<time) {
            return false
        }

        val speed = chargingTime
        val remainingTime = time - chargingTime
        val distance = remainingTime * speed
        return distance > distanceRecord
    }

    fun winningSetups(): BigInteger {
        var count = BigInteger.ZERO
        var i = BigInteger.ONE
        while (i < time) {
            if (isWinning(i)) {
                count++
            }
            i++
        }
        return count
    }
}