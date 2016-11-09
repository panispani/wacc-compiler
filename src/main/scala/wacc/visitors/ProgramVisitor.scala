package wacc.visitors

import antlr.WACCParser.ProgramContext
import antlr.WACCParserBaseVisitor
import wacc.constructs.{CompilationError, Program}
import wacc.Util._

import scala.collection.JavaConversions._

object ProgramVisitor extends WACCParserBaseVisitor[Either[CompilationError, Program]] {
  override def visitProgram(ctx: ProgramContext): Either[CompilationError, Program] = {

    for {
      functions <- sequence(ctx.function().toList map (e => e.accept(FunctionVisitor))).right
      statement <- sequence(ctx.sequence().statement().toList map (s => s.accept(StatementVisitor))).right
    } yield Program(functions, statement)
  }
}
