package wacc.constructs

import wacc.{SymbolTable, VariableReference}

case class Function(name: String, params: Seq[VariableReference], varType: Type, statements: Seq[Statement], symbolTable: SymbolTable) {
  val identifier: String = Function.fullName(name, params.map(_.varType))
}

object Function {

  /**
    * Contains the name of the function and the types of arguments
    * so that we can tell the difference between overloads of a given
    * function. If package imports are implemented this might need to
    * be extended with a prefix for the corresponding package.
    * */
  def fullName(name: String, argumentTypes: Seq[Type]): String = {
    if (argumentTypes.nonEmpty)
      name + "_" + (argumentTypes map (t => t.toAssemblyLabel)).mkString("_")
    else
      name
  }
}