import java.util.function.Consumer
import kotlin.math.pow

// AOC Year 2024 Day 17
fun main() {
    val lines = {}::class.java.getResourceAsStream("input.txt")!!.bufferedReader().readLines()

    val (a, b, c) = lines.takeWhile { it.isNotEmpty() }
        .map { it.split(": ")[1] }
        .map { it.toInt() }
    val instructionsText = lines.last().split(": ")[1]
    val instructions = instructionsText.split(",").map { it.toInt() }
    val computer = Computer(a, b, c, instructions)

    computer.runAll(100_000)
    println(computer.output())

    // Part 2
    for (aCandidate in 0..1_000_000) {
        val computerAttempt = Computer(aCandidate, b, c, instructions)
        computerAttempt.runAll(100_000)
        val output = computerAttempt.output()
        if (output == instructionsText) {
            println("Found: $aCandidate")
            break
        }

        if (aCandidate % 1_000 == 0) {
            println("Progress: $aCandidate/1_000_000")
        }
    }
}

class Computer(
    private var registerA: Int,
    private var registerB: Int,
    private var registerC: Int,
    private val instructions: List<Int>
) {
    private val output = mutableListOf<Int>()
    private var instructionPointer = 0

    fun runCycle() {
        val opCode = instructions[instructionPointer]
        val operand = instructions[instructionPointer + 1]

        when (opCode) {
            0 -> adv(operand)
            1 -> bxl(operand)
            2 -> bst(operand)
            3 -> jnz(operand)
            4 -> bxc()
            5 -> out(operand)
            6 -> bdv(operand)
            7 -> cdv(operand)
        }
        if (opCode != 3) {
            instructionPointer += 2
        }
    }

    fun runAll(forceStopAfter: Int) {
        var i = 0
        while (instructionPointer in instructions.indices) {
            runCycle()

            if (i >= forceStopAfter) {
                break
            }
            i++
        }
    }

    fun output() = output.joinToString(separator = ",")

    private fun combo(operand: Int) = when (operand) {
        0, 1, 2, 3 -> operand
        4 -> registerA
        5 -> registerB
        6 -> registerC
        else -> throw AssertionError()
    }

    private fun adv(operand: Int) = registerDivision(operand) { registerA = it }

    private fun bxl(operand: Int) {
        registerB = registerB xor operand
    }

    private fun bst(operand: Int) {
        registerB = combo(operand) % 8
    }

    private fun jnz(operand: Int) {
        if (registerA == 0) return
        instructionPointer = operand
    }

    private fun bxc() {
        registerB = registerB xor registerC
    }

    private fun out(operand: Int) {
        val result = combo(operand) % 8
        output += result
    }

    private fun bdv(operand: Int) = registerDivision(operand) { registerB = it }

    private fun cdv(operand: Int) = registerDivision(operand) { registerC = it }

    private fun registerDivision(operand: Int, consumer: Consumer<Int>) {
        val numerator = registerA
        val denominator = 2.0.pow(combo(operand).toDouble())
        val result = (numerator / denominator).toInt()
        consumer.accept(result)
    }
}
