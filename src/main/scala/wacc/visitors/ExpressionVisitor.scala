package wacc.visitors

import antlr.WACCParser._
import antlr.WACCParserBaseVisitor
import wacc.{FunctionReference, SymbolTable, VariableReference}
import wacc.constructs._

object ExpressionVisitor extends WACCParserBaseVisitor[Either[CompilationError, Expression]] {

  override def visitIntLiteral(ctx: IntLiteralContext): Either[CompilationError, Expression]
    = Right(IntegerLiteral(ctx.getText.toInt))

  override def visitBoolLiteral(ctx: BoolLiteralContext): Either[CompilationError, Expression]
    = Right(BoolLiteral(ctx.getText.toBoolean))

  override def visitCharLiteral(ctx: CharLiteralContext): Either[CompilationError, Expression]
    = Right(CharLiteral(ctx.getText.charAt(1))) // 0 is a quote

  override def visitVariableReference(ctx: VariableReferenceContext): Either[CompilationError, Expression] = {
    val identifier = ctx.IDENT().getText
    SymbolTable.currentTable.lookupAll(identifier) match {
      case Some(VariableReference(t)) => Right(VariableReferenceExpression(t))
//      case Some(FunctionReference(_, _)) => Left(SemanticError("Function is not a variable"))
      case None            => Left(SemanticError("Variable not declared : " + identifier))
    }
  }
}
