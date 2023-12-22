package day21

import getInput
import kotlin.collections.set

fun main() {
    day21part1(getInput(21, true, 1), true)
    day21part1(getInput(21, false, 1), false)
    day21part2(getInput(21, false, 2), true)
    day21part2(getInput(21, false, 2), false)
}

private fun day21part1(lines: List<String>, test: Boolean) {
    val (start, grid) = lines.parse()
    println(bfs1(start, grid, if (test) 6 else 64))
}

private fun bfs1(start: Point, grid: Grid, moves: Int): Int {
    val q = ArrayDeque(listOf(start to moves))
    val seen = hashSetOf<Point>()
    var total = 0
    while (q.isNotEmpty()) {
        val (point, movesLeft) = q.removeFirst()
        if (movesLeft < 0 || !seen.add(point)) {
            continue
        }
        if (movesLeft % 2 == 0) {
            total++
        }
        for (next in point.getAdjacent1(grid)) {
            q.addLast(next to movesLeft - 1)
        }
    }
    return total
}

private fun day21part2(lines: List<String>, test: Boolean) {
    val moves = if (test) 1001 else 26501365
    val (start, grid) = lines.parse()
    val tiledGrid = grid.map { it + it }.toTypedArray().let { it + it }
    val width = tiledGrid[0].size
    val height = tiledGrid.size
    val topLeft = Point(0, 0)
    val topRight = Point(width - 1, 0)
    val bottomLeft = Point(0, height - 1)
    val bottomRight = Point(width - 1, height - 1)
    val leftOfStart = start.copy(x = 0)
    val rightOfStart = start.copy(x = width - 1)
    val aboveStart = start.copy(y = 0)
    val belowStart = start.copy(y = height - 1)
    val dists =
        listOf(topLeft, bottomRight, rightOfStart, belowStart)
            .associateWith { DistanceInfo(bfs3(it, tiledGrid, needsOdd = true)) } +
                listOf(topRight, bottomLeft, leftOfStart, aboveStart)
                    .associateWith { DistanceInfo(bfs3(it, tiledGrid, needsOdd = false)) }

    fun verticalLine(stepsLeft: Int, targetPoint: Point): Long {
        if (stepsLeft < 0) {
            return 0L
        }
        val dist = dists.getValue(targetPoint)
        val furthest = dist.max
        val numOfFull = ((stepsLeft - furthest) / height).coerceAtLeast(0)
        var total = numOfFull.toLong() * dist.size
        var workingSteps = stepsLeft - height * numOfFull
        while (workingSteps >= 0) {
            total += dist.reachable(workingSteps)
            workingSteps -= height
        }
        return total
    }

    fun horizontalLine(initialSteps: Int, targetPoint: Point, bottomPoint: Point, topPoint: Point): Long {
        val stepsLeft = moves - (initialSteps + 1)
        val dist = dists.getValue(targetPoint)
        var total = 0L
        var workingSteps = stepsLeft
        val stepsToTop = start.y + 1
        val stepsToBottom = belowStart.y - start.y + 1
        while (workingSteps >= 0) {
            total += dist.reachable(workingSteps)
            total += verticalLine(workingSteps - stepsToTop, bottomPoint)
            total += verticalLine(workingSteps - stepsToBottom, topPoint)
            workingSteps -= width
        }
        return total
    }


    val leftLineCount = horizontalLine(start.x, rightOfStart, bottomRight, topRight)
    val rightLineCount = horizontalLine(rightOfStart.x - start.x, leftOfStart, bottomLeft, topLeft)
    val upLineCount = verticalLine(moves - (start.y + 1), belowStart)
    val downLineCount = verticalLine(moves - (belowStart.y - start.y + 1), aboveStart)

    val startCount = dists.values.first().size
    if (test) {
        val reference = bfs2(start, grid, moves)
        println("Reference for $moves: ${reference.size}")
        val leftLineReference = reference.count { (x, _) -> x < 0 }
        val rightLineReference = reference.count { (x, _) -> x >= width }
        val upLineReference = reference.count { (x, y) -> x in 0 until width && y < 0 }
        val downLineReference = reference.count { (x, y) -> x in 0 until width && y >= height }
        println("$leftLineReference - $leftLineCount")
        println("$rightLineReference - $rightLineCount")
        println("$upLineReference - $upLineCount")
        println("$downLineReference - $downLineCount")
    }
    println(startCount + leftLineCount + rightLineCount + upLineCount + downLineCount)
}

private fun bfs2(start: Point, grid: Grid, moves: Int): Set<Point> {
    val q = ArrayDeque(listOf(start to moves))
    val seen = hashSetOf<Point>()
    val reachable = hashSetOf<Point>()
    while (q.isNotEmpty()) {
        val (point, movesLeft) = q.removeFirst()
        if (movesLeft < 0 || !seen.add(point)) {
            continue
        }
        if (movesLeft % 2 == 0) {
            reachable.add(point)
        }
        for (next in point.getAdjacent2(grid)) {
            q.addLast(next to movesLeft - 1)
        }
    }
    return reachable
}

private fun bfs3(start: Point, grid: Grid, needsOdd: Boolean): Map<Point, Int> {
    val q = ArrayDeque(listOf(start to 0))
    val seen = hashSetOf<Point>()
    val result = hashMapOf<Point, Int>()
    val modResult = if (needsOdd) 1 else 0
    while (q.isNotEmpty()) {
        val (point, movesSoFar) = q.removeFirst()
        if (!seen.add(point)) {
            continue
        }
        if (movesSoFar % 2 == modResult) {
            result[point] = movesSoFar
        }
        for (next in point.getAdjacent1(grid)) {
            q.addLast(next to movesSoFar + 1)
        }
    }
    return result
}

private typealias Grid = Array<BooleanArray>

private fun List<String>.parse(): Pair<Point, Grid> {
    val startY = indexOfFirst { 'S' in it }
    val startX = this[startY].indexOf('S')
    return Point(startX, startY) to map { line ->
        line.map { it != '#' }.toBooleanArray()
    }.toTypedArray()
}

private data class Point(val x: Int, val y: Int) {
    fun getAdjacent1(grid: Grid) = sequence {
        yield(copy(x = x - 1))
        yield(copy(x = x + 1))
        yield(copy(y = y - 1))
        yield(copy(y = y + 1))
    }.filter { (x, y) -> y in grid.indices && x in grid[y].indices && grid[y][x] }

    fun getAdjacent2(grid: Grid): Sequence<Point> {
        val height = grid.size
        val width = grid[0].size
        return sequence {
            yield(copy(x = x - 1))
            yield(copy(x = x + 1))
            yield(copy(y = y - 1))
            yield(copy(y = y + 1))
        }.filter { (x, y) -> grid[y.mod(height)][x.mod(width)] }
    }
}

private class DistanceInfo private constructor(private val dists: List<Int>) {
    val max = dists.last()
    val size = dists.size

    constructor(dists: Map<Point, Int>) : this(dists.values.sorted())

    fun reachable(stepsLeft: Int): Int {
        if (stepsLeft >= max) {
            return size
        }
        var result = 0
        for (dist in dists) {
            if (dist > stepsLeft) {
                break
            }
            result++
        }
        return result
    }
}