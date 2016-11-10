package wacc.constructs

abstract class Expression extends AssignValue with AssignTarget

case class VariableReferenceExpression(vartype : Type) extends Expression

