package wacc.constructs

case class ArrayElement(reference: Typed, index: Seq[Expression], elemtype: Type) extends Expression {
  override val vartype = elemtype
}
