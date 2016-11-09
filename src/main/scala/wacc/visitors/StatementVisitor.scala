package wacc.visitors

import antlr.WACCParser._
import antlr.WACCParserBaseVisitor
import wacc.constructs._

object StatementVisitor extends WACCParserBaseVisitor[Either[CompilationError, Statement]] {

  override def visitSkip(ctx: SkipContext): Either[CompilationError, Skip] = {
    Right(Skip())
  }

  override def visitDeclare(ctx: DeclareContext): Either[CompilationError, Declare] = {
    val vartype = ctx.`type`().accept(TypeVisitor)
    val identifier = ctx.IDENT().toString

    for {
      rhs <- ctx.assignRhs().accept(AssignRhsVisitor).right
    } yield Declare(vartype, identifier, rhs)
  }

  override def visitExit(ctx: ExitContext): Either[CompilationError, ExitStatement] = {
    ctx.expression().accept(ExpressionVisitor).right.flatMap( e => e.vartype match {
      case Integer => Right(ExitStatement(e))
      case default => Left(SemanticError("Exit statement code should evaluate to value of type int"))
    })
  }
}

