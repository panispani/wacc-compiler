package wacc.visitors

import antlr.WACCParser.ProgramContext
import antlr.WACCParserBaseVisitor
import wacc.constructs.Program

import scala.collection.JavaConversions._

object ProgramVisitor extends WACCParserBaseVisitor[Program] {
  override def visitProgram(ctx: ProgramContext): Program = {
    val functions = ctx.function().toList map (_.accept(FunctionVisitor))
    val statement = ctx.sequence().statement().toList map (_.accept(StatementVisitor))

    Program(functions, statement)
  }
}
