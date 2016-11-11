package wacc.visitors

import antlr.WACCParser.ArrayLiteralContext
import antlr.WACCParserBaseVisitor
import wacc.constructs.{ArrayLiteral, CompilationError, Expression, SemanticError}
import wacc.visitor._

import scala.collection.JavaConversions._

object ArrayLiteralVisitor extends WACCParserBaseVisitor[Either[CompilationError, ArrayLiteral]] {
  def sameType(types: Seq[Expression]): Boolean = {
    if (types.nonEmpty) types.forall(_.vartype == types.head.vartype) else true
  }

  override def visitArrayLiteral(ctx: ArrayLiteralContext): Either[CompilationError, ArrayLiteral] = {
    val literals = ctx.expression().toList map (_.accept(ExpressionVisitor))

    sequenceOrLast(literals).right flatMap (ls =>
      if (!sameType(ls)) {
        val message = ctx.start.getLine + ":" + ctx.start.getCharPositionInLine + "Array literal types don't match"
        Left(SemanticError(message, ctx.LB().getSymbol))
      }
      else Right(ArrayLiteral(ls)))
  }
}
