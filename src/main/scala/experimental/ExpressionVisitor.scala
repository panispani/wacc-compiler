package experimental

import scala.collection.JavaConversions._

import antlr.WACCParser.{ArrayLiteralContext, BoolLiteralContext}
import antlr.{WACCParser, WACCParserBaseVisitor}

class ExpressionVisitor() extends WACCParserBaseVisitor[Expression] {

  override def visitIntLiteral(ctx: WACCParser.IntLiteralContext) = {
    IntegerLiteral(ctx.getText.toInt)
  }

  override def visitBoolLiteral(ctx: BoolLiteralContext): Expression
    = BoolLiteral(ctx.getText.toBoolean)

  override def visitArrayLiteral(ctx: ArrayLiteralContext): Expression
    = ArrayLiteral(ctx.expression().toList map this.visitExpression)
}
