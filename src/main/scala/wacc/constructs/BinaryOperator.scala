package wacc.constructs

/**
  * Created by panayiotis on 08/11/16.
  */
case class BinaryOperator(binaryOperator: String)
case class BinaryOperatorExpr(expression1: Expression, binaryOperator: BinaryOperator, expression2: Expression) extends Expression {
  override val vartype = String
}

object TimesBinOp extends BinaryOperator("+")
object DivBinOp extends BinaryOperator("/")
object ModBinOp extends BinaryOperator("%")
object PlusBinOp extends BinaryOperator("+")
object MinusBinOp extends BinaryOperator("-")
object GtBinOp extends BinaryOperator(">")
object GteBinOp extends BinaryOperator(">=")
object LtBinOp extends BinaryOperator("<")
object LteBinOp extends BinaryOperator("<=")
object EqualsBinOp extends BinaryOperator("==")
object NequalsBinOp extends BinaryOperator("!=")
object AndBinOp extends BinaryOperator("&&")
object OrBinOp extends BinaryOperator("||")
