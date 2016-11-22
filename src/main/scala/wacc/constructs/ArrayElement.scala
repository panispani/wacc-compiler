package wacc.constructs

case class ArrayElement(identifier: String, index: Seq[Expression], elemtype: Type) extends Expression {
  override val vartype = elemtype
}
