package wacc.visitors

import antlr.WACCParser.FunctionContext
import antlr.WACCParserBaseVisitor
import wacc.constructs._
import wacc.{FunctionReference, SymbolTable, VariableReference}

import scala.collection.JavaConversions._

object FunctionVisitor extends WACCParserBaseVisitor[Either[CompilationError, Function]] {

  override def visitFunction(ctx: FunctionContext): Either[CompilationError, Function] = {

    val name = ctx.IDENT().getText
    val returnType = ctx.`type`().accept(TypeVisitor)
    val arguments = SymbolTable.defineFunction(name)

    val matchReturnType: PartialFunction[Statement, Either[SemanticError, Statement]] = {
      case s @ ReturnStatement(expression) =>
        if (compatibleTypes(expression.vartype, returnType)) Right(s)
        else Left(SemanticError("The actual return type of a function should match the declared one", ctx.start))
      case s @ ExitStatement(_) => Right(s)
    }

    val matchReturnOrExit: PartialFunction[Statement, Either[SyntaxError, Statement]] = {
      case s @ (ReturnStatement(_) | ExitStatement(_)) => Right(s)
      case default => Left(SyntaxError("The last statement of a function should be a return", ctx.start))
    }

    def validateFunctionReturn(lastStatement: Statement): Either[CompilationError, Statement]
    = mapLastStatements(lastStatement, matchReturnOrExit(_).right flatMap matchReturnType)

    for {
      statements <- sequenceOrLast(ctx.sequence.statement.toList map (_.accept(StatementVisitor))).right

      lastStatement <- validateFunctionReturn(statements.last).right

      //body <- Right(statements.dropRight(1) :+ lastStatement).right
    } yield Function(name, arguments, returnType, statements, SymbolTable.completeFunctionDefinition())

  }

  private def mapLastStatements(lastStatement: Statement, f: Statement => Either[CompilationError, Statement])
  : Either[CompilationError, Statement] = lastStatement match {
    case ConditionalSimpleStatement(expression, trueStatements) => {
      mapLastStatements(trueStatements.statements.last, f)
    }
    case ConditionalElseStatement(expression, trueStatements, falseStatements) => {
      val trueRes = mapLastStatements(trueStatements.statements.last, f)
      val falseRes = mapLastStatements(falseStatements.statements.last, f)
      trueRes.right flatMap (_ => falseRes)
    }
    case ConditionalRecursiveStatement(expression, trueStatements, conditionalStatement) => {
      val trueRes = mapLastStatements(trueStatements.statements.last, f)
      val falseRes = mapLastStatements(conditionalStatement, f)
      trueRes.right flatMap (_ => falseRes)
    }
    case LoopStatement(expr, stats, _, _) => mapLastStatements(stats.last, f)
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