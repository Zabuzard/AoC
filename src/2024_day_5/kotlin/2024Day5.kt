// AOC Year 2024 Day 5
fun main() {
    val lines = {}::class.java.getResourceAsStream("input.txt")!!.bufferedReader().readLines()

    val rules = lines.takeWhile { it.isNotEmpty() }.map { UpdateRule(it) }
    val updates = lines.takeLastWhile { it.isNotEmpty() }.map { Update(it) }

    val middlePageSum = updates.filter { it.isValid(rules) }.sumOf { it.middlePage() }
    println("Middle page sum: $middlePageSum")
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
