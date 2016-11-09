package wacc.constructs

/**
  * Created by panayiotis on 09/11/16.
  */
case class ExprIdentifier(Identifier: String) extends Expression {
  override val vartype = String
}
