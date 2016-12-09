package wacc.visitors

import antlr.WACCParser.ProgramContext
import antlr.WACCParserBaseVisitor
import wacc.SymbolTable
import wacc.constructs._

import scala.collection.JavaConversions._

object ProgramVisitor extends WACCParserBaseVisitor[Either[Seq[CompilationError], Program]] {

  override def visitProgram(ctx: ProgramContext): Either[Seq[CompilationError], Program] = {

    def semanticErrorIfReturn(statement: Statement): Either[SemanticError, Statement] = statement match {
      case ReturnStatement(_) => Left(SemanticError("Return statement in main program", ctx.start))
      case statement: Statement => Right(statement)
    }

    for {
      structDefinitions <- sequenceOrAll(ctx.struct().toList map (e => e.accept(StructVisitor))).right

      functionContexts <- sequenceOrAll(ctx.function().toList ++ structDefinitions.unzip._2.flatten map
        FunctionVisitor.defineFunction).right

      functions <- sequenceOrAll(functionContexts map (_.accept(FunctionVisitor))).right

      statements <- sequenceOrAll(ctx.sequence().statement().toList map (
        _.accept(StatementVisitor).right.flatMap(semanticErrorIfReturn))).right
    } yield Program(structDefinitions.unzip._1, functions, ScopeStatement(statements, SymbolTable.globalTable))
  }
}
