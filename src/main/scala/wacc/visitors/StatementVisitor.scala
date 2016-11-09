package wacc.visitors

import antlr.WACCParser._
import antlr.WACCParserBaseVisitor
import wacc.SymbolTable
import wacc.constructs._

object StatementVisitor extends WACCParserBaseVisitor[Either[CompilationError, Statement]] {

  override def visitSkip(ctx: SkipContext): Either[CompilationError, Skip] = {
    Right(Skip())
  }

  override def visitDeclare(ctx: DeclareContext): Either[CompilationError, Declare] = {
    val vartype = ctx.`type`().accept(TypeVisitor)
    val identifier = ctx.IDENT().toString

    ctx.assignRhs().accept(AssignRhsVisitor).right.flatMap(rhs => rhs.vartype == vartype match {
      case true  => {
        SymbolTable.currentTable.addTyped(identifier, VariableReferenceExpression(vartype))
        Right(Declare(vartype, identifier, rhs))
      }
      case false => Left(SemanticError("Expected type " + vartype + ", got " + rhs.vartype))
    })
  }

  override def visitExit(ctx: ExitContext): Either[CompilationError, ExitStatement] = {
    ctx.expression().accept(ExpressionVisitor).right.flatMap(e => e.vartype match {
      case Integer => Right(ExitStatement(e))
      case default => Left(SemanticError("Exit statement code should evaluate to value of type int"))
    })
  }

  override def visitRead(ctx: ReadContext): Either[CompilationError, Read] = {
    ctx.assignLhs().accept(AssignLhsVisitor).right.flatMap(lhs => lhs.vartype match {
      case Integer | Character => Right(Read(lhs))
      case default => Left(SemanticError("Read statement target must be of type int or char"))
    })
  }

  override def visitReturn(ctx: ReturnContext): Either[CompilationError, Statement] = {
    ctx.expression().accept(ExpressionVisitor).right map ReturnStatement
  }

  //TODO: Any way to get rid of this duplication?
  override def visitPrint(ctx: PrintContext): Either[CompilationError, Statement] = {
    for {
      expression: Expression <- ctx.expression().accept(ExpressionVisitor)
    } yield expression.vartype match {
      case Integer | Character => PrintStatement(expression)
      case vartype @ default   => SemanticError("Print statement expected expression of type int or char, got " + vartype)
    }
  }

  override def visitPrintLn(ctx: PrintLnContext): Either[CompilationError, Statement] = {
    for {
      expression: Expression <- ctx.expression().accept(ExpressionVisitor)
    } yield expression.vartype match {
      case Integer | Character => PrintLnStatement(expression)
      case vartype @ default   => SemanticError("PrintLn statement expected expression of type int or char, got " + vartype)
    }
  }

}

