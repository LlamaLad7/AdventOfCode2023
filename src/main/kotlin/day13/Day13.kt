package day13

import getInput
import split

fun main() {
    day13part1(getInput(13, true, 1))
    day13part1(getInput(13, false, 1))
    day13part2(getInput(13, true, 2))
    day13part2(getInput(13, false, 2))
}

private fun day13part1(lines: List<String>) {
    var total = 0
    outer@for (pattern in lines.split("")) {
        for (col in 1 until pattern[0].length) {
            if (pattern.reflectsOnColumn(col)) {
                total += col
                continue@outer
            }
        }
        for (row in 1 until pattern.size) {
            if (pattern.reflectsOnRow(row)) {
                total += row * 100
                break
            }
        }
    }
    println(total)
}

private fun day13part2(lines: List<String>) {
    var total = 0
    outer@for (pattern in lines.split("")) {
        for (col in 1 until pattern[0].length) {
            if (pattern.mismatchingReflectedOnColumn(col) == 1) {
                total += col
                continue@outer
            }
        }
        for (row in 1 until pattern.size) {
            if (pattern.mismatchingReflectedOnRow(row) == 1) {
                total += row * 100
                break
            }
        }
    }
    println(total)
}

private fun List<String>.reflectsOnColumn(col: Int): Boolean {
    for (delta in 0..(col - 1).coerceAtMost(first().lastIndex - col)) {
        val l = col - 1 - delta
        val r = col + delta
        if (any { it[l] != it[r] }) {
            return false
        }
    }
    return true
}

private fun List<String>.reflectsOnRow(row: Int): Boolean {
    for (delta in 0..(row - 1).coerceAtMost(lastIndex - row)) {
        val l = row - 1 - delta
        val r = row + delta
        if (this[l] != this[r]) {
            return false
        }
    }
    return true
}

private fun List<String>.mismatchingReflectedOnColumn(col: Int): Int {
    var result = 0
    for (delta in 0..(col - 1).coerceAtMost(first().lastIndex - col)) {
        val l = col - 1 - delta
        val r = col + delta
        result += count { it[l] != it[r] }
    }
    return result
}

private fun List<String>.mismatchingReflectedOnRow(row: Int): Int {
    var result = 0
    for (delta in 0..(row - 1).coerceAtMost(lastIndex - row)) {
        val l = row - 1 - delta
        val r = row + delta
        result += this[l].zip(this[r]).count { (a, b) -> a != b }
    }
    return result
}