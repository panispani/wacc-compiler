package wacc.visitors

import antlr.WACCParser.StructLiteralContext
import antlr.WACCParserBaseVisitor
import wacc.constructs.{CompilationError, StructLiteral}

import scala.collection.JavaConversions._

object StructLiteralVisitor extends WACCParserBaseVisitor[Either[CompilationError, StructLiteral]] {

  override def visitStructLiteral(ctx: StructLiteralContext): Either[CompilationError, StructLiteral] = {
    val literals = ctx.expression().toList map (_.accept(ExpressionVisitor))

    sequenceOrLast(literals).right flatMap (ls => Right(StructLiteral(ls)))
  }
}
