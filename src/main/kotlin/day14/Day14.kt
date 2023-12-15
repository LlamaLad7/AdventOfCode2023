package day14

import getInput

fun main() {
    day14part1(getInput(14, true, 1))
    day14part1(getInput(14, false, 1))
    day14part2(getInput(14, true, 2))
    day14part2(getInput(14, false, 2))
}

private fun day14part1(lines: List<String>) {
    val grid = lines.map { it.toCharArray() }
    grid.roll(Direction.NORTH)
    println(grid.load())
}

private fun day14part2(lines: List<String>) {
    val states = hashMapOf(lines.map { it.toList() } to 0)
    val current = lines.map { it.toCharArray() }
    var done = 0
    while (true) {
        val remaining = 1000000000 - done
        current.cycle()
        done++
        val state = current.map { it.toList() }
        if (state !in states) {
            states[state] = done
        } else {
            val cycleLength = done - states.getValue(state)
            done += remaining / cycleLength * cycleLength
            break
        }
    }
    repeat(1000000000 - done) {
        current.cycle()
    }
    println(current.load())
}

private fun List<CharArray>.load(): Int {
    var total = 0
    for ((y, row) in withIndex()) {
        total += row.count { it == 'O' } * (size - y)
    }
    return total
}

private enum class Direction(val dy: Int, val dx: Int) {
    NORTH(-1, 0),
    WEST(0, -1),
    SOUTH(1, 0),
    EAST(0, 1),
}

private fun List<CharArray>.roll(direction: Direction) {
    do {
        var moved = false
        for ((y, row) in withIndex()) {
            val newY = y + direction.dy
            if (newY !in indices) continue
            for ((x, c) in row.withIndex()) {
                val newX = x + direction.dx
                if (newX !in row.indices) continue
                if (c == 'O' && this[newY][newX] == '.') {
                    row[x] = '.'
                    this[newY][newX] = 'O'
                    moved = true
                }
            }
        }
    } while (moved)
}

private fun List<CharArray>.cycle() {
    for (dir in Direction.entries) {
        roll(dir)
    }
}