// AOC Year 2024 Day 5
fun main() {
    val lines = {}::class.java.getResourceAsStream("input.txt")!!.bufferedReader().readLines()

    val rules = lines.takeWhile { it.isNotEmpty() }.map { UpdateRule(it) }
    val updates = lines.takeLastWhile { it.isNotEmpty() }.map { Update(it) }

    val middlePageSum = updates.filter { it.isValid(rules) }.sumOf { it.middlePage() }
    println("Middle page sum: $middlePageSum")

    val invalidUpdates = updates.filter { !it.isValid(rules) }.toList()
    var updatesDone = 0
    val invalidMiddlePageSum = invalidUpdates.map { update ->
        var nextUpdate: Update
        val permutations = mutableListOf<List<Int>>()
        update.pages.toList().permutations().forEach { permutations += it }
        var permutationsDone = 0
        for (pagePermutation in permutations) {
            nextUpdate = Update(pagePermutation.joinToString(separator = ","))
            if (nextUpdate.isValid(rules)) {
                updatesDone++
                println("Updates done ($updatesDone/${invalidUpdates.size})")
                return@map nextUpdate
            }

            permutationsDone++
            println("\tPermutations done ($permutationsDone/${permutations.size})")
        }
        throw AssertionError("Unreachable")
    }.sumOf { it.middlePage() }

    println("Invalid Middle page sum: $invalidMiddlePageSum")
}

fun Int.factorial(): Long {
    require(0 <= this) { "Factorial is undefined for negative numbers." }
    // Not using BigDecimal, so we must fit inside a Long.
    require(this < 21) { "Factorial is undefined for numbers greater than 20." }
    return when (this) {
        0, 1 -> 1L
        else -> (2..this).fold(1L) { acc, i -> acc * i }
    }
}

/**
 * Iterates the permutations of the receiver array.
 * By using an iterator, we minimize the memory footprint.
 */
fun <T> List<T>.permutations(): Iterator<List<T>> {
    // The nth permutation of the receiver list.
    fun <T> List<T>.permutation(nth: Long): List<T> {
        if (isEmpty()) return emptyList()
        val index = (nth % size)
            .also { require(it < Int.MAX_VALUE) }
            .toInt()
        // Grab the first element...
        val head = elementAt(index)
        // ...make a list of what's left...
        val tail = slice(indices.filter { it != index })
        // ...permute it...
        val tailPerm = tail.permutation(nth / size)
        // ...jam it all together.
        return listOf(head) + tailPerm
    }

    val total = size.factorial()
    return object : Iterator<List<T>> {
        var current = 0L
        override fun hasNext(): Boolean = current < total

        override fun next(): List<T> {
            require(hasNext()) { "No more permutations." }
            return this@permutations.permutation(current++)
        }
    }
}

// 47|53
data class UpdateRule(private val input: String) {
    val pageBefore: Int
    val pageAfter: Int

    init {
        val (left, right) = input.split("|")
            .map { it.toInt() }
        pageBefore = left
        pageAfter = right
    }
}

data class UpdateRuleset(val rules: List<UpdateRule>, val filterForPages: Set<Int>) {
    private val pageAfterToRule = rules.filter {
        filterForPages.contains(it.pageBefore) && filterForPages.contains(it.pageAfter)
    }.groupBy { it.pageAfter }

    fun isValid(page: Int, pagesBefore: Set<Int>) = pageAfterToRule.getOrElse(page) { listOf() }
        .map { it.pageBefore }
        .all { pagesBefore.contains(it) }
}

// 75,47,61,53,29
data class Update(private val input: String) {
    val pages = LinkedHashSet<Int>()

    init {
        input.split(",")
            .map { it.toInt() }
            .toCollection(pages)
    }

    fun isValid(rules: List<UpdateRule>): Boolean {
        val ruleset = UpdateRuleset(rules, pages)
        val pagesBefore = mutableSetOf<Int>()
        return pages.all { page -> ruleset.isValid(page, pagesBefore).also { pagesBefore += page } }
    }

    fun middlePage() = pages.elementAt(pages.size / 2)
}
