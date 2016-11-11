package wacc.visitors

import antlr.WACCParser._
import antlr.WACCParserBaseVisitor
import wacc.constructs.{CharLiteral, StringLiteral, _}

/**
  * It only visits expression literals
  * (excludes array literal which is implemented separately)
  */
object LiteralVisitor extends WACCParserBaseVisitor[Either[CompilationError, Literal]] {
  override def visitIntLiteral(ctx: IntLiteralContext): Either[CompilationError, IntegerLiteral]
    = try {
        Right(IntegerLiteral(ctx.getText.toInt))
      } catch {
        case e: NumberFormatException => Left(SyntaxError("integer literal not in range"))
      }

  override def visitBoolLiteral(ctx: BoolLiteralContext): Either[CompilationError, BoolLiteral]
    = Right(BoolLiteral(ctx.getText.toBoolean))

  override def visitCharLiteral(ctx: CharLiteralContext): Either[CompilationError, CharLiteral]
    = Right(CharLiteral(ctx.getText.charAt(1))) // 0 is a quote

  override def visitStringLiteral(ctx: StringLiteralContext): Either[CompilationError, StringLiteral]
    = Right(StringLiteral(ctx.getText))

  override def visitPairLiteral(ctx: PairLiteralContext): Either[CompilationError, PairLiteral]
    = Right(PairLiteral())
}
