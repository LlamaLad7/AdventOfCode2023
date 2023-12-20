import java.io.File

fun getInput(day: Int, test: Boolean, part: Int): List<String> {
    fun withSuffix(suffix: String) =
        File(".", "src/main/resources/${if (test) "test_inputs" else "inputs"}/day$day$suffix.txt")
            .takeIf { it.exists() }
            ?.readText() ?: ""
    val suffix = if (part == 2) "_part2" else ""
    val text = withSuffix(suffix).ifBlank { withSuffix("") }
    return text.lines()
}

fun <T> List<T>.split(separator: T): List<List<T>> {
    val result = mutableListOf(mutableListOf<T>())
    for (item in this) {
        if (item == separator) {
            result.add(mutableListOf())
        } else {
            result.last().add(item)
        }
    }
    return result
}

fun Iterable<Number>.lcm() = map { it.toLong().toBigInteger() }.reduce { a, b -> a * b / a.gcd(b) }