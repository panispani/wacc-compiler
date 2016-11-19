package wacc.constructs

trait Expression extends AssignValue with AssignTarget

case class VariableReferenceExpression(name: String, vartype : Type, offset: Int) extends Expression
