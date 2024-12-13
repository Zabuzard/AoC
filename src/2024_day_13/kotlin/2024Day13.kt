import java.util.function.Predicate
import kotlin.jvm.optionals.getOrNull
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.roundToLong
import kotlin.streams.asStream

// AOC Year 2024 Day 13
fun main() {
    val lines = {}::class.java.getResourceAsStream("input.txt")!!.bufferedReader().readLines()

    val games = lines.chunked { it.isEmpty() }
        .map { (buttonA, buttonB, prize) ->
            Game(
                buttonA.toButtonMoves(),
                buttonB.toButtonMoves(),
                prize.toElevatedPrize()
                //prize.toPrize()
            )
        }

    var i = 0
    val tokensNeeded = games.mapNotNull {
        it.solve()
            .also { i++; println("Games solved $i/${games.size}") }
    }.sum()
    println("Tokens needed to win most games: $tokensNeeded") // 38714
}

fun List<String>.chunked(splitWhen: Predicate<String>) = flatMapIndexed { index, line ->
    when {
        index == 0 || index == lastIndex -> listOf(index)
        splitWhen.test(line) -> listOf(index - 1, index + 1)
        else -> emptyList()
    }
}.windowed(size = 2, step = 2) { (from, to) -> slice(from..to) }

fun String.toButtonMoves() = "X([+-]\\d+), Y([+-]\\d+)".toRegex()
    .find(this)
    .let { it!!.groupValues }
    .let { ButtonMoves(it[1].toInt(), it[2].toInt()) }

fun String.toPrize() = "X=(\\d+), Y=(\\d+)".toRegex()
    .find(this)
    .let { it!!.groupValues }
    .let { Prize(it[1].toLong(), it[2].toLong()) }

fun String.toElevatedPrize() = toPrize().let { Prize(it.x + 10_000_000_000_000, it.y + 10_000_000_000_000) }

data class ButtonMoves(val x: Int, val y: Int)
data class Prize(val x: Long, val y: Long)
data class Solution(val buttonAPresses: Long, val buttonBPresses: Long) {
    fun tokens() = 3 * buttonAPresses + buttonBPresses
}

data class Game(val buttonA: ButtonMoves, val buttonB: ButtonMoves, val prize: Prize) {
    // 94a + 22b = 8400
    // 34a + 67b = 5400

    fun solve(): Long? {
        // val solutions = bruteforce()
        val solutions = gauss()
        return solutions.minOfOrNull { it.tokens() }
    }

    private fun bruteforce() =
        (0L..100L).asSequence().flatMap { a -> (0L..100L).asSequence().map { b -> a to b } }
            .asStream().parallel()
            .filter { (a, b) ->
                a * buttonA.x + b * buttonB.x == prize.x
                        && a * buttonA.y + b * buttonB.y == prize.y
            }.findAny().getOrNull()
            ?.let { Solution(it.first, it.second) }
            ?.let { listOf(it) } ?: emptyList()

    private fun gauss(): List<Solution> {
        val lhs = Matrix(
            2, 2, arrayOf(
                buttonA.x.toLong(), buttonB.x.toLong(),
                buttonA.y.toLong(), buttonB.y.toLong()
            )
        )
        val rhs = Matrix(
            2, 1,
            arrayOf(prize.x, prize.y)
        )

        val solutionMatrix = EquationSolver.solve(lhs, rhs) ?: return emptyList()
        val buttonAPresses = solutionMatrix[0, 0].takeIf { abs(it - it.roundToLong()) < 0.01 }?.roundToLong()
        val buttonBPresses = solutionMatrix[0, 1].takeIf { abs(it - it.roundToLong()) < 0.01 }?.roundToLong()

        if (buttonAPresses == null || buttonBPresses == null) return emptyList()
        if (buttonAPresses < 0 || buttonBPresses < 0) return emptyList()

        return listOf(Solution(buttonAPresses, buttonBPresses)).also { println("\t" + it) }
    }
}

// From https://github.com/dionsaputra/matrix-algeo
open class Matrix<T : Number>(val rows: Int, val cols: Int) {

    lateinit var elements: Array<T>

