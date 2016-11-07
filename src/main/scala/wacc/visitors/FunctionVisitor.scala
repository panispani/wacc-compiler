package wacc.visitors

import antlr.WACCParser.FunctionContext
import antlr.WACCParserBaseVisitor
import wacc.constructs.Function
import scala.collection.JavaConversions._

object FunctionVisitor extends WACCParserBaseVisitor[Function] {

  override def visitFunction(ctx: FunctionContext): Function = {
    Function(
      ctx.IDENT().accept(IdentifierVisitor),
      ctx.parameterList().parameter().toList map (_.accept(ParamVisitor)),
      ctx.`type`().accept(TypeVisitor)
    )
  }
}
