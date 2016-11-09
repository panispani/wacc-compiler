package wacc.constructs

trait Statement
case class ExitStatement(exitCode: Expression) extends Statement
case class ReturnStatement(returnValue: Expression) extends Statement