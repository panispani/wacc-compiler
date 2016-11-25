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
case class ConditionalStatement(expression: Expression, trueStatements: ScopeStatement, falseStatements: ScopeStatement) extends Statement

// Identifier is a new reference here so it will always have the correct offset at parse-time
// Otherwise we would need to have it as a String and do additional lookup during code generation
case class DeclareStatement(vartype: Type, newReference: VariableReference, value: AssignValue) extends Statement

case class LoopStatement(condition: Expression, statements: Seq[Statement], symbolTable: SymbolTable) extends Statement


