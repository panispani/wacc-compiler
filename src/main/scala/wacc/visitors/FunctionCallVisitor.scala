package wacc.visitors

import antlr.WACCParser.FunctionCallContext
import antlr.WACCParserBaseVisitor
import wacc.{FunctionReference, SymbolTable}
import wacc.constructs._
import wacc.visitor._

import scala.collection.JavaConversions._


object FunctionCallVisitor extends WACCParserBaseVisitor[Either[CompilationError, FunctionCall]] {

  override def visitFunctionCall(ctx: FunctionCallContext): Either[CompilationError, FunctionCall] = {
    val ctxArgList = Option(ctx.argumentList())

    val untypedArgList = ctxArgList match {
      case None => Seq()
      case Some(ls) => ls.expression().toList
    }

    val typedArgList = sequenceOrLast(untypedArgList map (_.accept(ExpressionVisitor)))

    val functionSignature: Either[CompilationError, (Type, Seq[Type])] =
      SymbolTable.globalTable.lookup(ctx.IDENT().getText) match {
      case Some(function) => {
        function match {
          case FunctionReference(f, returnType, arguments) => Right((returnType, arguments map (_.variable.vartype)))
          case default => Left(SemanticError(ctx.IDENT().getText + " is not a function", ctx.start))
        }
      }
      case None => Left(SemanticError("Function " + ctx.IDENT().getText + " is undefined", ctx.start))
    }

    typedArgList match {
      case Right(argList) =>
        functionSignature match {
          case Right((returnType, argTypes)) =>
            if (matchArgumentLists(argTypes, argList)) Right(FunctionCall(ctx.IDENT().getText, argList, returnType))
            else Left(SemanticError("Argument list types don't match up", ctx.start))
          case Left(error) => Left(error)
        }
      case Left(error) => Left(error)
    }
  }

  private def matchArgumentLists(l1: Seq[Type], l2: Seq[Expression]) = l1.size == l2.size && matchTypes(l1, l2)

  private def matchTypes(l1: Seq[Type], l2: Seq[Expression]): Boolean = {
    val matchList = (l1, l2).zipped map((e1, e2) => compatibleTypes(e1, e2.vartype))
    matchList.forall(b => b)
  }
}
