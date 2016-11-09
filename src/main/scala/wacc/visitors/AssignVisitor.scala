package wacc.visitors

import antlr.WACCParser._
import antlr.WACCParserBaseVisitor
import wacc.SymbolTable
import wacc.constructs._

import scala.util.{Failure, Success, Try}

object AssignLhsVisitor extends WACCParserBaseVisitor[Either[CompilationError, AssignTarget]] {

  override def visitAssignLhsIdent(ctx: AssignLhsIdentContext): Either[CompilationError, AssignTarget] = {
    SymbolTable.currentTable.lookupAll(ctx.variableReference().getText) match {
      case Some(symbol: Variable)   => Right(symbol)
      case None                     => Left(SemanticError("Variable not declared"))
      case default                  => Left(SemanticError("Can only assign to variables, not functions"))
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

  override def visitAssingRhsArrayLiteral(ctx: AssingRhsArrayLiteralContext): Either[CompilationError, AssignValue] =
    Right(ctx.arrayLiteral().accept(LiteralVisitor))

  override def visitAssingRhsPairConstructor(ctx: AssingRhsPairConstructorContext): Either[CompilationError, AssignValue] =
    Right(ctx.pairConstructor().accept(PairConstructorVisitor))

  override def visitAssingRhsPairElement(ctx: AssingRhsPairElementContext): Either[CompilationError, AssignValue] =
    Right(ctx.pairElement().accept(PairElementVisitor))

  override def visitAssingRhsFunctionCall(ctx: AssingRhsFunctionCallContext): Either[CompilationError, AssignValue] =
    ctx.functionCall().accept(FunctionCallVisitor)
}
