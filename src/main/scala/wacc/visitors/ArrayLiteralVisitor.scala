package wacc.visitors

import antlr.WACCParser.ArrayLiteralContext
import antlr.WACCParserBaseVisitor
import wacc.constructs.{ArrayLiteral, Error, Expression}

import scala.collection.JavaConversions._

object ArrayLiteralVisitor extends WACCParserBaseVisitor[ArrayLiteral] {
  def sameType(types: List[Expression]): Boolean = {
    types.forall(_.vartype == types.head.vartype)
    true
  }

  override def visitArrayLiteral(ctx: ArrayLiteralContext): ArrayLiteral = {
    val literals = ctx.expression() map (_.accept(ExpressionVisitor))
    if (!sameType(literals.toList)) {
      Error("Semantic", ctx.start.getLine + ":" + ctx.start.getCharPositionInLine + "Array literal types don't match")
    }
    ArrayLiteral(literals)
  }
}
