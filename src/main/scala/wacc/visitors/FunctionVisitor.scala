package wacc.visitors

import antlr.WACCParser.FunctionContext
import antlr.WACCParserBaseVisitor
import wacc.constructs._
import wacc.util.SemanticErrors
import wacc.{FunctionReference, SymbolTable}

import scala.collection.JavaConversions._

object FunctionVisitor extends WACCParserBaseVisitor[Either[CompilationError, Function]] {

  def defineFunction(ctx: FunctionContext): Either[SemanticError, FunctionContext] = {
    val name = ctx.IDENT().getText
    val returnType = ctx.`type`().accept(TypeVisitor)
    val (parameterNames, parameterTypes) = FunctionVisitor.getParameters(ctx)
    val identifier = Function.fullName(name, parameterTypes)

    // Check for duplicate function name
    if (SymbolTable.functionsTable contains identifier)
      return Left(SemanticError(
        "Attempted redefinition of function " +
          SemanticErrors.functionSignatureToString(name, parameterTypes), ctx.start))

    // Validate parameters
    if (parameterNames.distinct.size != parameterNames.size)
      return Left(SemanticError("A function shouldn't have two or more parameters with the same name", ctx.start))

    // The function signature is as follows
    SymbolTable.declareFunction(FunctionReference(identifier, returnType, parameterTypes))
    (parameterNames, parameterTypes).zipped map SymbolTable().addFunctionArgument
    SymbolTable.completeFunctionDeclaration()

    Right(ctx)
  }

  override def visitFunction(ctx: FunctionContext): Either[CompilationError, Function] = {
    val name = ctx.IDENT().getText
    val returnType = ctx.`type`().accept(TypeVisitor)
    val identifier = Function.fullName(name, getParameters(ctx)._2)
    val arguments = SymbolTable.defineFunction(identifier)

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
    } yield Function(name, arguments, returnType, statements, SymbolTable.completeFunctionDefinition())

  }

  private def getParameters(ctx: FunctionContext): (Seq[String], Seq[Type]) = {
    // Parameters could be null so convert to empty sequence in that case
    val params = Option(ctx.parameterList()) match {
      case None => Seq()
      case Some(ls) => ls.parameter().toList
    }

    val names = params.map(_.IDENT().getText)
    val types = params.map(_.`type`().accept(TypeVisitor))

    (names, types)
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