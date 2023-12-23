package day23

import getInput

fun main() {
    day23part1(getInput(23, true, 1))
    day23part1(getInput(23, false, 1))
    day23part2(getInput(23, true, 2))
    day23part2(getInput(23, false, 2))
}

private fun day23part1(lines: List<String>) {
    val startX = lines.first().indexOf('.')
    val endY = lines.lastIndex
    var bestEnd = Int.MIN_VALUE
    dfs(lines, Point(startX, 0), true) { point, dist ->
        if (point.y == endY) {
            bestEnd = maxOf(bestEnd, dist)
        }
    }
    println(bestEnd)
}

private fun day23part2(lines: List<String>) {
    val startX = lines.first().indexOf('.')
    val endY = lines.lastIndex
    var bestEnd = Int.MIN_VALUE
    dfs(lines, Point(startX, 0), false) { point, dist ->
        if (point.y == endY) {
            if (dist > bestEnd) {
                bestEnd = dist
                println("New best of $dist")
            }
        }
    }
    println(bestEnd)
}

private fun dfs(
    grid: List<String>,
    start: Point,
    followSlopes: Boolean,
    seen: MutableSet<Point> = hashSetOf(),
    onReached: (Point, Int) -> Unit
) {
    onReached(start, seen.size)
    seen.add(start)
    val toRemove = hashSetOf(start)
    var current = start
    var candidates = current.getUnvisited(grid, seen, followSlopes)
    while (candidates.size == 1) {
        current = candidates.single()
        onReached(current, seen.size)
        seen.add(current)
        toRemove.add(current)
        candidates = current.getUnvisited(grid, seen, followSlopes)
    }
    for (next in candidates) {
        dfs(grid, next, followSlopes, seen, onReached)
    }
    seen.removeAll(toRemove)
}

private data class Point(val x: Int, val y: Int) {
    fun getAdjacent(grid: List<String>, followSlopes: Boolean): Sequence<Point> =
        if (followSlopes) {
            when (grid[y][x]) {
                '>' -> sequenceOf(copy(x = x + 1))
                '<' -> sequenceOf(copy(x = x - 1))
                '^' -> sequenceOf(copy(y = y - 1))
                'v' -> sequenceOf(copy(y = y + 1))
                else -> adjacent()
            }
        } else {
            adjacent()
        }.filter { (x, y) ->
            y in grid.indices && x in grid[y].indices && grid[y][x] != '#'
        }

    private fun adjacent() = sequenceOf(
        copy(x = x + 1),
        copy(x = x - 1),
        copy(y = y - 1),
        copy(y = y + 1),
    )

    fun getUnvisited(grid: List<String>, seen: Set<Point>, followSlopes: Boolean) =
        getAdjacent(grid, followSlopes).filter { it !in seen }.toList()
}