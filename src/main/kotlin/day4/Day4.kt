package day4

import getInput

fun main() {
    day4part1(getInput(4, true, 1))
    day4part1(getInput(4, false, 1))
    day4part2(getInput(4, true, 2))
    day4part2(getInput(4, false, 2))
}

private fun day4part1(lines: List<String>) {
    var sum = 0
    for (line in lines) {
        val (winning, mine) = parseLine(line)
        sum += (winning intersect mine).size.let { if (it == 0) 0 else 1 shl (it - 1) }
    }
    println(sum)
}

private fun day4part2(lines: List<String>) {
    val cards = lines.map { parseLine(it).let { (winning, mine) -> Card(winning, mine) } }
    var num = cards.size
    outer@while (true) {
        for ((i, card) in cards.withIndex()) {
            val count = card.count
            if (count == 0) continue
            val matches = card.matches
            for (next in 1..matches) {
                cards[i + next].count += count
                num += count
            }
            card.count = 0
            continue@outer
        }
        break
    }
    println(num)
}

private fun parseLine(line: String): Pair<Set<Int>, Set<Int>> = line
    .substringAfter(": ")
    .split(" | ")
    .map { it.split(' ').filterNot(String::isBlank).mapTo(hashSetOf(), String::toInt) }
    .let { (a, b) -> a to b }

private data class Card(val winning: Set<Int>, val mine: Set<Int>, var count: Int = 1) {
    val matches = (winning intersect mine).size
}