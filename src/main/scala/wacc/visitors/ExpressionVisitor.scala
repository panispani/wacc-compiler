package wacc.visitors

import antlr.WACCParser.IntLiteralContext
import antlr.WACCParserBaseVisitor
import wacc.constructs.{CompilationError, Expression, IntegerLiteral}

object ExpressionVisitor extends WACCParserBaseVisitor[Either[CompilationError, Expression]] {

  override def visitIntLiteral(ctx: IntLiteralContext): Either[CompilationError, Expression] = {
    Right(IntegerLiteral(ctx.getText.toInt))
  }
}
