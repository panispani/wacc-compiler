package wacc.visitors

import antlr.WACCParser.ExpressionContext
import antlr.WACCParserBaseVisitor
import wacc.constructs.Expression
import wacc.constructs.ExprIdentifier

object ExpressionVisitor extends WACCParserBaseVisitor[Expression] {
  override def visitExpression(ctx: ExpressionContext): Expression = {
    if (Option(ctx.arrayElement()).isDefined) {
      ctx.arrayElement().accept(ArrayElementVisitor).get
    } else if (Option(ctx.binaryOperator()).isDefined) {
      ctx.binaryOperator().accept(BinaryOperatorVisitor)
    } else if (Option(ctx.literal()).isDefined) {
      ctx.literal().accept(LiteralVisitor)
    } else if (Option(ctx.IDENT()).isDefined) {
      ExprIdentifier(ctx.IDENT().accept(IdentifierVisitor))
    } else if (Option(ctx.unaryOperator()).isDefined) {
      ctx.unaryOperator().accept(UnaryOperatorVisitor)
    } else {
      ctx.expression(0).accept(ExpressionVisitor)
    }
  }
}
