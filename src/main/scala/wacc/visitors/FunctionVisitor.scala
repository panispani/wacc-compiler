package wacc.visitors

import antlr.WACCParser.FunctionContext
import antlr.WACCParserBaseVisitor
import wacc.constructs.Function
import scala.collection.JavaConversions._

object FunctionVisitor extends WACCParserBaseVisitor[Function] {

  override def visitFunction(ctx: FunctionContext): Function = {
    val ctxparamList = ctx.parameterList()
    if (ctxparamList == null) {
      Function(
        ctx.IDENT().accept(IdentifierVisitor),
        List(),
        ctx.`type`().accept(TypeVisitor),
        ctx.statement().accept(StatementVisitor)
      )
    } else {
      Function(
        ctx.IDENT().accept(IdentifierVisitor),
        ctxparamList.parameter().toList map (_.accept(ParamVisitor)),
        ctx.`type`().accept(TypeVisitor),
        ctx.statement().accept(StatementVisitor)
      )
    }
  }
}
