package wacc.constructs

import wacc.{SymbolTable, VariableReference}

case class Function(identifier: String, params: Seq[VariableReference],
                    vartype: Type, statements: Seq[Statement],
                    symbolTable: SymbolTable) {

  def getTypedName: String = Function.appendFunctionTypes(identifier, params map (_.vartype))
}

object Function {
  def appendFunctionTypes(name: String, argumentTypes: Seq[Type]): String = {
    if (argumentTypes.nonEmpty)
      name + "_" + (argumentTypes map (t => t.toAssemblyLabel)).mkString("_")
    else
      name
  }
}