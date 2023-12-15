package day15

import getInput

fun main() {
    day15part1(getInput(15, true, 1))
    day15part1(getInput(15, false, 1))
    day15part2(getInput(15, true, 2))
    day15part2(getInput(15, false, 2))
}

private fun day15part1(lines: List<String>) {
    println(lines.single().split(',').sumOf { it.hash() })
}

private fun day15part2(lines: List<String>) {
    val boxes = Array(256) { mutableMapOf<String, Int>() }
    for (lens in lines.single().split(',')) {
        val (label, focalLength) = lens.split('=', '-')
        val box = boxes[label.hash()]
        if (focalLength.isEmpty()) {
            box.remove(label)
        } else {
            box[label] = focalLength.toInt()
        }
    }
    println(boxes.mapIndexed { i, box -> (i + 1) * box.toList().mapIndexed { slot, (_, f) -> (slot + 1) * f }.sum() }.sum())
}

private fun String.hash() =
    fold(0) { acc, c -> (acc + c.code) * 17 % 256 }