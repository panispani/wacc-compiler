package wacc.visitors

import antlr.WACCParser.{FunctionCallContext, InstanceMethodCallContext}
import antlr.WACCParserBaseVisitor
import org.antlr.v4.runtime.Token
import wacc.constructs._
import wacc.util.SemanticErrors
import wacc.{FunctionReference, SymbolTable}

import scala.collection.JavaConversions._


object FunctionCallVisitor extends WACCParserBaseVisitor[Either[CompilationError, FunctionCall]] {

  override def visitFunctionCall(ctx: FunctionCallContext): Either[CompilationError, FunctionCall] = {
    val name = ctx.IDENT().getText

    val argumentList = Option(ctx.argumentList()) match {
      case None => Seq()
      case Some(ls) => ls.expression().toList
    }

    val argumentExpressions = sequenceOrLast(argumentList map (_.accept(ExpressionVisitor)))



    val functionSignature = argumentExpressions.right.flatMap(argList => {
      val argTypes = argList map (e => e.varType)
      getFunctionSignature(name, argTypes, ctx.start)
    })

    argumentExpressions match {
      case Right(argList) =>
        functionSignature match {
          case Right((name, returnType, argTypes)) =>
            if (matchArgumentLists(argTypes, argList)) Right(FunctionCall(name, Function.fullName(name, argTypes), argList, returnType))
            else Left(SemanticError("Argument list types don't match up", ctx.start))
          case Left(error) => Left(error)
        }
      case Left(error) => Left(error)
    }
  }

  override def visitInstanceMethodCall(ctx: InstanceMethodCallContext): Either[CompilationError, FunctionCall] = {
    val name = ctx.methodName.getText

    val argumentList = Option(ctx.argumentList()) match {
      case None => Seq()
      case Some(ls) => ls.expression().toList
    }

    val self = SymbolTable().lookupDeep(ctx.self.getText)

    if (self.isEmpty) {
      return Left(SemanticError("Attempted to call method on undefined reference", ctx.start))
    }

    val selfVR = self.get

    val argumentExpressions = sequenceOrLast(argumentList map (
      _.accept(ExpressionVisitor))).right.map(al => selfVR +: al)


    val functionSignature = argumentExpressions.right.flatMap(argList => {
      val argTypes = argList map (e => e.varType)
      getFunctionSignature(name, argTypes, ctx.start)
    })

    argumentExpressions match {
      case Right(argList) =>
        functionSignature match {
          case Right((name, returnType, argTypes)) =>
            if (matchArgumentLists(argTypes, argList)) Right(FunctionCall(name, Function.fullName(name, argTypes), argList, returnType))
            else Left(SemanticError("Argument list types don't match up", ctx.start))
          case Left(error) => Left(error)
        }
      case Left(error) => Left(error)
    }
  }

  private def getFunctionSignature(name: String, argTypes: Seq[Type], start: Token): Either[CompilationError, (String, Type, Seq[Type])] = {
    val identifier = Function.fullName(name, argTypes)

    SymbolTable.functionsTable.get(identifier) match {
      case Some(function) => {
        function.reference match {
          case FunctionReference(f, returnType, argumentTypes) => Right((name, returnType, argumentTypes))
          case default => Left(SemanticError(name + " is not a function", start))
        }
      }
      case None => argTypes.head match {
        case st @ StructType(id, _, parentName) =>
          parentName match {
            case Some(parent) => getFunctionSignature(name, SymbolTable.structsTable(parent) +: argTypes.tail, start)
            case None => Left(SemanticError(
              "Function " + SemanticErrors.functionSignatureToString(name, argTypes) + ") is undefined", start))
          }
        case _ => Left(SemanticError(
          "Function " + SemanticErrors.functionSignatureToString(name, argTypes) + ") is undefined", start))
      }
    }
  }

  private def matchArgumentLists(l1: Seq[Type], l2: Seq[Expression]) = l1.size == l2.size && matchTypes(l1, l2)

  private def matchTypes(l1: Seq[Type], l2: Seq[Expression]): Boolean = {
    val matchList = (l1, l2).zipped map((e1, e2) => compatibleTypes(e1, e2.varType))
    matchList.forall(b => b)
  }
}
