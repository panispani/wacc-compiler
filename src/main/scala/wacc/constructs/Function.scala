package wacc.constructs

import wacc.{SymbolTable, VariableReference}

case class Function(identifier: String, typed_name: String, params: Seq[VariableReference],
                    varType: Type, statements: Seq[Statement],
                    symbolTable: SymbolTable) {
}

object Function {
  def appendFunctionTypes(name: String, argumentTypes: Seq[Type]): String = {
    if (argumentTypes.nonEmpty)
      name + "_" + (argumentTypes map (t => t.toAssemblyLabel)).mkString("_")
    else
      name
  }
}