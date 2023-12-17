// AOC Year 2023 Day 15
fun main() {
    val line = {}::class.java.getResourceAsStream("input.txt")!!.bufferedReader().readText()

    val boxes = (0..255).associateWith { Box(it) }

    line.split(',').forEach { operation ->
        if (operation.endsWith('-')) {
            val label = operation.dropLast(1)
            boxes[label.lavaHash()]!!.removeConfig(label)
        } else {
            val (label, focusLength) = operation.split('=')
            val config = Config(label, focusLength.toInt())
            boxes[label.lavaHash()]!!.addConfig(config)
        }
    }

    val focusingPower = boxes.values.sumOf { it.focusingPower() }

    println("Focusing power: $focusingPower")
}

fun String.lavaHash() = asSequence().fold(0) { current, c ->
    ((current + c.code) * 17) % 256
}

class Box(private val index: Int) {
    private val configs = mutableListOf<Config>()

    fun addConfig(config: Config) {
        val i = find(config.label)
        if (i == -1) {
            configs += config
        } else {
            configs[i] = config
        }
    }

    private fun find(label: String) = configs.indexOfFirst { it.label == label }

    fun removeConfig(label: String) {
        val i = find(label)
        if (i != -1) {
            configs.removeAt(i)
        }
    }

    fun focusingPower(): Int {
        val boxNumber = index + 1
        return configs.mapIndexed { i, config ->
            val slot = i + 1
            boxNumber * slot * config.focalLength
        }.sum()
    }
}

data class Config(val label: String, val focalLength: Int)