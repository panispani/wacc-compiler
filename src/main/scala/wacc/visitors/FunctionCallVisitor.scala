package wacc.visitors

import antlr.WACCParser.FunctionCallContext
import antlr.WACCParserBaseVisitor
import wacc.constructs._
import wacc.{FunctionReference, SymbolTable}

import scala.collection.JavaConversions._


object FunctionCallVisitor extends WACCParserBaseVisitor[Either[CompilationError, FunctionCall]] {

  override def visitFunctionCall(ctx: FunctionCallContext): Either[CompilationError, FunctionCall] = {
    val name = ctx.IDENT().getText
    val ctxArgList = Option(ctx.argumentList())
    val untypedArgList = ctxArgList match {
      case None => Seq()
      case Some(ls) => ls.expression().toList
    }

    val typedArgList = sequenceOrLast(untypedArgList map (_.accept(ExpressionVisitor)))

    val functionSignature: Either[CompilationError, (String, Type, Seq[Type])] = {
      typedArgList match {
        case Right(argList) => {
          val argTypes = argList map (e => e.vartype)
          val typed_name   = Function.appendFunctionTypes(name, argTypes)

          SymbolTable.functionsTable.get(typed_name) match {
            case Some(function) => {
              function.reference match {
                case FunctionReference(f, returnType, argumentTypes) => Right((name, returnType, argumentTypes))
                case default => Left(SemanticError(ctx.IDENT().getText + " is not a function", ctx.start))
              }
            }
            case None => Left(SemanticError("Function " + ctx.IDENT().getText + "(" + argTypes.mkString(", ") + ") is undefined", ctx.start))
          }
        }
        case Left(error) => Left(error)
      }
    }

    typedArgList match {
      case Right(argList) =>
        functionSignature match {
          case Right((name, returnType, argTypes)) =>
            if (matchArgumentLists(argTypes, argList)) Right(FunctionCall(name, argList, returnType))
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
