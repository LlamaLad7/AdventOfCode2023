package day17

import getInput
import java.util.*

fun main() {
    day17part1(getInput(17, true, 1))
    day17part1(getInput(17, false, 1))
    day17part2(getInput(17, true, 2))
    day17part2(getInput(17, false, 2))
}

private fun day17part1(lines: List<String>) {
    val grid = lines.map { it.map(Char::digitToInt) }
    val endY = grid.lastIndex
    val endX = grid[endY].lastIndex
    val q = PriorityQueue<ToVisit>()
    val best = hashMapOf<VisitedKey, Int>()
    q.add(ToVisit(0, VisitedKey(0, 0, Direction.L, 3)))

    while (q.isNotEmpty()) {
        val (dist, key) = q.remove()
        if (best[key].let { it != null && it <= dist }) {
            continue
        }
        best[key] = dist
        val (x, y, cameFrom, stepsLeft) = key
        if (x == endX && y == endY) {
            println(dist)
            return
        }
        for (dir in Direction.entries) {
            if (dir == cameFrom) continue
            val newY = y + dir.dy
            if (newY !in grid.indices) continue
            val newX = x + dir.dx
            if (newX !in grid[newY].indices) continue
            val newDist = dist + grid[newY][newX]
            val newStepsLeft = if (dir == cameFrom.opposite) stepsLeft - 1 else 2
            if (newStepsLeft < 0) continue
            q.add(ToVisit(newDist, VisitedKey(newX, newY, dir.opposite, newStepsLeft)))
        }
    }
}

private fun day17part2(lines: List<String>) {
    val grid = lines.map { it.map(Char::digitToInt) }
    val endY = grid.lastIndex
    val endX = grid[endY].lastIndex
    val q = PriorityQueue<ToVisit2>()
    val best = hashMapOf<VisitedKey2, Int>()
    q.add(ToVisit2(0, VisitedKey2(0, 0, null, 0)))

    while (q.isNotEmpty()) {
        val (dist, key) = q.remove()
        if (best[key].let { it != null && it <= dist }) {
            continue
        }
        best[key] = dist
        val (x, y, cameFrom, rolled) = key
        if (x == endX && y == endY && rolled >= 4) {
            println(dist)
            return
        }
        for (dir in Direction.entries) {
            if (dir == cameFrom || cameFrom != null && cameFrom.opposite != dir && rolled < 4) continue
            val newY = y + dir.dy
            if (newY !in grid.indices) continue
            val newX = x + dir.dx
            if (newX !in grid[newY].indices) continue
            val newDist = dist + grid[newY][newX]
            val newRolled = if (dir.opposite == cameFrom) rolled + 1 else 1
            if (newRolled > 10) continue
            q.add(ToVisit2(newDist, VisitedKey2(newX, newY, dir.opposite, newRolled)))
        }
    }
}

private data class ToVisit(val dist: Int, val key: VisitedKey) :
    Comparable<ToVisit> {
    override fun compareTo(other: ToVisit) = dist.compareTo(other.dist)
}

private data class ToVisit2(val dist: Int, val key: VisitedKey2) :
    Comparable<ToVisit2> {
    override fun compareTo(other: ToVisit2) = dist.compareTo(other.dist)
}

private enum class Direction(val dx: Int = 0, val dy: Int = 0) {
    U(dy = -1) {
        override val opposite get() = D
    },
    D(dy = 1) {
        override val opposite get() = U
    },
    L(dx = -1) {
        override val opposite get() = R
    },
    R(dx = 1) {
        override val opposite get() = L
    };

    abstract val opposite: Direction
}

private data class VisitedKey(val x: Int, val y: Int, val cameFrom: Direction, val stepsLeft: Int)

private data class VisitedKey2(val x: Int, val y: Int, val cameFrom: Direction?, val rolled: Int)