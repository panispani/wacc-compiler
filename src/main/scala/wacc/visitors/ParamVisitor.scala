package wacc.visitors

import antlr.WACCParser.ParameterContext
import antlr.WACCParserBaseVisitor
import wacc.VariableReference

object ParamVisitor extends WACCParserBaseVisitor[VariableReference] {
  override def visitParameter(ctx: ParameterContext): VariableReference = {
    VariableReference(ctx.IDENT().toString, ctx.`type`.accept(TypeVisitor), 0)
  }
}
