package wacc.constructs

/**
  * Created by panayiotis on 08/11/16.
  */
case class UnaryOperator(unaryOperator: String)
case class UnaryOperatorExpr(unaryOperator: UnaryOperator, expression: Expression) extends Expression {
  override val vartype = String
}
