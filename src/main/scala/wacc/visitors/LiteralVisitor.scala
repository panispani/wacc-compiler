package wacc.visitors

import antlr.WACCParser._
import antlr.WACCParserBaseVisitor
import wacc.constructs.{CharLiteral, StringLiteral, _}

/**
  * Created by panayiotis on 08/11/16.
  */
object LiteralVisitor extends WACCParserBaseVisitor[Literal] {
  override def visitIntLiteral(ctx: IntLiteralContext): Either[CompilationError, Expression]
    = Right(IntegerLiteral(ctx.getText.toInt))

  override def visitBoolLiteral(ctx: BoolLiteralContext): Either[CompilationError, Expression]
    = Right(BoolLiteral(ctx.getText.toBoolean))

  override def visitCharLiteral(ctx: CharLiteralContext): Either[CompilationError, Expression]
    = Right(CharLiteral(ctx.getText.charAt(1))) // 0 is a quote

  override def visitStringLiteral(ctx: StringLiteralContext): Either[CompilationError, Expression]
    = Right(StringLiteral(ctx.getText))

  override def visitPairLiteral(ctx: PairLiteralContext): Either[CompilationError, Expression]
    = Right(PairLiteral(NullType, NullType, None))

}
