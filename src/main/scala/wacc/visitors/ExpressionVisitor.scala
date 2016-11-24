package wacc.visitors

import antlr.WACCParser._
import antlr.WACCParserBaseVisitor
import wacc.constructs._
import wacc.{FunctionReference, SymbolTable, VariableReference}

import scala.collection.JavaConversions._
import scala.util.Either

object ExpressionVisitor extends WACCParserBaseVisitor[Either[CompilationError, Expression]] {

  /**
    * An expression variable reference needs to be looked up during code generation
    * because the offset cannot be computed on the fly during parsing. Consider the case:
    *
    * begin
    *   int x = 1 ;
    *   begin
    *     print x;
    *     int y = 1
    *   end
    * end
    *
    * We would move the stack pointer on entry in the child scope but "print x" would have an offset of 0
    * while it should be 4. See the implementation of SymbolTable.lookupDeep. An additional lookupDeep during
    * code generation would recompute the offset taking into account the correct size of the child scope.
    * */
  override def visitVariableReference(ctx: VariableReferenceContext)
  : Either[CompilationError, VariableReference] = {
    val identifier = ctx.IDENT()

    SymbolTable().lookupDeep(identifier.getText) match {
      case Some(variable : VariableReference) =>
        Right(variable)
      case None =>
        Left(SemanticError("Identifier " + identifier.getText + " not declared", identifier.getSymbol))
    }
  }

  override def visitBracketedExp(ctx: BracketedExpContext): Either[CompilationError, Expression] = {
    ctx.expression().accept(ExpressionVisitor)
  }

  override def visitUnaryOperatorExp(ctx: UnaryOperatorExpContext): Either[CompilationError, Expression] = {
    val operator = UnaryOperator.fromOperatorString(ctx.unaryOperator.getText)
    val expr = ctx.expression().accept(ExpressionVisitor).right

    expr.flatMap(
      expr => operator match {
        case MinusOp | ChrOp if expr.vartype != Integer =>
          Left(SemanticError(ctx.unaryOperator.getText + " operator needs integers", ctx.start))

        case LenOp => expr.vartype match {
          case ArrayType(_) => Right(UnaryOperatorExpr(operator, expr))
          case _            => Left(SemanticError("'len' operator needs an array", ctx.start))
        }

        case OrdOp if expr.vartype != Character =>
          Left(SemanticError("'ord' operator needs character", ctx.start))

        case NotOp if expr.vartype != Boolean =>
          Left(SemanticError("'!' operator needs Boolean", ctx.start))

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
          Left(SemanticError(operator.binaryOperator + " operator needs 2 integers as its arguments", ctx.start))

        case GtBinOp | GteBinOp | LtBinOp | LteBinOp
        if expr1.vartype != expr2.vartype || (expr1.vartype != Integer && expr1.vartype != Character) =>
          Left(SemanticError(operator.binaryOperator + " operator needs 2 integers/characters as its arguments", ctx.start))

        case EqualsBinOp | NequalsBinOp
        if !compatibleTypes(expr1.vartype, expr2.vartype) =>
          Left(SemanticError(operator.binaryOperator + " operator needs 2 arguments of a compatible type", ctx.start))

        case AndBinOp | OrBinOp
        if expr1.vartype != Boolean || expr2.vartype != Boolean =>
          Left(SemanticError(operator.binaryOperator + " operator needs 2 booleans as its arguments", ctx.start))

        case default =>
          Right(BinaryOperatorExpr(expr1, operator, expr2))
        }
    }
  }

  override def visitLiteral(ctx: LiteralContext): Either[CompilationError, Expression]
    = ctx.accept(LiteralVisitor)

  override def visitArrayElement(ctx: ArrayElementContext): Either[CompilationError, ArrayElement] = {
    val identifier = ctx.variableReference().getText

    SymbolTable().lookupDeep(identifier) match {
      case Some(VariableReference(x, ArrayType(elemtype), offset)) => for {
        indexes <- sequenceOrLast(ctx.expression().toList map (e => e.accept(ExpressionVisitor))).right
      } yield ArrayElement(identifier, indexes, elemtype)

      // Special case for string indexing
      case Some(VariableReference(x, String, offset)) => for {
        indexes <- sequenceOrLast(ctx.expression().toList map (e => e.accept(ExpressionVisitor))).right
      } yield ArrayElement(identifier, indexes, Character)

      case None    => Left(SemanticError("Variable not declared", ctx.start))
      case default => Left(SemanticError("Identifier is not an array reference", ctx.start))
    }
  }
}



