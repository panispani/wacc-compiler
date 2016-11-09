package wacc.visitors

import antlr.WACCParser.FunctionContext
import antlr.WACCParserBaseVisitor
import wacc.{FunctionReference, SymbolTable}
import wacc.constructs.{CompilationError, Function, Param}
import wacc.visitor._

import scala.collection.JavaConversions._

object FunctionVisitor extends WACCParserBaseVisitor[Either[CompilationError, Function]] {

  override def visitFunction(ctx: FunctionContext): Either[CompilationError, Function] = {
    val params = Option(ctx.parameterList()) match {
      case None => Seq()
      case Some(ls) => ls.parameter().toList
    }

    val name = ctx.IDENT().getText
    val args: Seq[Param] = params map (_.accept(ParamVisitor))
    val returnType = ctx.`type`().accept(TypeVisitor)

    SymbolTable.currentTable.addTyped("f", FunctionReference(returnType, args map (_.variable.vartype)))

    for {
      body <- sequence(ctx.sequence().statement().toList map (s => s.accept(StatementVisitor))).right
    } yield Function(name, args, returnType, body)
  }
}

// function must have a return statement - syntax error
// return statement is the last statement is the function - semantic error
// return values(s) are the same(type) with the function type - semantic error
// function parameters are not duplicated - semantic error


//last statement - return
//get list of return types
//not null, all same, same with returnType
//func parameters not duplicated
//funcNotRedefined