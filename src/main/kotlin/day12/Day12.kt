package day12

import com.aballano.mnemonik.memoize
import getInput

fun main() {
    day12part1(getInput(12, true, 1))
    day12part1(getInput(12, false, 1))
    day12part2(getInput(12, true, 2))
    day12part2(getInput(12, false, 2))
}

private fun day12part1(lines: List<String>) {
    val input = parseInput(lines)
    var total = 0L
    for ((pattern, groups) in input) {
        total += groups.getPossiblePatterns(pattern.length).count { it.matches(pattern) }
    }
    println(total)
}

private fun day12part2(lines: List<String>) {
    val input = parseInput(lines)
    var total = 0L
    for ((pattern, groups) in input) {
        val repeatedPattern = (1..5).joinToString("?") { pattern }
        val repeatedGroups = (1..5).flatMap { groups }
        total += repeatedGroups.totalMatchCount(repeatedPattern)
    }
    println(total)
}

private fun parseInput(lines: List<String>): List<Pair<String, List<Int>>> {
    return lines.map {
        it.split(' ').let { (a, b) -> a to b.split(',').map(String::toInt) }
    }
}

private fun List<Int>.getPossiblePatterns(length: Int): List<String> {
    val dots = length - sum()
    return (0..dots)
        .flatMap { startDots ->
            val prefix = ".".repeat(startDots)
            getPossiblePatterns(length - startDots, dots - startDots)
                .map {
                    prefix + it
                }
        }
}

private fun List<Int>.getPossiblePatterns(length: Int, dotsLeft: Int): Sequence<String> =
    when {
        size == 1 && length == first() + dotsLeft -> sequenceOf("#".repeat(first()) + ".".repeat(dotsLeft))
        size <= 1 || length <= 0 || dotsLeft <= 0 -> emptySequence()
        else -> sequence {
            val groupSize = first()
            val ourGroup = "#".repeat(groupSize)
            for (dots in 1..dotsLeft) {
                yieldAll(
                    drop(1).getPossiblePatterns(length - groupSize - dots, dotsLeft - dots).map {
                        ourGroup + ".".repeat(dots) + it
                    }
                )
            }
        }
    }

private fun List<Int>.totalMatchCount(pattern: String): Long {
    val dots = pattern.length - sum()
    return (0..pattern.startDots().coerceAtMost(dots))
        .sumOf { startDots ->
            getMatchCount(dots - startDots, pattern.drop(startDots))
        }
}

private val getMatchCount: List<Int>.(Int, String) -> Long = List<Int>::getMatchCount0.memoize()

private fun List<Int>.getMatchCount0(dotsLeft: Int, pattern: String): Long =
    when {
        size == 1 && pattern.length == first() + dotsLeft && ("#".repeat(first()) + ".".repeat(dotsLeft)).matches(pattern) -> 1L
        size <= 1 || pattern.isEmpty() || dotsLeft <= 0 -> 0L
        else -> {
            val groupSize = first()
            if (pattern.take(groupSize).any { it == '.' }) {
                0L
            } else {
                var result = 0L
                val remaining = pattern.drop(groupSize)
                for (dots in 1..remaining.startDots().coerceAtMost(dotsLeft)) {
                    result += drop(1).getMatchCount(dotsLeft - dots, remaining.drop(dots))
                }
                result
            }
        }
    }

private fun String.matches(pattern: String): Boolean {
    require(length == pattern.length)
    for (i in indices) {
        if (pattern[i] != '?' && pattern[i] != this[i]) {
            return false
        }
    }
    return true
}

private fun String.startDots() = takeWhile { it != '#' }.length