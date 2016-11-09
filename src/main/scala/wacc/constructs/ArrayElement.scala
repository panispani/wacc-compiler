package wacc.constructs

case class ArrayElement(identifier: String, index: Seq[Expression]) extends Expression with AssignTarget {
  override val vartype = String
}
