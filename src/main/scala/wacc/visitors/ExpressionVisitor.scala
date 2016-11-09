package wacc.visitors

import antlr.WACCParser._
import antlr.WACCParserBaseVisitor
import wacc.constructs._
import wacc.{FunctionReference, SymbolTable, VariableReference}

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
      case Some(VariableReference(t))    => Right(VariableReferenceExpression(t))
      case Some(FunctionReference(_, _)) => Left(SemanticError("Function is not a variable"))
      case Some(_ : Typed)               => Left(SemanticError("Identifier is not a variable"))
      case None                          => Left(SemanticError("Variable not declared : " + identifier))
    }
  }

  override def visitPairLiteral(ctx: PairLiteralContext): Either[CompilationError, Expression] = {
    Right(PairLiteral(NullType, NullType, None)) // put values in
  }
}
