package wacc.visitors

import antlr.WACCParser.ProgramContext
import antlr.WACCParserBaseVisitor
import wacc.SymbolTable
import wacc.constructs._

import scala.collection.JavaConversions._

object ProgramVisitor extends WACCParserBaseVisitor[Either[Seq[CompilationError], Program]] {

  override def visitProgram(ctx: ProgramContext): Either[Seq[CompilationError], Program] = {

    def semanticErrorIfReturn(statement: Statement) : Either[SemanticError, Statement] = statement match {
      case ReturnStatement(_) => Left(SemanticError("Return statement in main program", ctx.start))
      case statement: Statement => Right(statement)
    }

    ctx.function() foreach (f => {
      FunctionVisitor.defineFunction(f) match {
        case Some(SemanticError(error, symbol)) =>
          return Left(Seq(SemanticError(error, symbol)))
        case None =>
      }
    })

    for {
      functions <- sequenceOrAll(ctx.function().toList map (e => e.accept(FunctionVisitor))).right
      statements <- sequenceOrAll(ctx.sequence().statement().toList map (
        _.accept(StatementVisitor).right.flatMap(semanticErrorIfReturn))).right
    } yield Program(functions, ScopeStatement(statements, SymbolTable.globalTable))
  }
}
