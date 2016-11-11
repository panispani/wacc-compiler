package wacc.constructs

case class ArrayElement(reference: Typed, index: Seq[Expression], elemtype: Type) extends Expression with AssignTarget {
  override val vartype = elemtype
}
