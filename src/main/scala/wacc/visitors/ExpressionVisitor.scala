package wacc.visitors

import antlr.WACCParser._
import antlr.WACCParserBaseVisitor
import wacc.constructs._
import wacc.{FunctionReference, SymbolTable, VariableReference}
import wacc.visitor._

object ExpressionVisitor extends WACCParserBaseVisitor[Either[CompilationError, Expression]] {

  override def visitVariableReference(ctx: VariableReferenceContext): Either[CompilationError, Expression] = {
    val identifier = ctx.IDENT().getText
    SymbolTable.currentTable.lookupAll(identifier) match {
      case Some(VariableReference(t)) => Right(VariableReferenceExpression(t))
      case Some(FunctionReference(_, _)) => Left(SemanticError("Function is not a variable"))
      case Some(_: Typed) => Left(SemanticError("Identifier is not a variable"))
      case None => Left(SemanticError("Variable not declared : " + identifier))
    }
  }

  override def visitBracketedExp(ctx: BracketedExpContext): Either[CompilationError, Expression] = {
    ctx.expression().accept(ExpressionVisitor)
  }

  override def visitUnaryOperatorExp(ctx: UnaryOperatorExpContext): Either[CompilationError, Expression] = {
    val operator = ctx.unaryOperator.accept(UnaryOperatorVisitor)
    val expr = ctx.expression().accept(ExpressionVisitor).right
    val unaryExpr = expr map (e => UnaryOperatorExpr(operator, e))

    operator match {
      case MinusOp | ChrOp if expr map (e => e.vartype != Integer) =>
        Left(SemanticError(operator.unaryOperator + " operator needs integers"))
      case LenOp if expr map (e => e.vartype != ArrayType(Character)) =>
        Left(SemanticError("'len' operator needs array of charcters"))
      case OrdOp if expr map (e => e.vartype != Character) =>
        Left(SemanticError("'ord' operator needs character"))
      case NotOp if expr map (e => e.vartype != Boolean) =>
        Left(SemanticError("'!' operator needs Boolean"))
      case default =>
        unaryExpr
    }
  }

  override def visitBinaryOperatorExp(ctx: BinaryOperatorExpContext): Either[CompilationError, Expression] = {
    val operator = ctx.binaryOperator().accept(BinaryOperatorVisitor)
    for {
      expr1 : Expression <- ctx.expression(0).accept(ExpressionVisitor)
      expr2 : Expression <- ctx.expression(1).accept(ExpressionVisitor)
    } yield operator match {

      case timesBinOp | divBinOp | modBinOp | plusBinOp | minusBinOp
        if expr1.vartype != Integer || expr2.vartype != Integer =>
        SemanticError(operator.binaryOperator + " operator needs 2 integers as its arguments")

      case gtBinOp | gteBinOP | ltBinOp | lteBinOp
        if expr1.vartype != expr2.vartype || (expr1.vartype != Integer && expr2.vartype != Character) =>
        SemanticError(operator.binaryOperator + " operator needs 2 integers/characters as its arguments")

      case equalsBinOp | nequalsOp
        if expr1.vartype != expr2.vartype =>
        SemanticError(operator.binaryOperator + " operator needs 2 arguments of the same type")

      case andBinOp | orBinOp
        if expr1.vartype != Boolean || expr2.vartype != Boolean =>
        SemanticError(operator.binaryOperator + " operator needs 2 booleans as its arguments")

      case default =>
        BinaryOperatorExpr(expr1, operator, expr2)
    }
  }
}



