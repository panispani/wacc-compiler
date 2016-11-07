package wacc.constructs

case class FunctionParam(variable: Variable)
case class Function(identifier: Identifier, params: List[FunctionParam], returns: BaseType) extends SemanticallyCheckable {

  override def semanticCheck(): Unit = System.out.println("overriden")
}
