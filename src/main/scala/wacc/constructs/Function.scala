package wacc.constructs

case class Param(variable: Variable)
case class Function(identifier: Identifier, params: Seq[Param], returns: Type, stmt: Statement) extends SemanticallyCheckable {
//not base type

}
