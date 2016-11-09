package wacc.constructs

case class ArrayElement(reference: Typed, index: Seq[Expression]) extends Expression with AssignTarget {
  override val vartype = String
}
