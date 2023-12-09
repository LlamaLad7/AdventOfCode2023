package day9

import getInput

fun main() {
    day9part1(getInput(9, true, 1))
    day9part1(getInput(9, false, 1))
    day9part2(getInput(9, true, 2))
    day9part2(getInput(9, false, 2))
}

private fun day9part1(lines: List<String>) {
    var sum = 0
    for (line in lines) {
        val seq = line.getSeq()
        seq.last().add(0)
        for ((it, prev) in seq.asReversed().zipWithNext()) {
            prev.add(prev.last() + it.last())
        }
        sum += seq.first().last()
    }
    println(sum)
}

private fun day9part2(lines: List<String>) {
    var sum = 0
    for (line in lines) {
        val seq = line.getSeq()
        seq.last().add(0, 0)
        for ((it, prev) in seq.asReversed().zipWithNext()) {
            prev.add(0, prev.first() - it.first())
        }
        sum += seq.first().first()
    }
    println(sum)
}

private fun String.getSeq(): MutableList<MutableList<Int>> {
    val seq = mutableListOf(split(' ').mapTo(mutableListOf(), String::toInt))
    while (seq.last().any { it != 0 }) {
        seq.add(seq.last().zipWithNext { a, b -> b - a }.toMutableList())
    }
    return seq
}