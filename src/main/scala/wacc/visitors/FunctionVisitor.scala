package wacc.visitors

import antlr.WACCParser.FunctionContext
import antlr.WACCParserBaseVisitor
import wacc.constructs.Function
import scala.collection.JavaConversions._

object FunctionVisitor extends WACCParserBaseVisitor[Function] {

  override def visitFunction(ctx: FunctionContext): Function = {
    val ctxparamList = Option(ctx.parameterList())

    val paramList = ctxparamList match {
      case None => Seq()
      case Some(ls) => ls.parameter().toList
    }

    Function(
      ctx.IDENT().accept(IdentifierVisitor),
      paramList map (_.accept(ParamVisitor)),
      ctx.`type`().accept(TypeVisitor),
      ctx.statement().accept(StatementVisitor)
    )
  }
}
