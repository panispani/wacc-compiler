package wacc.visitors

import antlr.WACCParser.{BoolLiteralContext, CharLiteralContext, IntLiteralContext}
import antlr.WACCParserBaseVisitor
import wacc.constructs._

object ExpressionVisitor extends WACCParserBaseVisitor[Either[CompilationError, Expression]] {

  override def visitIntLiteral(ctx: IntLiteralContext): Either[CompilationError, Expression]
    = Right(IntegerLiteral(ctx.getText.toInt))

  override def visitBoolLiteral(ctx: BoolLiteralContext): Either[CompilationError, Expression]
    = Right(BoolLiteral(ctx.getText.toBoolean))

  override def visitCharLiteral(ctx: CharLiteralContext): Either[CompilationError, Expression]
    = Right(CharLiteral(ctx.getText.charAt(1))) // 0 is a quote
}
