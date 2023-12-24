package day24

import getInput
import java.math.BigInteger

fun main() {
    day24part1(getInput(24, true, 1), true)
    day24part1(getInput(24, false, 1), false)
//    day24part2(getInput(24, true, 2))
//    day24part2(getInput(24, false, 2))
}

private fun day24part1(lines: List<String>, test: Boolean) {
    val range = if (test) {
        7.toRational()..27.toRational()
    } else {
        200000000000000.toRational()..400000000000000.toRational()
    }
    val stones = lines.parse { it.toBigInteger().toRational() }
    var total = 0
    for (i in stones.indices) {
        val (pos1, speed1) = stones[i]
        val (x1, y1) = pos1
        val (u1, v1) = speed1
        for (j in i + 1 until stones.size) {
            val (pos2, speed2) = stones[j]
            val (x2, y2) = pos2
            val (u2, v2) = speed2
            val mInv = Matrix2(
                u1, -u2,
                v1, -v2
            ).inverse() ?: continue
            val v = Vector2(
                x2 - x1,
                y2 - y1
            )
            val times = mInv dot v
            if (times.x.isNegative() || times.y.isNegative()) {
                continue
            }
            val t = times.x
            val x = x1 + t * u1
            val y = y1 + t * v1
            if (x in range && y in range) {
                total++
            }
        }
    }
    println(total)
}

private fun day24part2(lines: List<String>) {
    // See day24p2.py
}

private inline fun <T> List<String>.parse(transform: (String) -> T) = map { line ->
    val (l, r) = line.split(" @ ").map { vec ->
        vec.split(", ").map(transform)
    }
    val (x, y, z) = l
    val (u, v, w) = r
    Triple(x, y, z) to Triple(u, v, w)
}

private class Rational(numerator: BigInteger, denominator: BigInteger) : Comparable<Rational> {
    val numerator: BigInteger
    val denominator: BigInteger

    init {
        require(denominator != BigInteger.ZERO) { "Cannot divide by 0" }
        var num = numerator
        var denom = denominator
        if (denom < BigInteger.ZERO) {
            num = -num
            denom = -denom
        }
        val gcd = num.gcd(denom)
        this.numerator = num / gcd
        this.denominator = denom / gcd
    }

    fun isZero() = numerator == BigInteger.ZERO

    fun isNegative() = numerator < BigInteger.ZERO

    operator fun unaryMinus() = Rational(-numerator, denominator)

    fun reciprocal() = Rational(denominator, numerator)

    operator fun times(int: BigInteger) = Rational(numerator * int, denominator)

    operator fun times(other: Rational) = Rational(numerator * other.numerator, denominator * other.denominator)

    operator fun div(other: Rational) = this * other.reciprocal()

    operator fun plus(other: Rational): Rational = Rational(
        numerator * other.denominator + other.numerator * denominator,
        denominator * other.denominator
    )

    operator fun minus(other: Rational) = this + -other

    override operator fun compareTo(other: Rational) = (this - other).numerator.compareTo(BigInteger.ZERO)

    override fun toString() = "$numerator/$denominator"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Rational

        if (numerator != other.numerator) return false
        if (denominator != other.denominator) return false

        return true
    }

    override fun hashCode(): Int {
        var result = numerator.hashCode()
        result = 31 * result + denominator.hashCode()
        return result
    }
}

private infix fun Number.over(other: Number) = Rational(this.toLong().toBigInteger(), other.toLong().toBigInteger())

private fun BigInteger.toRational() = Rational(this, BigInteger.ONE)

private fun Number.toRational() = this over 1

private data class Matrix2(
    val m00: Rational, val m01: Rational,
    val m10: Rational, val m11: Rational,
) {
    infix fun dot(vec: Vector2) = Vector2(
        m00 * vec.x + m01 * vec.y,
        m10 * vec.x + m11 * vec.y
    )

    operator fun times(scalar: Rational) = Matrix2(
        m00 * scalar, m01 * scalar,
        m10 * scalar, m11 * scalar
    )

    fun det() = m00 * m11 - m01 * m10

    fun inverse(): Matrix2? {
        val det = det().takeUnless { it.isZero() } ?: return null
        return Matrix2(
            m11, -m01,
            -m10, m00
        ) * det.reciprocal()
    }
}

private data class Vector2(val x: Rational, val y: Rational)