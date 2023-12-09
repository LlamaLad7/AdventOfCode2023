package day7

import getInput

fun main() {
    day7part1(getInput(7, true, 1))
    day7part1(getInput(7, false, 1))
    day7part2(getInput(7, true, 2))
    day7part2(getInput(7, false, 2))
}

private fun day7part1(lines: List<String>) {
    doPart(lines, String::getHandType1, Char::ranking1)
}

private fun day7part2(lines: List<String>) {
    doPart(lines, String::getHandType2, Char::ranking2)
}

private inline fun doPart(lines: List<String>, crossinline getHandType: String.() -> Int, crossinline ranking: Char.() -> Int) {
    val input = lines.map { it.split(' ').let { (a, b) -> a to b.toInt() } }
    val individualComparators: List<(String) -> Comparable<*>> = (0..4).map { i -> { it[i].ranking() } }
    val comparator = compareBy({ it.getHandType() }, *individualComparators.toTypedArray())
    val hands = input.sortedWith { (a, _), (b, _) -> comparator.compare(a, b) }
    println(hands.mapIndexed { index, (_, bid) -> bid * (index + 1) }.sum())
}

private fun String.getHandType1(): Int {
    val counts = groupingBy { it }.eachCount()
    return when {
        counts.size == 1 -> 5
        4 in counts.values -> 4
        3 in counts.values && 2 in counts.values -> 3
        3 in counts.values -> 2
        counts.values.count { it == 2 } == 2 -> 1
        2 in counts.values -> 0
        else -> -1
    }
}

private fun Char.ranking1() = when (this) {
    'A' -> 14
    'K' -> 13
    'Q' -> 12
    'J' -> 11
    'T' -> 10
    else -> digitToInt()
}

private fun String.getHandType2(): Int {
    val counts = groupingBy { it }.eachCountTo(hashMapOf())
    val jokers = counts.remove('J') ?: 0
    return when {
        jokers == 5 || (5 - jokers) in counts.values -> 5
        jokers == 4 || (4 - jokers) in counts.values -> 4
        jokers == 1 && counts.values.count { it == 2 } == 2 || 3 in counts.values && 2 in counts.values -> 3
        jokers == 3 || (3 - jokers) in counts.values -> 2
        counts.values.count { it == 2 } == 2 -> 1
        jokers == 1 || 2 in counts.values -> 0
        else -> -1
    }
}

private fun Char.ranking2() = when (this) {
    'A' -> 14
    'K' -> 13
    'Q' -> 12
    'J' -> 1
    'T' -> 10
    else -> digitToInt()
}