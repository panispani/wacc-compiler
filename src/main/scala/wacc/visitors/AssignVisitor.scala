package wacc.visitors

import antlr.WACCParser._
import antlr.WACCParserBaseVisitor
import wacc.constructs._

object AssignLhsVisitor extends WACCParserBaseVisitor[Either[CompilationError, AssignTarget]] {

  override def visitAssignLhsIdent(ctx: AssignLhsIdentContext): Either[CompilationError, AssignTarget] = {
    ctx.variableReference().accept(ExpressionVisitor)
  }

  override def visitAssignLhsArrayElement(ctx: AssignLhsArrayElementContext): Either[CompilationError, AssignTarget] =
    ctx.arrayElement().accept(ExpressionVisitor)

  override def visitAssignLhsPairElement(ctx: AssignLhsPairElementContext): Either[CompilationError, AssignTarget] =
    ctx.pairElement().accept(PairElementVisitor)

  override def visitAssignLhsStructMember(ctx: AssignLhsStructMemberContext): Either[CompilationError, AssignTarget] =
    ctx.structMember().accept(StructMemberVisitor)
}

//Todo: probably you need it for arrayElement as well
object AssignRhsVisitor extends WACCParserBaseVisitor[Either[CompilationError, AssignValue]] {

  override def visitAssignRhsExpression(ctx: AssignRhsExpressionContext): Either[CompilationError, AssignValue] =
    ctx.expression().accept(ExpressionVisitor)

  override def visitAssignRhsArrayLiteral(ctx: AssignRhsArrayLiteralContext): Either[CompilationError, AssignValue] =
    ctx.arrayLiteral().accept(ArrayLiteralVisitor)

  override def visitAssignRhsPairConstructor(ctx: AssignRhsPairConstructorContext): Either[CompilationError, AssignValue] =
    ctx.pairConstructor().accept(PairConstructorVisitor)

  override def visitAssignRhsPairElement(ctx: AssignRhsPairElementContext): Either[CompilationError, AssignValue] =
    ctx.pairElement().accept(PairElementVisitor)

  override def visitAssignRhsFunctionCall(ctx: AssignRhsFunctionCallContext): Either[CompilationError, AssignValue] =
    ctx.functionCall().accept(FunctionCallVisitor)

  override def visitAssignRhsStructLiteral(ctx: AssignRhsStructLiteralContext): Either[CompilationError, AssignValue] =
    ctx.structLiteral().accept(StructLiteralVisitor)

  override def visitAssignRhsStructMember(ctx: AssignRhsStructMemberContext): Either[CompilationError, AssignValue] =
    ctx.structMember().accept(StructMemberVisitor)


}
