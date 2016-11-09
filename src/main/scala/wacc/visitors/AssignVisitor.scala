package wacc.visitors

import antlr.WACCParser._
import antlr.WACCParserBaseVisitor
import wacc.SymbolTable
import wacc.constructs._

import scala.util.{Failure, Success, Try}

object AssignLhsVisitor extends WACCParserBaseVisitor[AssignTarget] {

  override def visitAssignLhsIdent(ctx: AssignLhsIdentContext): AssignTarget = {
    SymbolTable.currentTable.lookupAll(ctx.IDENT().toString) match {
      case Some(symbol: Variable)   => symbol

      /*
      case None                     => Failure(Error("Semantic", "Variable not declared"))
      case default                  => Failure(Error("Semantic", "Can only assign to variables, not functions"))
      */
      }
    }

  override def visitAssignLhsArrayElement(ctx: AssignLhsArrayElementContext): AssignTarget =
    ctx.arrayElement().accept(ArrayElementVisitor)

  override def visitAssignLhsPairElement(ctx: AssignLhsPairElementContext): AssignTarget =
    ctx.pairElement().accept(PairElementVisitor)
}

object AssignRhsVisitor extends WACCParserBaseVisitor[AssignValue] {

  override def visitAssignRhsExpression(ctx: AssignRhsExpressionContext): AssignValue =
    ctx.expression().accept(ExpressionVisitor)

  override def visitAssingRhsArrayLiteral(ctx: AssingRhsArrayLiteralContext): AssignValue =
    ctx.arrayLiteral().accept(LiteralVisitor)

  override def visitAssingRhsPairConstructor(ctx: AssingRhsPairConstructorContext): AssignValue =
    ctx.pairConstructor().accept(PairConstructorVisitor)

  override def visitAssingRhsPairElement(ctx: AssingRhsPairElementContext): AssignValue =
    ctx.pairElement().accept(PairElementVisitor)

  override def visitAssingRhsFunctionCall(ctx: AssingRhsFunctionCallContext): AssignValue =
    ctx.functionCall().accept(FunctionCallVisitor)
}
