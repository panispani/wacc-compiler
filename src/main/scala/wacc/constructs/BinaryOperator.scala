package wacc.constructs

/**
  * Created by panayiotis on 08/11/16.
  */
case class BinaryOperator(binaryOperator: String)
case class BinaryOperatorExpr(expression1: Expression, binaryOperator: BinaryOperator, expression2: Expression ) {

}
