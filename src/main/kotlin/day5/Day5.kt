package day5

import getInput
import split

fun main() {
    day5part1(getInput(5, true, 1))
    day5part1(getInput(5, false, 1))
    day5part2(getInput(5, true, 2))
    day5part2(getInput(5, false, 2))
}

private fun day5part1(lines: List<String>) {
    val (seeds, mappings) = parse(lines)
    var working = seeds
    for (mapping in mappings) {
        working = working.map { mapping.map(it) }
    }
    println(working.min())
}

private fun day5part2(lines: List<String>) {
    val (rawSeeds, mappings) = parse(lines)
    var working = rawSeeds.chunked(2).map { (a, b) -> a until a + b }
    for (mapping in mappings) {
        working = working.flatMap { mapping.map(it) }
    }
    println(working.minOf { it.first })
}

private fun parse(lines: List<String>): Pair<List<Long>, List<List<MappingSegment>>> {
    val numbers = lines[0].substringAfter(": ").split(' ').map(String::toLong)
    val result = mutableListOf<List<MappingSegment>>()
    for (block in lines.drop(2).split("")) {
        val list = mutableListOf<MappingSegment>()
        result.add(list)
        for ((a, b, c) in block.drop(1).map { it.split(' ').map(String::toLong) }) {
            list.add(MappingSegment(b, a, c))
        }
        list.sortBy { it.sourceStart }
    }
    return numbers to result
}

private fun List<MappingSegment>.map(input: Long): Long {
    if (input < first().sourceStart) {
        return input
    }
    val matching = lowestSegment(input)
    if (input in matching.sourceRange) {
        return matching.convert(input)
    }
    return input
}

private fun List<MappingSegment>.lowestSegment(input: Long): MappingSegment {
    val search = binarySearchBy(input) { it.sourceStart }
    val lowest = if (search < 0) {
        -search - 2
    } else {
        search
    }
    return this[lowest]
}

private fun List<MappingSegment>.map(input: LongRange): List<LongRange> {
    val firstStart = first().sourceStart
    if (input.first < firstStart && input.last < firstStart || input.first > last().sourceEnd && input.last > last().sourceEnd) {
        return listOf(input)
    }
    var working = input
    val result = mutableListOf<LongRange>()
    while (!working.isEmpty()) {
        if (working.first < firstStart) {
            result.add(working.first until firstStart)
            working = firstStart..working.last
        }
        val lowest = lowestSegment(working.first)
        if (working.first > lowest.sourceEnd) {
            result.add(working)
            break
        } else if (working.first in lowest.sourceRange && working.last in lowest.sourceRange) {
            result.add(lowest.convert(working.first)..lowest.convert(working.last))
            break
        } else if (working.first in lowest.sourceRange) {
            val int = working.intersect(lowest.sourceRange)
            result.add(lowest.convert(int.first)..lowest.convert(int.last))
            working = lowest.sourceEnd + 1..working.last
        } else {
            error("")
        }
    }
    return result.filterNot { it.isEmpty() }
}

private fun LongRange.intersect(other: LongRange) =
    maxOf(first, other.first)..minOf(last, other.last)

private data class MappingSegment(val sourceStart: Long, val destStart: Long, val length: Long) {
    val sourceEnd = sourceStart + length - 1
    val sourceRange = sourceStart..sourceEnd

    fun convert(source: Long) = destStart + (source - sourceStart)
}