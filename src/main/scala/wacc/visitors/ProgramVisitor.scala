package wacc.visitors

import antlr.WACCParser.{FunctionContext, ProgramContext}
import antlr.WACCParserBaseVisitor
import wacc.constructs._
import wacc.{FunctionReference, SymbolTable}

import scala.collection.JavaConversions._

object ProgramVisitor extends WACCParserBaseVisitor[Either[Seq[CompilationError], Program]] {

  def defineFunction(ctx: FunctionContext): Option[SemanticError] = {

    val name = ctx.IDENT().getText
    val returnType = ctx.`type`().accept(TypeVisitor)
    val (parameterNames, parameterTypes) = FunctionVisitor.getParameters(ctx)
    val typed_name = Function.appendFunctionTypes(name, parameterTypes)

    // Check for duplicate function name
    if (SymbolTable.functionsTable contains typed_name)
      return Some(SemanticError("Attempted redefinition of function " + functionSignatureToString(name, parameterNames, parameterTypes), ctx.start))

    // Validate parameters
    if (parameterNames.distinct.size != parameterNames.size)
      return Some(SemanticError("A function shouldn't have two or more parameters with the same name", ctx.start))

    // The function signature is as follows
    SymbolTable.declareFunction(FunctionReference(typed_name, returnType, parameterTypes))
    (parameterNames, parameterTypes).zipped map SymbolTable().addFunctionArgument
    SymbolTable.completeFunctionDeclaration()

    None
  }

  private def functionSignatureToString(name: String, parameterNames: Seq[String], parameterTypes: Seq[Type]): String = {
    s"$name(${parameterTypes.mkString(", ")})"
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
      structs <- sequenceOrAll(ctx.struct().toList map (e => e.accept(StructVisitor))).right
      functions <- sequenceOrAll(ctx.function().toList map (e => e.accept(FunctionVisitor))).right
      statements <- sequenceOrAll(ctx.sequence().statement().toList map (
        _.accept(StatementVisitor).right.flatMap(semanticErrorIfReturn))).right
    } yield Program(structs, functions, ScopeStatement(statements, SymbolTable.globalTable))
  }
}
