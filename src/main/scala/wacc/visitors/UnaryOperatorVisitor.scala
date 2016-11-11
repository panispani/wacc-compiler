package wacc.visitors

import antlr.WACCParser.UnaryOperatorContext
import antlr.WACCParserBaseVisitor
import wacc.constructs._

object UnaryOperatorVisitor extends WACCParserBaseVisitor[UnaryOperator] {
  override def visitUnaryOperator(ctx: UnaryOperatorContext): UnaryOperator = {
    UnaryOperator(ctx.getText)
  }
}