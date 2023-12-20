package day19

import getInput
import split

fun main() {
    day19part1(getInput(19, true, 1))
    day19part1(getInput(19, false, 1))
    day19part2(getInput(19, true, 2))
    day19part2(getInput(19, false, 2))
}

private fun day19part1(lines: List<String>) {
    val (workflows, parts) = lines.parse()
    val q = parts.mapTo(ArrayDeque()) { it to "in" }
    var total = 0
    while (q.isNotEmpty()) {
        val (part, workflow) = q.removeFirst()
        if (workflow == "A") {
            total += part.number()
            continue
        }
        if (workflow == "R") {
            continue
        }
        q.addLast(part to workflows.getValue(workflow).process(part))
    }
    println(total)
}

private fun day19part2(lines: List<String>) {
    val (workflows) = lines.parse()
    val q = ArrayDeque(listOf(Parts("xmas".associateWith { 1..4000 }) to "in"))
    val passed = mutableListOf<Parts>()
    while (q.isNotEmpty()) {
        val (parts, workflow) = q.removeFirst()
        if (workflow == "A") {
            passed.add(parts)
            continue
        }
        if (workflow == "R") {
            continue
        }
        q.addAll(workflows.getValue(workflow).process(parts))
    }
    // Not strictly correct but seems to work
    println(passed.sumOf { it.ratings.values.fold(1L) { acc, x -> acc * x.size } })
}

private fun List<String>.parse(): Pair<Map<String, Workflow>, List<Part>> {
    val (a, b) = split("")
    val workflows = a.associate {
        val name = it.substringBefore('{')
        val rules = it.removePrefix(name).removeSurrounding("{", "}").splitToSequence(',').toMutableList()
        val default = rules.removeLast()
        name to Workflow(
            rules.map { rule ->
                val r = StringReader(rule)
                val cat = r.next()
                val comp = Comparison.fromChar(r.next())
                val operand = r.takeWhile(Char::isDigit).toInt()
                r.next() // :
                val dest = r.remaining()
                Rule(cat, comp, operand, dest)
            },
            default
        )
    }
    val parts = b.map {
        val ratings = it.removeSurrounding("{", "}").split(',').associate { rating ->
            rating.first() to rating.drop(2).toInt()
        }
        Part(ratings)
    }
    return workflows to parts
}

private data class Workflow(val rules: List<Rule>, val default: String) {
    fun process(part: Part): String = rules.firstOrNull { it.accepts(part) }?.dest ?: default

    fun process(parts: Parts) = buildList {
        var current = parts
        for (rule in rules) {
            rule.passing(current)?.let { add(it to rule.dest) }
            current = rule.failing(current) ?: return@buildList
        }
        add(current to default)
    }
}

private data class Rule(val category: Char, val comparison: Comparison, val operand: Int, val dest: String) {
    fun accepts(part: Part) = comparison.compare(part.ratings.getValue(category), operand)

    fun passing(parts: Parts): Parts? {
        val result = parts.ratings.toMutableMap()
        val range = parts.ratings.getValue(category)
        result[category] = when (comparison) {
            Comparison.GT -> range.greaterThan(operand)
            Comparison.LT -> range.lessThan(operand)
        }
        return if (result.getValue(category).isEmpty()) null else Parts(result)
    }

    fun failing(parts: Parts): Parts? {
        val result = parts.ratings.toMutableMap()
        val range = parts.ratings.getValue(category)
        result[category] = when (comparison) {
            Comparison.GT -> range.lessThan(operand + 1)
            Comparison.LT -> range.greaterThan(operand - 1)
        }
        return if (result.getValue(category).isEmpty()) null else Parts(result)
    }
}

private enum class Comparison {
    GT {
        override fun compare(left: Int, right: Int) = left > right
    },
    LT {
        override fun compare(left: Int, right: Int) = left < right
    },
    ;

    abstract fun compare(left: Int, right: Int): Boolean

    companion object {
        fun fromChar(c: Char) = when (c) {
            '>' -> GT
            '<' -> LT
            else -> error("Invalid comparison $c")
        }
    }
}

private data class Part(val ratings: Map<Char, Int>) {
    fun number() = ratings.values.sum()
}

private data class Parts(val ratings: Map<Char, IntRange>)

private class StringReader(private var contents: String) {
    inline fun takeWhile(predicate: (Char) -> Boolean): String =
        contents.takeWhile(predicate).also { contents = contents.removePrefix(it) }

    fun next() = contents.first().also { contents = contents.drop(1) }

    fun remaining() = contents.also { contents = "" }
}

private fun IntRange.greaterThan(min: Int) = maxOf(first, min + 1)..last

private fun IntRange.lessThan(max: Int) = first..minOf(last, max - 1)

private val IntRange.size get() = (last - first + 1).coerceAtLeast(0)