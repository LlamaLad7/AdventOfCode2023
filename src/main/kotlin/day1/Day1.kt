package day1

import getInput

fun main() {
    day1part1(getInput(1, true, 1))
    day1part1(getInput(1, false, 1))
    day1part2(getInput(1, true, 2))
    day1part2(getInput(1, false, 2))
}

private fun day1part1(lines: List<String>) {
    println(lines.sumOf { line -> line.first { it.isDigit() }.digitToInt() * 10 + line.last { it.isDigit() }.digitToInt() })
}

private val valid = mapOf(
    "one" to 1,
    "two" to 2,
    "three" to 3,
    "four" to 4,
    "five" to 5,
    "six" to 6,
    "seven" to 7,
    "eight" to 8,
    "nine" to 9
) + ('0'..'9').associateBy({ it.toString() }) { it.digitToInt() }

private fun day1part2(lines: List<String>) {
    println(lines.sumOf { it.calibrationValue() })
}

private fun String.calibrationValue(): Int {
    val first = valid.minBy { (k, _) -> indexOf(k).takeIf { it >= 0 } ?: Int.MAX_VALUE }.value
    val second = valid.maxBy { (k, _) -> lastIndexOf(k).takeIf { it >= 0 }?.plus(k.length - 1) ?: Int.MIN_VALUE }.value
    return first * 10 + second
}