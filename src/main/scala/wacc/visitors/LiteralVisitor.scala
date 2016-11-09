package wacc.visitors

import antlr.WACCParser.LiteralContext
import antlr.WACCParserBaseVisitor
import wacc.constructs.Literal

/**
  * Created by panayiotis on 08/11/16.
  */
object LiteralVisitor extends WACCParserBaseVisitor[Literal] {
  override def visitLiteral(ctx: LiteralContext): Literal = super.visitLiteral(ctx)
}
