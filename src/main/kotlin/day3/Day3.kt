package day3

import getInput

fun main() {
    day3part1(getInput(3, true, 1))
    day3part1(getInput(3, false, 1))
    day3part2(getInput(3, true, 2))
    day3part2(getInput(3, false, 2))
}

private val number = "[0-9]+".toRegex()

private fun day3part1(lines: List<String>) {
    var sum = 0
    for ((y, line) in lines.withIndex()) {
        for (match in number.findAll(line)) {
            for ((adjX, adjY) in match.range.adjacent(y)) {
                if (lines.getOrNull(adjY)?.getOrNull(adjX)?.isSymbol() == true) {
                    sum += match.value.toInt()
                }
            }
        }
    }
    println(sum)
}


private fun day3part2(lines: List<String>) {
    val gears = hashMapOf<Pair<Int, Int>, MutableList<Int>>()
    for ((y, line) in lines.withIndex()) {
        for (match in number.findAll(line)) {
            for ((adjX, adjY) in match.range.adjacent(y)) {
                if (lines.getOrNull(adjY)?.getOrNull(adjX) == '*') {
                    gears.getOrPut(adjX to adjY, ::mutableListOf).add(match.value.toInt())
                }
            }
        }
    }
    println(gears.values.filter { it.size == 2 }.sumOf { it.reduce(Int::times) })
}

private fun IntRange.adjacent(y: Int): Set<Pair<Int, Int>> = buildSet {
    for (x in this@adjacent) {
        add(x to y - 1)
        add(x to y + 1)
    }
    for (dy in -1..1) {
        add(first - 1 to y + dy)
        add(last + 1 to y + dy)
    }
}

private fun Char.isSymbol() = when (this) {
    '.', in '0'..'9' -> false
    else -> true
}