package day2

import getInput

fun main() {
    day2part1(getInput(2, true, 1))
    day2part1(getInput(2, false, 1))
    day2part2(getInput(2, true, 2))
    day2part2(getInput(2, false, 2))
}

private fun day2part1(lines: List<String>) {
    var sum = 0
    outer@for ((i, line) in lines.withIndex()) {
        val id = i + 1
        for ((a, b) in line.split(' ').chunked(2)) {
            if (b.startsWith("red") && a.toInt() > 12
                || b.startsWith("green") && a.toInt() > 13
                || b.startsWith("blue") && a.toInt() > 14) {
                continue@outer
            }
        }
        sum += id
    }
    println(sum)
}

private fun day2part2(lines: List<String>) {
    var sum = 0
    for (line in lines) {
        var red = 0
        var green = 0
        var blue = 0
        for ((a, b) in line.split(' ').chunked(2)) {
            when {
                b.startsWith("red") -> red = maxOf(red, a.toInt())
                b.startsWith("green") -> green = maxOf(green, a.toInt())
                b.startsWith("blue") -> blue = maxOf(blue, a.toInt())
            }
        }
        sum += red * green * blue
    }
    println(sum)
}