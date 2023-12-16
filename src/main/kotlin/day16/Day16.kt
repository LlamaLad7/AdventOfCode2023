package day16

import getInput

fun main() {
    day16part1(getInput(16, true, 1))
    day16part1(getInput(16, false, 1))
    day16part2(getInput(16, true, 2))
    day16part2(getInput(16, false, 2))
}

private fun day16part1(grid: List<String>) {
    println(energizedTiles(grid, Beam(-1, 0, Direction.RIGHT)))
}

private fun day16part2(grid: List<String>) {
    val beams =
        grid.indices.flatMap { listOf(Beam(-1, it, Direction.RIGHT), Beam(grid[it].length, it, Direction.LEFT)) } +
                grid[0].indices.flatMap { listOf(Beam(it, -1, Direction.DOWN), Beam(it, grid.size, Direction.UP)) }
    println(beams.maxOf { energizedTiles(grid, it) })
}

private fun energizedTiles(grid: List<String>, start: Beam): Int {
    val beams = mutableListOf(start)
    val seenBeams = hashSetOf<Beam>()
    while (beams.isNotEmpty()) {
        val newBeams = beams.flatMap { it.move(grid) }
        beams.clear()
        for (beam in newBeams) {
            if (seenBeams.add(beam)) {
                beams.add(beam)
            }
        }
    }
    return seenBeams.mapTo(hashSetOf()) { (x, y, _) -> Pos(x, y) }.size
}

private enum class Direction {
    RIGHT {
        override fun handle(c: Char) = when (c) {
            '/' -> listOf(UP)
            '\\' -> listOf(DOWN)
            '|' -> listOf(UP, DOWN)
            '-' -> listOf(this)
            else -> super.handle(c)
        }
    },
    LEFT {
        override fun handle(c: Char) = when (c) {
            '/' -> listOf(DOWN)
            '\\' -> listOf(UP)
            '|' -> listOf(UP, DOWN)
            '-' -> listOf(this)
            else -> super.handle(c)
        }
    },
    UP {
        override fun handle(c: Char) = when (c) {
            '/' -> listOf(RIGHT)
            '\\' -> listOf(LEFT)
            '|' -> listOf(this)
            '-' -> listOf(LEFT, RIGHT)
            else -> super.handle(c)
        }
    },
    DOWN {
        override fun handle(c: Char) = when (c) {
            '/' -> listOf(LEFT)
            '\\' -> listOf(RIGHT)
            '|' -> listOf(this)
            '-' -> listOf(LEFT, RIGHT)
            else -> super.handle(c)
        }
    }
    ;

    open fun handle(c: Char) = when (c) {
        '.' -> listOf(this)
        else -> error("Unimplemented char $c for direction $this")
    }
}

private data class Beam(val x: Int, val y: Int, val dir: Direction) {
    fun move(grid: List<String>): List<Beam> {
        val newX = when (dir) {
            Direction.RIGHT -> x + 1
            Direction.LEFT -> x - 1
            else -> x
        }
        val newY = when (dir) {
            Direction.UP -> y - 1
            Direction.DOWN -> y + 1
            else -> y
        }
        if (newY !in grid.indices || newX !in grid[newY].indices) {
            return emptyList()
        }
        return dir.handle(grid[newY][newX]).map { Beam(newX, newY, it) }
    }
}

private data class Pos(val x: Int, val y: Int)