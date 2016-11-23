package wacc.visitors

import antlr.WACCParser.{FunctionContext, ProgramContext}
import antlr.WACCParserBaseVisitor
import wacc.constructs._
import wacc.{FunctionReference, SymbolTable, VariableReference}

import scala.collection.JavaConversions._

object ProgramVisitor extends WACCParserBaseVisitor[Either[Seq[CompilationError], Program]] {

  private def defineFunction(ctx: FunctionContext): Option[SemanticError] = {

    val name = ctx.IDENT().getText

    // Check for duplicate function name
    if (SymbolTable.functionsTable contains name)
      return Some(SemanticError("Attempted redefinition of function " + name, ctx.start))

    val returnType = ctx.`type`().accept(TypeVisitor)

    // Parameters could be null so convert to empty sequence in that case
    val params = Option(ctx.parameterList()) match {
      case None => Seq()
      case Some(ls) => ls.parameter().toList
    }

    // Validate parameters
    val parameterNames = params.map(_.IDENT().getText)
    if (parameterNames.distinct.size != parameterNames.size)
      return Some(SemanticError("A function shouldn't have two or more parameters with the same name", ctx.start))

    val argumentTypes = params.map(_.`type`().accept(TypeVisitor))

    // The function signature is as follows
    SymbolTable.declareFunction(FunctionReference(name, returnType, argumentTypes))
    (parameterNames, argumentTypes).zipped map SymbolTable().addFunctionArgument
    SymbolTable.completeFunctionDeclaration()

    None
  }

  override def visitProgram(ctx: ProgramContext): Either[Seq[CompilationError], Program] = {

    def semanticErrorIfReturn(statement: Statement) : Either[SemanticError, Statement] = statement match {
      case ReturnStatement(_) => Left(SemanticError("Return statement in main program", ctx.start))
      case statement: Statement => Right(statement)
    }

    //define functions
    ctx.function() foreach (f => {
      defineFunction(f) match {
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
