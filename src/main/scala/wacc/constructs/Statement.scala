package wacc.constructs

trait Statement
case class ExitStatement(exitCode: Expression) extends Statement
case class ReturnStatement(returnValue: Expression) extends Statement
case class PrintStatement(expression: Expression) extends Statement
case class PrintLnStatement(expression: Expression) extends Statement
case class AssignStatement(lhs: AssignTarget, rhs: AssignValue) extends Statement
case class ScopeStatement(sequence: Seq[Statement]) extends Statement
