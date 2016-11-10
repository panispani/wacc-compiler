package wacc.constructs

/**
  * Created by panayiotis on 08/11/16.
  */
case class BinaryOperatorExpr(expression1: Expression, binaryOperator: BinaryOperator, expression2: Expression) extends Expression {
  override val vartype = binaryOperator match {
    case TimesBinOp
       | DivBinOp
       | ModBinOp
       | PlusBinOp
       | MinusBinOp
      => Integer

    case GtBinOp
       | GteBinOp
       | LtBinOp
       | LteBinOp
       | EqualsBinOp
       | NequalsBinOp
       | AndBinOp
       | OrBinOp
      => Boolean
  }
}

case class BinaryOperator(binaryOperator: String)

/* Integers */
object TimesBinOp extends BinaryOperator("*")
object DivBinOp extends BinaryOperator("/")
object ModBinOp extends BinaryOperator("%")
object PlusBinOp extends BinaryOperator("+")
object MinusBinOp extends BinaryOperator("-")

/* Booleans */
object GtBinOp extends BinaryOperator(">")
object GteBinOp extends BinaryOperator(">=")
object LtBinOp extends BinaryOperator("<")
object LteBinOp extends BinaryOperator("<=")
object EqualsBinOp extends BinaryOperator("==")
object NequalsBinOp extends BinaryOperator("!=")
object AndBinOp extends BinaryOperator("&&")
object OrBinOp extends BinaryOperator("||")
