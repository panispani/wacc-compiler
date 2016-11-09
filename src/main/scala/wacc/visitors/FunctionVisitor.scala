package wacc.visitors

import antlr.WACCParser.FunctionContext
import antlr.WACCParserBaseVisitor
import wacc.constructs.{Function, CompilationError}

import scala.collection.JavaConversions._

object FunctionVisitor extends WACCParserBaseVisitor[Either[CompilationError, Function]] {

  override def visitFunction(ctx: FunctionContext): Either[CompilationError, Function] = {
    val params = Option(ctx.parameterList()) match {
      case None => Seq()
      case Some(ls) => ls.parameter().toList
    }

    val name = ctx.IDENT().toString
    val args = params map (_.accept(ParamVisitor))
    val returnType = ctx.`type`().accept(TypeVisitor)

    for {
      body <- ctx.statement().accept(StatementVisitor).right
    } yield Function(name, args, returnType, body)
  }
}
