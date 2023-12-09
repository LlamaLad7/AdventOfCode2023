package day8

import getInput

fun main() {
    day8part1(getInput(8, true, 1))
    day8part1(getInput(8, false, 1))
    day8part2(getInput(8, true, 2))
    day8part2(getInput(8, false, 2))
}

private fun day8part1(lines: List<String>) {
    val instructions = lines.first()
    val graph = parseGraph(lines)
    println(stepsToEnd(graph, instructions, "AAA") { this == "ZZZ" })
}

private fun day8part2(lines: List<String>) {
    val instructions = lines.first()
    val graph = parseGraph(lines)
    val positions = graph.keys.filter { it.endsWith('A') }
    val steps = positions.map {
        stepsToEnd(graph, instructions, it) { endsWith('Z') }
    }
    println(steps.map { it.toBigInteger() }.reduce { a, b -> a * b / a.gcd(b) })
}

private inline fun stepsToEnd(
    graph: Map<String, Pair<String, String>>,
    instructions: String,
    start: String,
    isTheEnd: String.() -> Boolean
): Int {
    var pos = start
    var steps = 0
    for (instruction in generateSequence(0) { it + 1 }.map { instructions[it % instructions.length] }) {
        pos = when (instruction) {
            'L' -> graph.getValue(pos).first
            'R' -> graph.getValue(pos).second
            else -> error("Invalid instruction $instruction")
        }
        steps++
        if (pos.isTheEnd()) break
    }
    return steps
}

private fun parseGraph(lines: List<String>): Map<String, Pair<String, String>> = buildMap {
    for (line in lines.drop(2)) {
        put(line.take(3), line.substring(7..9) to line.substring(12..14))
    }
}