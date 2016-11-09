package wacc.constructs

/**
  * Created by panayiotis on 08/11/16.
  */
case class BinaryOperator(binaryOperator: String)
case class BinaryOperatorExpr(expression1: Expression, binaryOperator: BinaryOperator, expression2: Expression) extends Expression {
  override val vartype = String
}

object timesBinOp extends BinaryOperator("+")
object divBinOp extends BinaryOperator("/")
object modBinOp extends BinaryOperator("%")
object plusBinOp extends BinaryOperator("+")
object minusBinOp extends BinaryOperator("-")
object gtBinOp extends BinaryOperator(">")
object gteBinOp extends BinaryOperator(">=")
object ltBinOp extends BinaryOperator("<")
object lteBinOp extends BinaryOperator("<=")
object equalsBinOp extends BinaryOperator("==")
object nequalsBinOp extends BinaryOperator("!=")
object andBinOp extends BinaryOperator("&&")
object orBinOp extends BinaryOperator("||")
