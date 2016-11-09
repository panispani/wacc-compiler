package wacc.visitors

import antlr.WACCParser.IntLiteralContext
import antlr.WACCParserBaseVisitor
import wacc.constructs.{Expression, IntegerLiteral}

object ExpressionVisitor extends WACCParserBaseVisitor[Expression] {

  override def visitIntLiteral(ctx: IntLiteralContext): IntegerLiteral = {
    IntegerLiteral(ctx.getText.toInt)
  }

}
