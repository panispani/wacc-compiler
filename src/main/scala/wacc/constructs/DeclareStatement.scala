package wacc.constructs

case class DeclareStatement(vartype: Type, identifier: String, value: AssignValue) extends Statement {
}