    constructor(rows: Int, cols: Int, elements: Array<T>) : this(rows, cols) {
        this.elements = elements
        for (r in 0 until rows) {
            for (c in 0 until cols) set(r, c, elements[index(r, c)])
        }
    }

    override fun toString(): String {
        val sb = StringBuilder()
        sb.append("[")
        sb.append(elements.slice(0 until cols))
        for (r in 1 until rows) {
            sb.append(", ")
            sb.append(elements.slice(r * cols until (r + 1) * cols))
        }
        sb.append("]")
        return sb.toString()
    }

    operator fun get(r: Int, c: Int) = elements[index(r, c)]

    operator fun set(r: Int, c: Int, element: T) {
        elements[index(r, c)] = element
    }

    operator fun plus(other: Matrix<T>) = elementWise(other, Number::plus)

    operator fun minus(other: Matrix<T>) = elementWise(other, Number::minus)

    /**
     * matrix dot-product
     */
    operator fun times(other: Matrix<T>): Double {
        var res = 0.0
        for (r in 0 until rows) {
            for (c in 0 until cols) res += (this[r, c].toDouble() * other[r, c].toDouble())
        }
        return res
    }

    /**
     * matrix cross-product
     */
    infix fun <R : Number> x(other: Matrix<R>): Matrix<Double> {
        require(cols == other.rows)
        val res = Matrix(rows, other.cols, Array(rows * other.cols) { 0.0 })
        for (i in 0 until rows) {
            for (j in 0 until other.cols) {
                for (k in 0 until cols) res[i, j] += (this[i, k] * other[k, j]).toDouble()
            }
        }
        return res
    }

    fun clone() = Matrix(rows, cols, elements.copyOf())

    fun transpose(): Matrix<T> {
        val res = Matrix<T>(cols, rows, elements.copyOf())
        for (r in 0 until rows) {
            for (c in 0 until cols) res[c, r] = this[r, c]
        }
        return res
    }

    @Suppress("UNCHECKED_CAST")
    private fun elementWise(other: Matrix<T>, transform: Number.(Number) -> Number): Matrix<T> {
        require(cols == other.cols && rows == other.rows)
        val res = Matrix(rows, cols, elements.copyOf())
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                val element = this[r, c].transform(other[r, c])
                res[r, c] = element as T
            }
        }
        return res
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        other as Matrix<*>
        if (rows != other.rows || cols != other.cols) return false
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                if (!this[r, c].equalsDelta(other[r, c])) return false
            }
        }
        return true
    }

    override fun hashCode(): Int {
        var result = rows
        result = 31 * result + cols
        result = 31 * result + elements.contentHashCode()
        return result
    }

    private fun index(r: Int, c: Int) = r * cols + c

    companion object {
        inline fun <reified T : Number> diagonal(size: Int, one: T, zero: T): Matrix<T> {
            val res = Matrix<T>(size, size, Array(size * size) { zero })
            for (i in 0 until size) res[i, i] = one
            return res
        }

        fun <T : Number> square(dimen: Int, elements: Array<T>) = Matrix(dimen, dimen, elements)

        inline fun <reified T : Number> vector(vararg elements: T) = Matrix<T>(elements.size, 1, arrayOf(*elements))

    }
}

object EquationSolver {

