package day25

import getInput
import guru.nidi.graphviz.attribute.Named
import guru.nidi.graphviz.engine.Graphviz
import guru.nidi.graphviz.engine.GraphvizCmdLineEngine
import guru.nidi.graphviz.graph
import guru.nidi.graphviz.model.LinkSource
import guru.nidi.graphviz.model.LinkTarget

fun main() {
    Graphviz.useEngine(GraphvizCmdLineEngine())
    day25part1(getInput(25, false, 1))
}

private fun day25part1(lines: List<String>) {
    val graph = graph {
        for (line in lines) {
            val (node, rest) = line.split(": ")
            val connections = rest.split(' ')
            connections.forEach {
                node - it
                it - node // comment for graphviz
            }
        }
    }
//    graph.toGraphviz().engine(Engine.DOT).render(Format.SVG).toFile(File("day25.svg"))
    val nodeIndex = graph.rootNodes().associateBy { it.name }
    val toRemove = listOf(
        "thk" to "cms",
        "dht" to "xmv",
        "xkf" to "rcn"
    )
    for ((a, b) in toRemove) {
        nodeIndex.getValue(a).links().removeAll { it.to().name == b }
        nodeIndex.getValue(b).links().removeAll { it.to().name == a }
    }
    println("Starting")
    val left = discover(nodeIndex.getValue("thk"))
    val right = discover(nodeIndex.getValue("cms"))
    println(left * right)
}

@Suppress("UNCHECKED_CAST")
fun <T> discover(node: T, seen: MutableSet<String> = hashSetOf()): Int
        where T : LinkSource, T : LinkTarget {
    if (!seen.add(node.name)) {
        return 0
    }
    for (next in node.links().asSequence().map { it.to().asLinkSource() }) {
        discover(next as T, seen)
    }
    return seen.size
}

private val Named.name get() = name().value()