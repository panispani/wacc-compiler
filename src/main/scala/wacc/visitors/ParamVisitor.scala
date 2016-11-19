package wacc.visitors

import antlr.WACCParser.ParameterContext
import antlr.WACCParserBaseVisitor
import wacc.VariableReference
import wacc.constructs.{Param}

object ParamVisitor extends WACCParserBaseVisitor[Param] {
  override def visitParameter(ctx: ParameterContext): Param = {
    Param(VariableReference(ctx.IDENT().toString, ctx.`type`.accept(TypeVisitor), 0))
  }
}