    /**
     * compute determinant of a matrix
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : Number> det(matrix: Matrix<T>): T {
        require(matrix.rows == matrix.cols)

        if (matrix.rows == 1) return matrix[0, 0]
        if (matrix.rows == 2) return (matrix[0, 0] * matrix[1, 1] - matrix[0, 1] * matrix[1, 0]) as T

        var det = 0.0
        for (c in 0 until matrix.cols) {
            var incValue = 1.0
            var decValue = 1.0
            for (r in 0 until matrix.rows) {
                incValue *= matrix[r, (c + r) % matrix.cols].toDouble()
                decValue *= matrix[matrix.rows - r - 1, (c + r) % matrix.cols].toDouble()
            }
            det += (incValue - decValue)
        }
        return det as T
    }

    /**
     * get inverse of a matrix
     */
    fun <T : Number> inverse(matrix: Matrix<T>): Matrix<Double>? {
        require(matrix.rows == matrix.cols)
        if (det(matrix).equalsDelta(0.0)) return null

        val inverse = Matrix.diagonal(matrix.rows, 1.0, 0.0)
        val temp: Matrix<Double> =
            Matrix(matrix.rows, matrix.cols, matrix.elements.map { it.toDouble() }.toTypedArray())
        for (r in 0 until matrix.rows) {
            for (c in 0 until matrix.cols) temp[r, c] = matrix[r, c].toDouble()
        }

        for (fdRow in 0 until matrix.rows) {
            // find focus-diagonal element (first non-zero element in i-th row)
            var fdCol = 0
            while (fdCol < matrix.cols && temp[fdRow, fdCol].equalsDelta(0.0)) fdCol++

            // matrix hasn't inverse if all row is zero
            if (fdCol == matrix.cols) return null

            // scale current row so it's fd has value 1.0
            val scalingFactor = 1 / temp[fdRow, fdCol]
            for (c in 0 until matrix.cols) {
                temp[fdRow, c] *= scalingFactor
                inverse[fdRow, c] *= scalingFactor
            }

            // subtract other row with current-row * subs-factor
            for (r in 0 until matrix.rows) {
                if (r == fdRow) continue
                val subFactor = temp[r, fdCol]
                for (c in 0 until matrix.cols) {
                    temp[r, c] -= subFactor * temp[fdRow, c]
                    inverse[r, c] -= subFactor * inverse[fdRow, c]
                }
            }
        }

        return inverse
    }

    /**
     * solve matrix equation AX = B
     */
    fun <T : Number> solve(lhsMatrix: Matrix<T>, rhsMatrix: Matrix<T>): Matrix<Double>? {
        return inverse(lhsMatrix)?.let { it x rhsMatrix }
    }
}

operator fun Number.minus(other: Number): Number {
    return when (this) {
        is Long -> this.toLong() - other.toLong()
        is Int -> this.toInt() - other.toInt()
        is Short -> this.toShort() - other.toShort()
        is Byte -> this.toByte() - other.toByte()
        is Double -> this.toDouble() - other.toDouble()
        is Float -> this.toFloat() - other.toFloat()
        else -> throw RuntimeException("Unknown numeric type")
    }
}

operator fun Number.plus(other: Number): Number {
    return when (this) {
        is Long -> this.toLong() + other.toLong()
        is Int -> this.toInt() + other.toInt()
        is Short -> this.toShort() + other.toShort()
        is Byte -> this.toByte() + other.toByte()
        is Double -> this.toDouble() + other.toDouble()
        is Float -> this.toFloat() + other.toFloat()
        else -> throw RuntimeException("Unknown numeric type")
    }
}

operator fun Number.times(other: Number): Number {
    return when (this) {
        is Long -> this.toLong() * other.toLong()
        is Int -> this.toInt() * other.toInt()
        is Short -> this.toShort() * other.toShort()
        is Byte -> this.toByte() * other.toByte()
        is Double -> this.toDouble() * other.toDouble()
        is Float -> this.toFloat() * other.toFloat()
        else -> throw RuntimeException("Unknown numeric type")
    }
}

operator fun Number.div(other: Number): Number {
    return when (this) {
        is Long -> this.toLong() / other.toLong()
        is Int -> this.toInt() / other.toInt()
        is Short -> this.toShort() / other.toShort()
        is Byte -> this.toByte() / other.toByte()
        is Double -> this.toDouble() / other.toDouble()
        is Float -> this.toFloat() / other.toFloat()
        else -> throw RuntimeException("Unknown numeric type")
    }
}

fun Number.equalsDelta(other: Number): Boolean {
    return when (this) {
        is Long -> this.toLong() == other.toLong()
        is Int -> this.toInt() == other.toInt()
        is Short -> this.toShort() == other.toShort()
        is Byte -> this.toByte() == other.toByte()
        is Double -> abs(this.toDouble() - other.toDouble()) < max(Math.ulp(this), Math.ulp(other.toDouble())) * 2
        is Float -> abs(this.toFloat() - other.toFloat()) < max(Math.ulp(this), Math.ulp(other.toFloat())) * 2
        else -> throw RuntimeException("Unknown numeric type")
    }
}