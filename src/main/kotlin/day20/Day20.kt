package day20

import getInput
import lcm

fun main() {
    day20part1(getInput(20, true, 1))
    day20part1(getInput(20, false, 1))
//    day20part2(getInput(20, true, 2))
    day20part2(getInput(20, false, 2))
}

private fun day20part1(lines: List<String>) {
    val modules = lines.parse()
    var low = 0L
    var high = 0L
    repeat(1000) {
        val q = ArrayDeque(listOf(Pulse(false, Module.Button, "broadcaster")))
        while (q.isNotEmpty()) {
            val pulse = q.removeFirst()
            if (pulse.high) {
                high++
            } else {
                low++
            }
            val dest = modules[pulse.destination] ?: continue
            q.addAll(dest.process(pulse))
        }
    }
    println(low * high)
}

private fun day20part2(lines: List<String>) {
    val modules = lines.parse()
    // For graphviz:
//    for ((k, v) in modules) {
//        for (dest in v.destinations) {
//            val destName = modules[dest]?.toString(dest) ?: dest
//            println("${v.toString(k)} -> $destName;")
//        }
//    }
    // The graph is essentially 4 binary counters where each "hub" is a conjunction that outputs to the final conjunction
    val hubs = mutableListOf("pr", "qs", "jv", "jm")
    val counts = mutableMapOf<String, Int>()
    outer@ for (press in generateSequence(1) { it + 1 }) {
        val q = ArrayDeque(listOf(Pulse(false, Module.Button, "broadcaster")))
        while (q.isNotEmpty()) {
            val pulse = q.removeFirst()
            if (!pulse.high) {
                for (hub in hubs) {
                    if (pulse.source === modules[hub]) {
                        counts[hub] = press
                        hubs.remove(hub)
                        break
                    }
                }
                if (hubs.isEmpty()) {
                    break@outer
                }
            }
            val dest = modules[pulse.destination] ?: continue
            q.addAll(dest.process(pulse))
        }
    }
    println(counts.values.lcm())
}

private sealed class Module(val destinations: List<String>) {
    abstract fun process(pulse: Pulse): List<Pulse>
    abstract fun toString(name: String): String

    class Broadcaster(destinations: List<String>) : Module(destinations) {
        override fun process(pulse: Pulse) = destinations.map { Pulse(pulse.high, it) }
        override fun toString(name: String) = "broadcaster"
    }

    class FlipFlop(destinations: List<String>) : Module(destinations) {
        private var on = false

        override fun process(pulse: Pulse): List<Pulse> {
            if (pulse.high) {
                return emptyList()
            }
            on = !on
            return destinations.map { Pulse(on, it) }
        }

        override fun toString(name: String) = "\"!$name\""
    }

    class Conjunction(destinations: List<String>) : Module(destinations) {
        private val inputs = mutableMapOf<Module, Boolean>()

        override fun process(pulse: Pulse): List<Pulse> {
            inputs[pulse.source] = pulse.high
            val low = inputs.values.all { it }
            return destinations.map { Pulse(!low, it) }
        }

        fun connectInput(input: Module) {
            inputs[input] = false
        }

        override fun toString(name: String) = "\"&$name\""
    }

    data object Button : Module(emptyList()) {
        override fun process(pulse: Pulse) = error("Not implemented")
        override fun toString(name: String) = error("Not implemented")
    }

    protected fun Pulse(high: Boolean, destination: String) = Pulse(high, this, destination)
}

private data class Pulse(val high: Boolean, val source: Module, val destination: String)

private fun List<String>.parse() = associate {
    val (left, right) = it.split(" -> ")
    val first = left.first()
    val name = left.drop(1)
    val dests = right.split(", ")
    when (first) {
        'b' -> "broadcaster" to Module.Broadcaster(dests)
        '%' -> name to Module.FlipFlop(dests)
        '&' -> name to Module.Conjunction(dests)
        else -> error("Invalid start char $first")
    }
}.also { modules ->
    for (module in modules.values) {
        for (dest in module.destinations) {
            val destination = modules[dest] ?: continue
            if (destination is Module.Conjunction) {
                destination.connectInput(module)
            }
        }
    }
}