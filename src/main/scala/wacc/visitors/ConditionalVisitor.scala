package wacc.visitors

import antlr.WACCParser.{IfElseContext, IfRecursiveContext, IfSimpleContext}
import antlr.WACCParserBaseVisitor
import wacc.SymbolTable
import wacc.constructs.{Boolean, CompilationError, ConditionalElseStatement, ConditionalRecursiveStatement, ConditionalSimpleStatement, ConditionalStatement, ScopeStatement, SemanticError}
import wacc.util.SemanticErrors

import scala.collection.JavaConversions._


object ConditionalVisitor extends WACCParserBaseVisitor[Either[CompilationError, ConditionalStatement]] {


  override def visitIfSimple(ctx: IfSimpleContext): Either[CompilationError, ConditionalStatement] = {
    val tuple = for {
      expression <- ctx.expression().accept(ExpressionVisitor).right
      _ <- Right(SymbolTable.openScope()).right
      trueStatements <- sequenceOrLast(ctx.trueSequence.statement().toList map (s => s.accept(StatementVisitor))).right
      trueTable <- Right(SymbolTable.closeScope()).right

    } yield (expression, trueStatements, trueTable)
    val conditional = tuple match {
      case Left(error)                                                => Left(error)
      case Right((expression, trueStatements, trueTable)) => expression.vartype match {
        case Boolean => Right(ConditionalSimpleStatement(expression, ScopeStatement(trueStatements, trueTable)))
        case default => Left(
          SemanticError(
            "Conditional statement " + SemanticErrors.typeError("expression", expression.vartype, Boolean),
            ctx.start))
      }
    }

    conditional
  }

  override def visitIfElse(ctx: IfElseContext): Either[CompilationError, ConditionalStatement] = {
    val tuple = for {
      expression <- ctx.expression().accept(ExpressionVisitor).right
      _ <- Right(SymbolTable.openScope()).right
      trueStatements <- sequenceOrLast(ctx.trueSequence.statement().toList map (s => s.accept(StatementVisitor))).right
      trueTable <- Right(SymbolTable.closeScope()).right
      _ <- Right(SymbolTable.openScope()).right
      falseStatements <- sequenceOrLast(ctx.falseSequence.statement().toList map (s => s.accept(StatementVisitor))).right
    } yield (expression, trueStatements, falseStatements, trueTable)
    val falseTable = SymbolTable.closeScope()
    val conditional = tuple match {
      case Left(error)                                                => Left(error)
      case Right((expression, trueStatements, falseStatements, trueTable)) => expression.vartype match {
        case Boolean => Right(ConditionalElseStatement(expression, ScopeStatement(trueStatements, trueTable), ScopeStatement(falseStatements, falseTable)))
        case default => Left(
          SemanticError(
            "Conditional statement " + SemanticErrors.typeError("expression", expression.vartype, Boolean),
            ctx.start))
      }
    }

    conditional
  }

  override def visitIfRecursive(ctx: IfRecursiveContext): Either[CompilationError, ConditionalStatement] = {
    val tuple = for {
      expression <- ctx.expression().accept(ExpressionVisitor).right
      _ <- Right(SymbolTable.openScope()).right
      trueStatements <- sequenceOrLast(ctx.trueSequence.statement().toList map (s => s.accept(StatementVisitor))).right
      trueTable <- Right(SymbolTable.closeScope()).right
      conditionalStatement <- ctx.conditionalStatement().accept(ConditionalVisitor).right
    } yield (expression, trueStatements, conditionalStatement, trueTable)

    val conditional = tuple match {
      case Left(error)                                                => Left(error)
      case Right((expression, trueStatements, conditionalStatement, trueTable)) => expression.vartype match {
        case Boolean => Right(ConditionalRecursiveStatement(expression, ScopeStatement(trueStatements, trueTable), conditionalStatement))
        case default => Left(
          SemanticError(
            "Conditional statement " + SemanticErrors.typeError("expression", expression.vartype, Boolean),
            ctx.start))
      }
    }

    conditional
  }

}
