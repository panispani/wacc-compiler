package wacc.visitors

import antlr.WACCParser.ProgramContext
import antlr.WACCParserBaseVisitor
import wacc.constructs._
import wacc.visitor._

import scala.collection.JavaConversions._

object ProgramVisitor extends WACCParserBaseVisitor[Either[CompilationError, Program]] {
  override def visitProgram(ctx: ProgramContext): Either[CompilationError, Program] = {

    def checkForReturnStatements(statements : Seq[Either[CompilationError, Statement]]): Seq[Either[CompilationError, Statement]] = {
      statements.map {
        _.right.flatMap({
          case ReturnStatement(_) => Left(SemanticError("Return statement in main program"))
          case statement : Statement => Right(statement)
        })
      }
    }

    for {
      statements <- sequence(checkForReturnStatements(ctx.sequence().statement().toList map (s => s.accept(StatementVisitor)))).right
      functions <- sequence(ctx.function().toList map (e => e.accept(FunctionVisitor))).right
    } yield Program(functions, statements)
  }
}
