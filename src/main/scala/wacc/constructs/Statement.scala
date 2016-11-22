package wacc.constructs

import wacc.{SymbolTable, VariableReference}

trait Statement
case class ExitStatement(exitCode: Expression) extends Statement
case class ReturnStatement(returnValue: Expression) extends Statement
case class PrintStatement(expression: Expression) extends Statement
case class PrintLnStatement(expression: Expression) extends Statement
case class AssignStatement(lhs: AssignTarget, rhs: AssignValue) extends Statement
case class FreeStatement(expression: Expression) extends Statement
case class ScopeStatement(statements: Seq[Statement], symbolTable: SymbolTable) extends Statement
case class ReadStatement(target: AssignTarget) extends Statement
case class SkipStatement() extends Statement
case class ConditionalStatement(expression: Expression, trueStatements: Seq[Statement], falseStatements: Seq[Statement],
                                symbolTable: SymbolTable) extends Statement
case class DeclareStatement(vartype: Type, identifier: VariableReference, value: AssignValue) extends Statement
case class LoopStatement(condition: Expression, statements: Seq[Statement], symbolTable: SymbolTable) extends Statement


