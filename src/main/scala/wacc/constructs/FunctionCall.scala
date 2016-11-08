package wacc.constructs

case class FunctionCall(identifier: String, args: Seq[Expression]) extends SemanticallyCheckable with AssignValue {
  override val vartype = String
}
