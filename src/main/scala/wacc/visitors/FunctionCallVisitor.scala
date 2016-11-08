package wacc.visitors

import antlr.WACCParser.FunctionCallContext
import antlr.WACCParserBaseVisitor
import wacc.constructs.FunctionCall

import scala.collection.JavaConversions._

object FunctionCallVisitor extends WACCParserBaseVisitor[FunctionCall] {

  override def visitFunctionCall(ctx: FunctionCallContext): FunctionCall = {
    val ctxArgList = Option(ctx.argumentList())

    val argList = ctxArgList match {
      case None => Seq()
      case Some(ls) => ls.expression().toList
    }

    FunctionCall(
      ctx.IDENT().accept(IdentifierVisitor),
      argList map (_.accept(ExpressionVisitor))
    )
  }
}
