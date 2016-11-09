package wacc.constructs

case class FunctionCall(identifier: String, args: Seq[Expression]) extends AssignValue {
  override val vartype = String
}
