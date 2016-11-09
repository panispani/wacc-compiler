package wacc.visitors

import antlr.WACCParser.IntLiteralContext
import antlr.WACCParserBaseVisitor
import wacc.constructs.{CompilationError, Expression, IntLiteral}

object ExpressionVisitor extends WACCParserBaseVisitor[Either[CompilationError, Expression]] {

  override def visitIntLiteral(ctx: IntLiteralContext): Either[CompilationError, Expression] = {
    Right(new IntLiteral(ctx.getText.toInt))
  }
}
