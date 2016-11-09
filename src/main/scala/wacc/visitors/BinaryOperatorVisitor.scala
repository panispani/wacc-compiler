package wacc.visitors

import antlr.WACCParser.BinaryOperatorContext
import antlr.WACCParserBaseVisitor
import wacc.constructs._


/**
  * Created by panayiotis on 08/11/16.
  */
object BinaryOperatorVisitor extends WACCParserBaseVisitor[BinaryOperator] {
  override def visitBinaryOperator(ctx: BinaryOperatorContext): BinaryOperator = {
    BinaryOperator(ctx.getText)
  }
}
