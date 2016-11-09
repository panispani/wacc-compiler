package wacc.visitors

import antlr.WACCParser.FunctionCallContext
import antlr.WACCParserBaseVisitor
import wacc.constructs.{CompilationError, FunctionCall}

import scala.collection.JavaConversions._
import wacc.Util._


object FunctionCallVisitor extends WACCParserBaseVisitor[Either[CompilationError, FunctionCall]] {

  override def visitFunctionCall(ctx: FunctionCallContext): Either[CompilationError, FunctionCall] = {
    val ctxArgList = Option(ctx.argumentList())

    val argList = ctxArgList match {
      case None => Seq()
      case Some(ls) => ls.expression().toList
    }

    for {
      args <- sequence(argList map (_.accept(ExpressionVisitor))).right
    } yield FunctionCall(ctx.IDENT().getText, args)
  }
}
