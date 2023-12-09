package day6

import getInput
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.sqrt

fun main() {
    day6part1(getInput(6, true, 1))
    day6part1(getInput(6, false, 1))
    day6part2(getInput(6, true, 2))
    day6part2(getInput(6, false, 2))
}

private fun day6part1(lines: List<String>) {
    val (times, distances) = lines.map { it.getNumbers() }
    var total = 1
    for ((time, dist) in times.zip(distances)) {
        var ways = 0
        for (buttonTime in 0..time) {
            val ourDist = buttonTime * (time - buttonTime)
            if (ourDist > dist) {
                ways++
            }
        }
        total *= ways
    }
    println(total)
}

private fun day6part2(lines: List<String>) {
    val (time, dist) = lines.map { it.getSingleNumber() }
    val (r1, r2) = roots(-1, time, -dist)
    println(ceil(r2 - 1).toLong() - floor(r1 + 1).toLong() + 1)
}

private val spaces = " +".toRegex()

private fun String.getNumbers() = dropWhile { !it.isDigit() }.split(spaces).map(String::toInt)

private fun String.getSingleNumber() = filter { it.isDigit() }.toLong()

private fun roots(a: Long, b: Long, c: Long): List<Double> {
    val disc = (b * b - 4 * a * c).toDouble()
    val roots = when {
        disc > 0 -> listOf(sqrt(disc), -sqrt(disc))
        disc == 0.0 -> listOf(0.0)
        else -> emptyList()
    }
    return roots.map { (-b + it) / (2 * a) }
}