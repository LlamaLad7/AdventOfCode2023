package day18

import getInput
import kotlin.math.absoluteValue

fun main() {
    day18part1(getInput(18, true, 1))
    day18part1(getInput(18, false, 1))
    day18part2(getInput(18, true, 2))
    day18part2(getInput(18, false, 2))
}

private fun day18part1(lines: List<String>) {
    val dug = hashSetOf(Point(0, 0))
    var x = 0
    var y = 0
    for (line in lines) {
        val (d, num) = line.split(' ')
        val dir = Direction.valueOf(d)
        repeat(num.toInt()) {
            x += dir.dx
            y += dir.dy
            dug.add(Point(x, y))
        }
    }
    println(fill(x + 1, y + 1, dug))
}

private fun fill(startX: Int, startY: Int, walls: Set<Point>): Int {
    val seen = walls.toHashSet()
    seen.add(Point(startX, startY))
    val q = ArrayDeque(listOf(Point(startX, startY)))
    while (q.isNotEmpty()) {
        val (x, y) = q.removeFirst()
        for (dir in Direction.entries) {
            val new = Point(x + dir.dx, y + dir.dy)
            if (seen.add(new)) {
                q.addLast(new)
            }
        }
    }
    return seen.size
}

private fun day18part2(lines: List<String>) {
    val instructions = lines.parseInstructions()
    val points = mutableListOf(BigPoint(0, 0))
    var x = 0L
    var y = 0L
    for (instruction in instructions) {
        x = instruction.moveX(x)
        y = instruction.moveY(y)
        points.add(BigPoint(x, y))
    }
    val area = ((points + points[0]).zipWithNext().sumOf { (a, b) -> (a.y + b.y) * (a.x - b.x) } / 2).absoluteValue
    val perim = instructions.sumOf { it.length }
    val inside = area - perim / 2 - 1
    println(inside + perim + 2)
}

private enum class Direction(val dx: Int = 0, val dy: Int = 0) {
    U(dy = -1),
    D(dy = 1),
    L(dx = -1),
    R(dx = 1),
}

private data class Point(val x: Int, val y: Int)

private data class BigPoint(val x: Long, val y: Long)

private data class Instruction(val dir: Direction, val length: Long) {
    fun moveX(x: Long) = x + dir.dx * length
    fun moveY(y: Long) = y + dir.dy * length
}

private fun List<String>.parseInstructions() = map { it.split(' ') }.map { (_, _, hex) ->
    Instruction(Direction.valueOf("RDLU"[hex[7].digitToInt()].toString()), hex.drop(2).dropLast(2).toLong(16))
}