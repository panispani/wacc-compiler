package wacc.constructs

case class FunctionParam(variable: Variable)
case class Function(identifier: Identifier, params: Seq[FunctionParam], returns: Type, stmt: Statement) extends SemanticallyCheckable {
//not base type

  
}
