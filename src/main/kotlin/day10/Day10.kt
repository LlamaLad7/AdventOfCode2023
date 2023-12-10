package day10

import getInput

fun main() {
    day10part1(getInput(10, true, 1))
    day10part1(getInput(10, false, 1))
    day10part2(getInput(10, true, 2))
    day10part2(getInput(10, false, 2))
}

private typealias Grid = List<CharArray>

private fun day10part1(lines: List<String>) {
    val grid = lines.map { it.toCharArray() }
    var farthest = Int.MIN_VALUE
    val start = grid.getStartPos()
    val seen = hashSetOf(start)
    val q = ArrayDeque(listOf(0 to start))
    while (q.isNotEmpty()) {
        val (dist, pos) = q.removeFirst()
        farthest = maxOf(farthest, dist)
        val (y, x) = pos
        for (new in grid.getConnected(y, x)) {
            if (seen.add(new)) {
                q.addLast(dist + 1 to new)
            }
        }
    }
    println(farthest)
}

private fun day10part2(lines: List<String>) {
    val grid = lines.map { it.toCharArray() }
    var farthest = Int.MIN_VALUE
    val start = grid.getStartPos()
    val seen = hashSetOf(start)
    val q = ArrayDeque(listOf(0 to start))
    while (q.isNotEmpty()) {
        val (dist, pos) = q.removeFirst()
        farthest = maxOf(farthest, dist)
        val (y, x) = pos
        for (new in grid.getConnected(y, x)) {
            if (seen.add(new)) {
                q.addLast(dist + 1 to new)
            }
        }
    }
    for (y in grid.indices) {
        for (x in grid[y].indices) {
            if (y to x !in seen) {
                print('.')
            } else {
                print(pathChars[grid[y][x]])
            }
        }
        println()
    }
    // I then flood filled and counted them lol (303)
}

private val pathChars = mapOf(
    '|' to '│',
    '-' to '─',
    'L' to '└',
    'J' to '┘',
    '7' to '┐',
    'F' to '┌',
)

private fun Grid.getStartPos(): Pair<Int, Int> {
    val startY = indexOfFirst { 'S' in it }
    val startX = this[startY].indexOf('S')
    val start = startY to startX
    for (c in "|-LJ7F") {
        this[startY][startX] = c
        if (getConnected(startY, startX).count { (y, x) -> start in getConnected(y, x) } == 2) {
            break
        }
    }
    return start
}

private fun Grid.getConnected(y: Int, x: Int) = when (val c = this[y][x]) {
    '|' -> setOf(y - 1 to x, y + 1 to x)
    '-' -> setOf(y to x - 1, y to x + 1)
    'L' -> setOf(y - 1 to x, y to x + 1)
    'J' -> setOf(y - 1 to x, y to x - 1)
    '7' -> setOf(y + 1 to x, y to x - 1)
    'F' -> setOf(y + 1 to x, y to x + 1)
    '.' -> emptySet()
    else -> error("Invalid pipe char $c")
}.filter { (y, x) -> y in indices && x in this[0].indices }