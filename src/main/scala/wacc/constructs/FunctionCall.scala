package wacc.constructs

case class FunctionCall(identifier: String, args: Seq[Expression], returnType: Type) extends AssignValue {
  override val vartype = returnType
}
