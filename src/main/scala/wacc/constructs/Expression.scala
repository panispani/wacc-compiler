package wacc.constructs

trait Expression extends AssignValue with AssignTarget

case class VariableReferenceExpression(vartype : Type) extends Expression

