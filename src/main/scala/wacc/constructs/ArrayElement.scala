package wacc.constructs

case class ArrayElement(identifier: String, index: Seq[Expression]) extends AssignValue with AssignTarget {
  override val vartype = String
}
