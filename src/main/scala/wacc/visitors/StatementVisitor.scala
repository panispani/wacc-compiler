package wacc.visitors

import antlr.WACCParser._
import antlr.WACCParserBaseVisitor
import wacc.constructs.{CompilationError, ExitStatement, Statement}

object StatementVisitor extends WACCParserBaseVisitor[Either[CompilationError, Statement]] {

  override def visitExit(ctx: ExitContext): Either[CompilationError, ExitStatement] = {
    ctx.expression().accept(ExpressionVisitor).right.flatMap( e => e.valueType match {
      case wacc.constructs.Integer => Right(ExitStatement(e))
      case default => Left(CompilationError("Semantic", ""))
    })
  }
}

