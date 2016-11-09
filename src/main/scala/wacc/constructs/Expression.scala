package wacc.constructs

abstract class Expression extends AssignValue

case class VariableReferenceExpression(vartype : Type) extends Expression

