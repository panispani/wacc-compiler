package wacc.constructs

import wacc.{SymbolTable, VariableReference}

case class Function(identifier: String, params: Seq[VariableReference],
                    vartype: Type, statements: Seq[Statement],
                    symbolTable: SymbolTable)