package z3

import com.microsoft.z3.*

fun Context.mkInts(vararg names: String) = names.map { mkIntConst(it) }

context(Context)
operator fun <T : ArithSort> Expr<T>.times(other: Expr<T>): Expr<T> = mkMul(this, other)

context(Context)
operator fun Expr<IntSort>.times(other: Long): Expr<IntSort> = this * mkInt(other)

context(Context)
operator fun <T : ArithSort> Expr<T>.plus(other: Expr<T>): Expr<T> = mkAdd(this, other)

context(Context)
operator fun Expr<IntSort>.plus(other: Long): Expr<IntSort> = this + mkInt(other)

context(Context)
infix fun <T : Sort> Expr<T>.eq(other: Expr<T>): BoolExpr = mkEq(this, other)

operator fun Model.get(ex: IntExpr) = (getConstInterp(ex) as IntNum).int64