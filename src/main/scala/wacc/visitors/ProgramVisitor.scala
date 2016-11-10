package wacc.visitors

import antlr.WACCParser.ProgramContext
import antlr.WACCParserBaseVisitor
import wacc.constructs._
import wacc.visitor._

import scala.collection.JavaConversions._

object ProgramVisitor extends WACCParserBaseVisitor[Either[CompilationError, Program]] {
  override def visitProgram(ctx: ProgramContext): Either[CompilationError, Program] = {

    def semanticErrorIfReturn(statement: Statement) : Either[SemanticError, Statement] = statement match {
      case ReturnStatement(_) => Left(SemanticError("Return statement in main program"))
      case statement: Statement => Right(statement)
    }

    for {
      statements <- sequence(ctx.sequence().statement().toList map (
        _.accept(StatementVisitor).right.flatMap(semanticErrorIfReturn))).right

      functions <- sequence(ctx.function().toList map (e => e.accept(FunctionVisitor))).right
    } yield Program(functions, statements)
  }
}
