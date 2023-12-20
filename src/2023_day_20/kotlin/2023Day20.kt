// AOC Year 2023 Day 20
fun main() {
    val lines = {}::class.java.getResourceAsStream("input.txt")!!.bufferedReader().readLines()

    val nameToModule = mutableMapOf<String, Module>()
    val conjunctionModules = mutableSetOf<String>()
    lines.map { it.split(" -> ") }.map { (moduleRaw, destinationsRaw) ->
        val destinations = destinationsRaw.split(", ")

        val module = when (moduleRaw.first()) {
            '%' -> FlipFlop(moduleRaw.drop(1), destinations)
            '&' -> Conjunction(moduleRaw.drop(1), destinations).also { conjunctionModules += it.name }
            else -> Broadcaster(moduleRaw, destinations)
        }

        nameToModule[module.name] = module
    }

    nameToModule.values.map { source ->
        source.name to source.destinations.map { nameToModule[it] }.filterIsInstance<Conjunction>()
    }.forEach { (source, destinations) ->
        destinations.forEach {
            it.addSource(source)
        }
    }

    val pulsesToProcess = ArrayDeque<Pulse>()
    val pulseToCount = mutableMapOf<PulseType, Long>()
    // var buttonPresses = 0
    val sandModule = "rx"
    while (true) {
        pulsesToProcess += pressButton()
        buttonPresses++

        val sandPulseToCount = mutableMapOf<PulseType, Long>()
        while (pulsesToProcess.isNotEmpty()) {
            val pulse = pulsesToProcess.removeFirst()
            if (pulse.destination == sandModule) {
                sandPulseToCount[pulse.type] = (sandPulseToCount[pulse.type] ?: 0L) + 1L
            }

            pulseToCount[pulse.type] = (pulseToCount[pulse.type] ?: 0L) + 1L

            val nextPulses = nameToModule[pulse.destination]?.onPulse(pulse) ?: listOf()
            pulsesToProcess += nextPulses
        }

        if (sandPulseToCount[PulseType.LOW] == 1L && sandPulseToCount[PulseType.HIGH] == 0L) {
            break
        }

        if (buttonPresses % 100_000 == 0) {
            println("Button presses: $buttonPresses")
        }
    }

    val score = pulseToCount.values.reduce(Long::times)
    println("pulseToCount: $pulseToCount")
    println("Score: $score")
    println("Button presses: $buttonPresses")
}

var buttonPresses = 0

fun pressButton() = listOf(Pulse(PulseType.LOW, "button", "broadcaster"))

enum class PulseType {
    LOW, HIGH
}

data class Pulse(val type: PulseType, val source: String, val destination: String)

sealed class Module(val name: String, val destinations: List<String>) {
    abstract fun onPulse(pulse: Pulse): List<Pulse>
    fun pulseToAll(type: PulseType) = destinations.map { Pulse(type, name, it) }
}

class Broadcaster(name: String, destinations: List<String>) : Module(name, destinations) {
    override fun onPulse(pulse: Pulse) = pulseToAll(pulse.type)
}

class FlipFlop(name: String, destinations: List<String>) : Module(name, destinations) {
    private var isOn = false

    override fun onPulse(pulse: Pulse): List<Pulse> {
        if (pulse.type == PulseType.HIGH) return listOf()

        isOn = !isOn
        val type = if (isOn) PulseType.HIGH else PulseType.LOW
        return pulseToAll(type)
    }
}

class Conjunction(name: String, destinations: List<String>) : Module(name, destinations) {
    private val lastPulseFromSource = mutableMapOf<String, PulseType>()

    fun addSource(source: String) {
        lastPulseFromSource[source] = PulseType.LOW
    }

    override fun onPulse(pulse: Pulse): List<Pulse> {
        lastPulseFromSource[pulse.source] = pulse.type

        if (name == "rm" && pulse.type == PulseType.HIGH) {
            println("HIGH on rm for input ${pulse.source} after $buttonPresses presses")
        }

        val type = if (lastPulseFromSource.values.all { it == PulseType.HIGH }) PulseType.LOW else PulseType.HIGH
        return pulseToAll(type)
    }
}
