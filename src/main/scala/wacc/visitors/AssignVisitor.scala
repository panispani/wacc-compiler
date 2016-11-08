package wacc.visitors

import antlr.WACCParser._
import antlr.WACCParserBaseVisitor
import wacc.SymbolTable
import wacc.constructs._

import scala.util.{Failure, Success, Try}

object AssignVisitor extends WACCParserBaseVisitor[Try[Assign]] {

  override def visitAssign(ctx: AssignContext): Try[Assign] = {
    val lhs = ctx.assignLhs().accept(AssignLhsVisitor)
    val rhs = ctx.assignRhs().accept(AssignRhsVisitor)

    Success(Assign(lhs.get, rhs.get))
  }
}

object AssignLhsVisitor extends WACCParserBaseVisitor[Try[AssignTarget]] {

  override def visitAssignLhsIdent(ctx: AssignLhsIdentContext): Try[AssignTarget] = {
    SymbolTable.currentTable.lookupAll(ctx.IDENT().toString) match {
      case Some(symbol: Variable)   => Success(symbol)
      case None                     => Failure(Error("Semantic", "Variable not declared"))
      case default                  => Failure(Error("Semantic", "Can only assign to variables, not functions"))
      }
    }

  override def visitAssignLhsArrayElement(ctx: AssignLhsArrayElementContext): Try[AssignTarget] =
    ctx.arrayElement().accept(ArrayElementVisitor).flatMap(Success(_))

  override def visitAssignLhsPairElement(ctx: AssignLhsPairElementContext): Try[AssignTarget] =
    Success(ctx.pairElement().accept(PairElementVisitor))
}

object AssignRhsVisitor extends WACCParserBaseVisitor[Try[AssignValue]] {

  override def visitAssignRhsExpression(ctx: AssignRhsExpressionContext): Try[AssignValue] =
    Success(ctx.expression().accept(ExpressionVisitor))

  override def visitAssingRhsArrayLiteral(ctx: AssingRhsArrayLiteralContext): Try[AssignValue] =
    Success(ctx.arrayLiteral().accept(ArrayLiteralVisitor))

  override def visitAssingRhsPairConstructor(ctx: AssingRhsPairConstructorContext): Try[AssignValue] =
    Success(ctx.pairConstructor().accept(PairConstructorVisitor))

  override def visitAssingRhsPairElement(ctx: AssingRhsPairElementContext): Try[AssignValue] =
    Success(ctx.pairElement().accept(PairElementVisitor))

  override def visitAssingRhsFunctionCall(ctx: AssingRhsFunctionCallContext): Try[AssignValue] =
    Success(ctx.functionCall().accept(FunctionCallVisitor))
}
