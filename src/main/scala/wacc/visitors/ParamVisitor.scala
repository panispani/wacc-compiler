package wacc.visitors

import antlr.WACCParser.ParameterContext
import antlr.WACCParserBaseVisitor
import wacc.constructs.{Param, Variable}

object ParamVisitor extends WACCParserBaseVisitor[Param] {
  override def visitParameter(ctx: ParameterContext): Param = {
    Param (
      Variable(
        ctx.IDENT().accept(IdentifierVisitor),
        ctx.`type`.accept(TypeVisitor)
      )
    )
  }
}
