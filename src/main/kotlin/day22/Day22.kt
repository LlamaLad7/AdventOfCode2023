package day22

import getInput

fun main() {
    day22part1(getInput(22, true, 1))
    day22part1(getInput(22, false, 1))
    day22part2(getInput(22, true, 2))
    day22part2(getInput(22, false, 2))
}

private fun day22part1(lines: List<String>) {
    val bricks = lines.parse().sortedBy { it.z.first }
    fall(bricks)
    val supporting = bricks.associateWith { brick ->
        bricks.filter {
            it.x overlaps brick.x && it.y overlaps brick.y && it.z.last == brick.z.first - 1
        }
    }
    val candidates = bricks.toHashSet()
    for (supportedBy in supporting.values) {
        if (supportedBy.size == 1) {
            candidates.remove(supportedBy.single())
        }
    }
    println(candidates.size)
}

private fun day22part2(lines: List<String>) {
    val bricks = lines.parse().sortedBy { it.z.first }
    fall(bricks)
    val fallen = bricks.sortedBy { it.z.first }
    var total = 0
    for ((i, toDisintegrate) in fallen.withIndex()) {
        if (i % 100 == 0) {
            println("Done $i/${fallen.size}")
        }
        val working = fallen.mapNotNull {
            if (it === toDisintegrate) {
                null
            } else {
                it.copy()
            }
        }
        fall(working) { total++ }
    }
    println(total)
}

private inline fun fall(bricks: List<Brick>, onFall: () -> Unit = {}) {
    for (falling in bricks) {
        val newZ = bricks.asSequence()
            .filter { it.x overlaps falling.x && it.y overlaps falling.y && it.z.last < falling.z.first }
            .maxOfOrNull { it.z.last + 1 } ?: 1
        if (newZ != falling.z.first) {
            onFall()
        }
        falling.moveTo(newZ)
    }
}

private fun List<String>.parse() = mapIndexed { i, line ->
    val (l, r) = line.split('~').map { it.split(',').map(String::toInt) }
    val (x1, y1, z1) = l
    val (x2, y2, z2) = r
    Brick(x1..x2, y1..y2, z1..z2, 'A' + i)
}
private data class Brick(val x: IntRange, val y: IntRange, private var _z: IntRange, val name: Char) {
    val z get() = _z

    fun moveTo(newZ: Int) {
        _z = newZ..(_z.last - _z.first + newZ)
    }
}

private infix fun IntRange.overlaps(other: IntRange) =
    first in other || last in other || other.first in this || other.last in this