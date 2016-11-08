package wacc.constructs

case class FunctionCall(identifier: Identifier, args: Seq[Expression]) extends SemanticallyCheckable with AssignValue {

}
