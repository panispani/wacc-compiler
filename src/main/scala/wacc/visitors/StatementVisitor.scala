package wacc.visitors

import antlr.WACCParser._
import antlr.WACCParserBaseVisitor
import wacc.{SymbolTable, VariableReference}
import wacc.constructs._

import scala.collection.JavaConversions._
import wacc.visitor._

object StatementVisitor extends WACCParserBaseVisitor[Either[CompilationError, Statement]] {

  override def visitSkip(ctx: SkipContext): Either[CompilationError, SkipStatement] = {
    Right(SkipStatement())
  }

  override def visitDeclare(ctx: DeclareContext): Either[CompilationError, DeclareStatement] = {
    val vartype = ctx.`type`().accept(TypeVisitor)
    val identifier = ctx.IDENT().toString

    ctx.assignRhs().accept(AssignRhsVisitor).right.flatMap(rhs => rhs.vartype == vartype match {
      case true  =>
        SymbolTable.currentTable.addTyped(identifier, VariableReference(vartype))
        Right(DeclareStatement(vartype, identifier, rhs))
      case false => Left(SemanticError("Expected type " + vartype + ", got " + rhs.vartype))
    })
  }

  override def visitExit(ctx: ExitContext): Either[CompilationError, ExitStatement] = {
    ctx.expression().accept(ExpressionVisitor).right.flatMap(e => e.vartype match {
      case Integer => Right(ExitStatement(e))
      case default => Left(SemanticError("Exit statement code should evaluate to value of type int"))
    })
  }

  override def visitRead(ctx: ReadContext): Either[CompilationError, ReadStatement] = {
    ctx.assignLhs().accept(AssignLhsVisitor).right.flatMap(lhs => lhs.vartype match {
      case Integer | Character => Right(ReadStatement(lhs))
      case default => Left(SemanticError("Read statement target must be of type int or char"))
    })
  }

  override def visitReturn(ctx: ReturnContext): Either[CompilationError, ReturnStatement] = {
    ctx.expression().accept(ExpressionVisitor).right map ReturnStatement
  }

  //TODO: Any way to get rid of this duplication?
  override def visitPrint(ctx: PrintContext): Either[CompilationError, PrintStatement] = {
    ctx.expression().accept(ExpressionVisitor).right flatMap (e => e.vartype match {
      case Integer | Character => Right(PrintStatement(e))
      case vartype @ default   => Left(SemanticError("PrintLn statement expected expression of type int or char, got " + vartype))
    })
  }

  override def visitPrintLn(ctx: PrintLnContext): Either[CompilationError, PrintLnStatement] = {
    ctx.expression().accept(ExpressionVisitor).right flatMap (e => e.vartype match {
      case Integer | Character => Right(PrintLnStatement(e))
      case vartype @ default   => Left(SemanticError("PrintLn statement expected expression of type int or char, got " + vartype))
    })
  }

  override def visitConditional(ctx: ConditionalContext): Either[CompilationError, ConditionalStatement] = {
    val tuple = for {
      expression <- ctx.expression().accept(ExpressionVisitor).right
      trueStatements <- sequence(ctx.trueSequence.statement().toList map (s => s.accept(StatementVisitor))).right
      falseStatements <- sequence(ctx.falseSequence.statement().toList map (s => s.accept(StatementVisitor))).right
    } yield Tuple3(expression, trueStatements, falseStatements)

    tuple match {
      case Left(error)                                                => Left(error)
      case Right(Tuple3(expression, trueStatements, falseStatements)) => expression.vartype match {
        case Boolean => Right(ConditionalStatement(expression, trueStatements, falseStatements))
        case default => Left(SemanticError("Conditional statement expected expression of type bool, got " + expression.vartype))
      }
    }
  }

  override def visitLoop(ctx: LoopContext): Either[CompilationError, Statement] = {
    val pair = for {
      expression     <- ctx.expression().accept(ExpressionVisitor).right
      statements     <- sequence(ctx.sequence().statement().toList map (s => s.accept(StatementVisitor))).right
    } yield (expression, statements)

    pair match {
      case Left(error)                         => Left(error)
      case Right((expression, statements)) => expression.vartype match {
        case Boolean => Right(Loop(expression, statements))
        case default => Left(SemanticError("Loop statement expected expression of type bool, got " + expression.vartype))
      }
    }
  }

  override def visitScope(ctx: ScopeContext): Either[CompilationError, Statement] = {
    SymbolTable.openScope()
    val stmt = ctx.sequence().accept(SequenceVisitor).right
    SymbolTable.closeScope()
    for (
      s <- stmt
    ) yield ScopeStatement(s)
  }

}

