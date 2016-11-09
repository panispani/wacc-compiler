package wacc.visitors

import antlr.WACCParser._
import antlr.WACCParserBaseVisitor
import wacc.constructs.{CharLiteral, StringLiteral, _}

/**
  * Created by panayiotis on 08/11/16.
  */
object LiteralVisitor extends WACCParserBaseVisitor[Literal] {
  override def visitIntLiteral(ctx: IntLiteralContext): IntegerLiteral
    = IntegerLiteral(ctx.getText.toInt)

  override def visitBoolLiteral(ctx: BoolLiteralContext): BoolLiteral
    = BoolLiteral(ctx.getText.toBoolean)

  override def visitCharLiteral(ctx: CharLiteralContext): CharLiteral
    = CharLiteral(ctx.getText.charAt(1)) // 0 is a quote

  override def visitStringLiteral(ctx: StringLiteralContext): StringLiteral
    = StringLiteral(ctx.getText)

  override def visitPairLiteral(ctx: PairLiteralContext): PairLiteral
    = PairLiteral(NullType, NullType, None)

}
