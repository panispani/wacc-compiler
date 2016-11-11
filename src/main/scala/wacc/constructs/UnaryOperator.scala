package wacc.constructs

/**
  * Created by panayiotis on 08/11/16.
  */
case class UnaryOperator(unaryOperator: String)
case class UnaryOperatorExpr(unaryOperator: UnaryOperator, expression: Expression) extends Expression {
  override val vartype = unaryOperator match {
    case NotOp | MinusOp => expression.vartype
    case LenOp | OrdOp => Integer
    case ChrOp => Character
  }
}

object NotOp extends UnaryOperator("!")
object MinusOp extends UnaryOperator("-")
object LenOp extends UnaryOperator("len")
object OrdOp extends UnaryOperator("ord")
object ChrOp extends UnaryOperator("chr")