package wacc.visitors

import antlr.WACCParser.FunctionContext
import antlr.WACCParserBaseVisitor
import wacc.constructs.{CompilationError, ConditionalStatement, ExitStatement, Function, LoopStatement, Param, ReturnStatement, SemanticError, Statement, SyntaxError}
import wacc.visitor._
import wacc.{FunctionReference, SymbolTable, VariableReference}

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

    SymbolTable.currentTable.addTyped(name, FunctionReference(name, returnType, args map (_.variable.vartype)))

    SymbolTable.openScope()

    args map (arg => {
      val ident = arg.variable.identifier

      if (SymbolTable.currentTable.lookup(ident).isDefined) {
        return Left(SemanticError("A function shouldn't have two or more parameters with the same"))
      }

      SymbolTable.currentTable.addTyped(ident, VariableReference(ident, arg.variable.vartype))
    })

    val matchReturnType: PartialFunction[Statement, Either[SemanticError, Statement]] = {
      case s @ ReturnStatement(expression) =>
        if (expression.vartype == returnType) Right(s)
        else Left(SemanticError("The actual return type of a function should match the declared one"))
      case s @ ExitStatement(_) => Right(s)
    }

    val matchReturnOrExit: PartialFunction[Statement, Either[SyntaxError, Statement]] = {
      case s @ (ReturnStatement(_) | ExitStatement(_)) => Right(s)
      case default => Left(SyntaxError("The last statement of a function should be a return"))
    }

    val function = for {
      statements <- sequence(ctx.sequence.statement.toList map (_.accept(StatementVisitor))).right

      lastStatement <- mapLastStatements(
        statements.last,
        matchReturnOrExit(_).right flatMap matchReturnType).right

      body <- Right(statements.dropRight(1) :+ lastStatement).right
    } yield Function(name, args, returnType, body)

    SymbolTable.closeScope()

    function
  }

  private def mapLastStatements(lastStatement: Statement, f: Statement => Either[CompilationError, Statement])
  : Either[CompilationError, Statement] = lastStatement match {
    case ConditionalStatement(expr, trueStats, falseStats) =>
      val trueRes = mapLastStatements(trueStats.last, f)
      val falseRes = mapLastStatements(falseStats.last, f)
      trueRes.right flatMap (_ => falseRes)
    case LoopStatement(expr, stats) => mapLastStatements(stats.last, f)
    case statement => f(statement)
  }
}

// function must have a return statement - syntax error
// return statement is the last statement of the function - semantic error
// return values(s) are the same(type) as the function type - semantic error


//last statement - return
//get list of return types
//not null, all same, same with returnType
//func parameters not duplicated
//funcNotRedefined