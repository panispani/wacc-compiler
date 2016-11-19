package wacc.visitors

import antlr.WACCParser.{FunctionContext, ProgramContext}
import antlr.WACCParserBaseVisitor
import wacc.constructs._
import wacc.{FunctionReference, SymbolTable, VariableReference}

import scala.collection.JavaConversions._

object ProgramVisitor extends WACCParserBaseVisitor[Either[Seq[CompilationError], Program]] {

  private def defineFunction(ctx: FunctionContext): Option[SemanticError] = {
    val params = Option(ctx.parameterList()) match {
      case None => Seq()
      case Some(ls) => ls.parameter().toList
    }

    val name = ctx.IDENT().getText
    val args: Seq[Param] = params map (_.accept(ParamVisitor))
    val returnType = ctx.`type`().accept(TypeVisitor)

    if (SymbolTable.globalTable.lookupTyped(name).isDefined)
      return Some(SemanticError("Attempted redefinition of function " + name, ctx.start))
    else SymbolTable.globalTable.addFunction(name, FunctionReference(name, returnType, args))

    args foreach (arg => {

      val ident = arg.variable match {
        case VariableReference(varname, _, _) => varname
      }

      if (SymbolTable.currentTable.lookupTyped(ident).isDefined) {
        return Some(SemanticError("A function shouldn't have two or more parameters with the same name", ctx.start))
      }

      SymbolTable.currentTable.addFunctionArgument(ident, arg.variable, FunctionReference(name, returnType, args))
    })
    None
  }

  override def visitProgram(ctx: ProgramContext): Either[Seq[CompilationError], Program] = {

    def semanticErrorIfReturn(statement: Statement) : Either[SemanticError, Statement] = statement match {
      case ReturnStatement(_) => Left(SemanticError("Return statement in main program", ctx.start))
      case statement: Statement => Right(statement)
    }

    //define functions
    ctx.function() foreach (f => {
      SymbolTable.openScope()
      defineFunction(f) match {
        case Some(SemanticError(error, symbol)) =>
          SymbolTable.closeScope()
          return Left(Seq(SemanticError(error, symbol)))
        case None => ;
      }
      SymbolTable.closeScope()
    })

    for {
      functions <- sequenceOrAll(ctx.function().toList map (e => e.accept(FunctionVisitor))).right
      statements <- sequenceOrAll(ctx.sequence().statement().toList map (
        _.accept(StatementVisitor).right.flatMap(semanticErrorIfReturn))).right
    } yield Program(functions, statements)
  }
}
