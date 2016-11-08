package wacc.constructs

case class FunctionParam(variable: Variable)
case class Function(identifier: Identifier, params: Seq[FunctionParam], returns: BaseType, stmt: Statement) extends SemanticallyCheckable {

}

