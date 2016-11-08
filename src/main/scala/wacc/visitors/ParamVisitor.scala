package wacc.visitors

import antlr.WACCParser.ParameterContext
import antlr.WACCParserBaseVisitor
import wacc.constructs.{FunctionParam, Variable}

object ParamVisitor extends WACCParserBaseVisitor[FunctionParam] {
  override def visitParameter(ctx: ParameterContext): FunctionParam = {
    FunctionParam (
      Variable(
        ctx.IDENT().accept(IdentifierVisitor),
        ctx.`type`.accept(TypeVisitor)
      )
    )
  }
}
