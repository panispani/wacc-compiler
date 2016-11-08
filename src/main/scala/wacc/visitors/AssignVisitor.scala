package wacc.visitors

import antlr.WACCParser._
import antlr.WACCParserBaseVisitor
import wacc.constructs._

object AssignVisitor extends WACCParserBaseVisitor[Assign] {

  override def visitAssign(ctx: AssignContext): Assign = {
    val lhs = ctx.assignLhs().accept(AssignLhsVisitor)
    val rhs = ctx.assignRhs().accept(AssignRhsVisitor)

    Assign(lhs, rhs)
  }
}

object AssignLhsVisitor extends WACCParserBaseVisitor[AssignTarget] {

  override def visitAssignLhsIdent(ctx: AssignLhsIdentContext): AssignTarget =
    ctx.IDENT().accept(IdentifierVisitor)

  override def visitAssignLhsArrayElement(ctx: AssignLhsArrayElementContext): AssignTarget =
    ctx.arrayElement().accept(ArrayElementVisitor)

  override def visitAssignLhsPairElement(ctx: AssignLhsPairElementContext): AssignTarget =
    ctx.pairElement().accept(PairElementVisitor)
}

object AssignRhsVisitor extends WACCParserBaseVisitor[AssignValue] {

  override def visitAssignRhsExpression(ctx: AssignRhsExpressionContext): AssignValue =
    ctx.expression().accept(ExpressionVisitor)

  override def visitAssingRhsArrayLiteral(ctx: AssingRhsArrayLiteralContext): AssignValue =
    ctx.arrayLiteral().accept(ArrayLiteralVisitor)

  override def visitAssingRhsPairConstructor(ctx: AssingRhsPairConstructorContext): AssignValue =
    ctx.pairConstructor().accept(PairConstructorVisitor)

  override def visitAssingRhsPairElement(ctx: AssingRhsPairElementContext): AssignValue =
    ctx.pairElement().accept(PairElementVisitor)

  override def visitAssingRhsFunctionCall(ctx: AssingRhsFunctionCallContext): AssignValue =
    ctx.functionCall().accept(FunctionCallVisitor)
}
