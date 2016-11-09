package wacc.visitors

import antlr.WACCParser._
import antlr.WACCParserBaseVisitor
import wacc.constructs._
import wacc.{FunctionReference, SymbolTable, VariableReference}
import wacc.visitor._

object ExpressionVisitor extends WACCParserBaseVisitor[Either[CompilationError, Expression]] {

  override def visitIntLiteral(ctx: IntLiteralContext): Either[CompilationError, Expression]
  = Right(IntegerLiteral(ctx.getText.toInt))

  override def visitBoolLiteral(ctx: BoolLiteralContext): Either[CompilationError, Expression]
  = Right(BoolLiteral(ctx.getText.toBoolean))

  override def visitCharLiteral(ctx: CharLiteralContext): Either[CompilationError, Expression]
  = Right(CharLiteral(ctx.getText.charAt(1))) // 0 is a quote

  override def visitStringLiteral(ctx: StringLiteralContext): Either[CompilationError, Expression]
  = Right(StringLiteral(ctx.getText))

  override def visitVariableReference(ctx: VariableReferenceContext): Either[CompilationError, Expression] = {
    val identifier = ctx.IDENT().getText
    SymbolTable.currentTable.lookupAll(identifier) match {
      case Some(VariableReference(t)) => Right(VariableReferenceExpression(t))
      case Some(FunctionReference(_, _)) => Left(SemanticError("Function is not a variable"))
      case Some(_: Typed) => Left(SemanticError("Identifier is not a variable"))
      case None => Left(SemanticError("Variable not declared : " + identifier))
    }
  }

  override def visitPairLiteral(ctx: PairLiteralContext): Either[CompilationError, Expression] = {
    Right(PairLiteral(NullType, NullType, None))
  }

  override def visitBracketedExp(ctx: BracketedExpContext): Either[CompilationError, Expression] = {
    ctx.expression().accept(ExpressionVisitor)
  }

  override def visitUnaryOperatorExp(ctx: UnaryOperatorExpContext): Either[CompilationError, Expression] = {
    val operator = ctx.unaryOperator.accept(UnaryOperatorVisitor)
    val expr = ctx.expression().accept(ExpressionVisitor).right
    val unaryExpr = expr map (e => UnaryOperatorExpr(operator, e))

    operator match {
      case MinusOp | ChrOp if expr map (e => e.vartype != Integer) => Left(SemanticError(operator.unaryOperator + " operator needs integers"))
      case LenOp if expr map (e => e.vartype != ArrayType(Character)) => Left(SemanticError("'len' operator needs array of charcters"))
      case OrdOp if expr map (e => e.vartype != Character) => Left(SemanticError("'ord' operator needs character"))
      case NotOp if expr map (e => e.vartype != Boolean) => Left(SemanticError("'!' operator needs Boolean"))
      case default => unaryExpr
    }
  }

}



