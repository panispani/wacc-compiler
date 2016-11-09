package wacc.visitors

import antlr.WACCParser.{BoolLiteralContext, IntLiteralContext}
import antlr.WACCParserBaseVisitor
import wacc.constructs.{BoolLiteral, CompilationError, Expression, IntegerLiteral}

object ExpressionVisitor extends WACCParserBaseVisitor[Either[CompilationError, Expression]] {

  override def visitIntLiteral(ctx: IntLiteralContext): Either[CompilationError, Expression] = {
    Right(IntegerLiteral(ctx.getText.toInt))
  }

  override def visitBoolLiteral(ctx: BoolLiteralContext): Either[CompilationError, Expression] = {
    Right(BoolLiteral(ctx.getText.toBoolean))
  }
}
