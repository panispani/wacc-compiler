package wacc.visitors

import antlr.WACCParser._
import antlr.WACCParserBaseVisitor
import wacc.constructs._
import wacc.{FunctionReference, SymbolTable, VariableReference}

import scala.util.Either

object ExpressionVisitor extends WACCParserBaseVisitor[Either[CompilationError, Expression]] {


  override def visitLiteral(ctx: LiteralContext): Either[CompilationError, Expression] = {
    Right(ctx.accept(LiteralVisitor))
  }

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

    expr.flatMap(
      expr => operator match {
        case MinusOp | ChrOp if expr.vartype != Integer =>
          Left(SemanticError(operator.unaryOperator + " operator needs integers"))
        case LenOp if expr.vartype != ArrayType(Character) =>
          Left(SemanticError("'len' operator needs array of charcters"))
        case OrdOp if expr.vartype != Character =>
          Left(SemanticError("'ord' operator needs character"))
        case NotOp if expr.vartype != Boolean =>
          Left(SemanticError("'!' operator needs Boolean"))
        case default =>
          Right(UnaryOperatorExpr(operator, expr))
      }
    )

  }


  override def visitBinaryOperatorExp(ctx: BinaryOperatorExpContext): Either[CompilationError, Expression] = {
    val operator = BinaryOperator(ctx.op.getText)

    val pair = for {
      expr1 <- ctx.expression(0).accept(ExpressionVisitor).right
      expr2 <- ctx.expression(1).accept(ExpressionVisitor).right
    } yield (expr1, expr2)

    pair match {
      case Left(error)                                    => Left(error)
      case Right((expr1: Expression, expr2: Expression))  => operator match {

        case TimesBinOp | DivBinOp | ModBinOp | PlusBinOp | MinusBinOp
        if expr1.vartype != Integer || expr2.vartype != Integer =>
          Left(SemanticError(operator.binaryOperator + " operator needs 2 integers as its arguments"))

        case GtBinOp | GteBinOp | LtBinOp | LteBinOp
        if expr1.vartype != expr2.vartype || (expr1.vartype != Integer && expr2.vartype != Character) =>
          Left(SemanticError(operator.binaryOperator + " operator needs 2 integers/characters as its arguments"))

        case EqualsBinOp | NequalsBinOp
        if expr1.vartype != expr2.vartype =>
          Left(SemanticError(operator.binaryOperator + " operator needs 2 arguments of the same type"))

        case AndBinOp | OrBinOp
        if expr1.vartype != Boolean || expr2.vartype != Boolean =>
          Left(SemanticError(operator.binaryOperator + " operator needs 2 booleans as its arguments"))

        case default =>
          Right(BinaryOperatorExpr(expr1, operator, expr2))
        }
    }

  }
}



