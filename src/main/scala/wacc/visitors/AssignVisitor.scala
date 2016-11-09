package wacc.visitors

import antlr.WACCParser._
import antlr.WACCParserBaseVisitor
import wacc.SymbolTable
import wacc.constructs._

import scala.util.{Failure, Success, Try}

object AssignLhsVisitor extends WACCParserBaseVisitor[Either[CompilationError, AssignTarget]] {

  override def visitAssignLhsIdent(ctx: AssignLhsIdentContext): Either[CompilationError, AssignTarget] = {
    SymbolTable.currentTable.lookupAll(ctx.variableReference().getText) match {
      case Some(symbol: AssignTarget) => Right(symbol)
      case None                       => Left(SemanticError("Variable not declared"))
      case target @ default           => Left(SemanticError("Invalid assignment target " + target))
      }
    }

  override def visitAssignLhsArrayElement(ctx: AssignLhsArrayElementContext): Either[CompilationError, AssignTarget] =
    ctx.arrayElement().accept(ArrayElementVisitor)

  override def visitAssignLhsPairElement(ctx: AssignLhsPairElementContext): Either[CompilationError, AssignTarget] =
    Right(ctx.pairElement().accept(PairElementVisitor))
}

object AssignRhsVisitor extends WACCParserBaseVisitor[Either[CompilationError, AssignValue]] {

  override def visitAssignRhsExpression(ctx: AssignRhsExpressionContext): Either[CompilationError, AssignValue] =
    ctx.expression().accept(ExpressionVisitor)

  override def visitAssignRhsArrayLiteral(ctx: AssignRhsArrayLiteralContext): Either[CompilationError, AssignValue] =
    Right(ctx.arrayLiteral().accept(LiteralVisitor))

  override def visitAssignRhsPairConstructor(ctx: AssignRhsPairConstructorContext): Either[CompilationError, AssignValue] =
    ctx.pairConstructor().accept(PairConstructorVisitor)

  override def visitAssignRhsPairElement(ctx: AssignRhsPairElementContext): Either[CompilationError, AssignValue] =
    Right(ctx.pairElement().accept(PairElementVisitor))

  override def visitAssignRhsFunctionCall(ctx: AssignRhsFunctionCallContext): Either[CompilationError, AssignValue] =
    ctx.functionCall().accept(FunctionCallVisitor)
}
