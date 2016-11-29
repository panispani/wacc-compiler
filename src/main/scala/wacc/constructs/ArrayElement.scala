package wacc.constructs
import wacc.VariableReference

case class ArrayElement(array: VariableReference, index: Seq[Expression], elemtype: Type) extends Expression {
  override val vartype: Type = elemtype
}
