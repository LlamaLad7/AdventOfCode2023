package day11

import getInput
import kotlin.math.abs

fun main() {
    day11part1(getInput(11, true, 1))
    day11part1(getInput(11, false, 1))
    day11part2(getInput(11, true, 2))
    day11part2(getInput(11, false, 2))
}

private fun day11part1(lines: List<String>) {
    val map = lines.expand()
    val coords = map.indices.flatMap { y -> map[y].indices.filter { x -> map[y][x] == '#' }.map { x -> y to x } }
    var sum = 0
    for (a in coords.indices) {
        val (y1, x1) = coords[a]
        for (b in a + 1 until coords.size) {
            val (y2, x2) = coords[b]
            sum += abs(y1 - y2) + abs(x1 - x2)
        }
    }
    println(sum)
}

private fun day11part2(lines: List<String>) {
    val (emptyRows, emptyColumns) = lines.findEmpties()
    val coords = lines.indices.flatMap { y -> lines[y].indices.filter { x -> lines[y][x] == '#' }.map { x -> y to x } }
    var sum = 0uL
    for (a in coords.indices) {
        val (y1, x1) = coords[a]
        for (b in a + 1 until coords.size) {
            val (y2, x2) = coords[b]
            val (ys, ym) = minOf(y1, y2) to maxOf(y1, y2)
            val (xs, xm) = minOf(x1, x2) to maxOf(x1, x2)
            sum += (ym - ys + emptyRows.count { it in ys..ym } * 999_999).toULong()
            sum += (xm - xs + emptyColumns.count { it in xs..xm } * 999_999).toULong()
        }
    }
    println(sum)
}

private fun List<String>.expand(): List<List<Char>> {
    val result = mapTo(mutableListOf()) { it.toMutableList() }
    val (emptyRows, emptyColumns) = findEmpties()
    for ((expanded, row) in emptyRows.withIndex()) {
        result.add(row + expanded, result[row + expanded].toMutableList())
    }
    for ((expanded, col) in emptyColumns.withIndex()) {
        for (row in result) {
            row.add(col + expanded, '.')
        }
    }
    return result
}

private fun List<String>.findEmpties(): Pair<List<Int>, List<Int>> {
    val emptyRows = indices.filterNot { '#' in this[it] }
    val emptyColumns = first().indices.filter { col -> this.all { it[col] == '.' } }
    return emptyRows to emptyColumns
}