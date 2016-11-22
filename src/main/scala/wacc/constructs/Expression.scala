package wacc.constructs

trait Expression extends AssignValue with AssignTarget

case class VariableReferenceExpression(identifier: String, vartype: Type) extends Expression