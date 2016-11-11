package wacc.visitors

import antlr.WACCParser._
import antlr.WACCParserBaseVisitor
import wacc.constructs._
import wacc.util.SemanticErrors
import wacc.visitor._
import wacc.{SymbolTable, VariableReference}

import scala.collection.JavaConversions._

object StatementVisitor extends WACCParserBaseVisitor[Either[CompilationError, Statement]] {

  override def visitDeclare(ctx: DeclareContext): Either[CompilationError, DeclareStatement] = {
    val varType = ctx.`type`().accept(TypeVisitor)
    val identifier = ctx.IDENT().toString

    ctx.assignRhs().accept(AssignRhsVisitor).right.flatMap(rhs => {
      if (compatibleTypes(varType, rhs.vartype)) {

        SymbolTable.currentTable.lookup(identifier) match {
          case None => SymbolTable.currentTable.addTyped(identifier, VariableReference(identifier, varType))
                       Right(DeclareStatement(varType, identifier, rhs))

          case Some(_) => Left(SemanticError("Identifier " + identifier + " already declared in current scope"))
        }
      } else Left(SemanticError("Declare statement " + SemanticErrors.typeError("expression", rhs.vartype, varType)))
    }
    )
  }

  override def visitAssign(ctx: AssignContext): Either[CompilationError, Statement] = {

    val pair = for {
      rhs <- ctx.assignRhs().accept(AssignRhsVisitor).right
      lhs <- ctx.assignLhs().accept(AssignLhsVisitor).right
    } yield (lhs, rhs)

    pair.right flatMap {
      case (l, r) if compatibleTypes(l.vartype, r.vartype)
        => Right(AssignStatement(l, r))
      case (l, r)
        => Left(SemanticError("Cannot assign " + r.vartype + " to " + l.vartype))
    }
  }

  override def visitSkip(ctx: SkipContext): Either[CompilationError, SkipStatement] = {
    Right(SkipStatement())
  }


  override def visitExit(ctx: ExitContext): Either[CompilationError, ExitStatement] = {
    ctx.expression().accept(ExpressionVisitor).right.flatMap(e => e.vartype match {
      case Integer => Right(ExitStatement(e))
      case default => Left(SemanticError("Exit statement " + SemanticErrors.typeError("expression", e.vartype, Integer)))
    })
  }

  override def visitRead(ctx: ReadContext): Either[CompilationError, ReadStatement] = {
    ctx.assignLhs().accept(AssignLhsVisitor).right.flatMap(lhs => lhs.vartype match {
      case Integer | Character | String => Right(ReadStatement(lhs))
      case default => Left(SemanticError("Read statement " + SemanticErrors.typeError("target", lhs.vartype, Seq(Integer, Character))))
    })
  }

  override def visitReturn(ctx: ReturnContext): Either[CompilationError, ReturnStatement] = {
    ctx.expression().accept(ExpressionVisitor).right map ReturnStatement
  }

  //TODO: Any way to get rid of this duplication?
  override def visitPrint(ctx: PrintContext): Either[CompilationError, PrintStatement] = {
    ctx.expression().accept(ExpressionVisitor).right flatMap (e => Right(PrintStatement(e)))
  }

  override def visitPrintLn(ctx: PrintLnContext): Either[CompilationError, PrintLnStatement] = {
    ctx.expression().accept(ExpressionVisitor).right flatMap (e => Right(PrintLnStatement(e)))
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
        case default => Left(SemanticError("Conditional statement " + SemanticErrors.typeError("expression", expression.vartype, Boolean)))
      }
    }
  }

  override def visitLoop(ctx: LoopContext): Either[CompilationError, LoopStatement] = {
    SymbolTable.openScope()
    val pair = for {
      expression     <- ctx.expression().accept(ExpressionVisitor).right
      statements     <- sequence(ctx.sequence().statement().toList map (s => s.accept(StatementVisitor))).right
    } yield (expression, statements)
    SymbolTable.closeScope()

    pair match {
      case Left(error)                         => Left(error)
      case Right((expression, statements))     => expression.vartype match {
        case Boolean => Right(LoopStatement(expression, statements))
        case default => Left(SemanticError("Loop statement " + SemanticErrors.typeError("expression", expression.vartype, Boolean)))
      }
    }
  }

  override def visitScope(ctx: ScopeContext): Either[CompilationError, ScopeStatement] = {
    SymbolTable.openScope()
    val stmt = ctx.sequence().accept(SequenceVisitor).right
    SymbolTable.closeScope()
    for (
      s <- stmt
    ) yield ScopeStatement(s)
  }

  override def visitFree(ctx: FreeContext): Either[CompilationError, FreeStatement] = {
      ctx.expression().accept(ExpressionVisitor).right flatMap (e => e.vartype match {
        case ArrayType(_) | PairType(_, _)    => Right(FreeStatement(e))
        case default                          =>
          Left(SemanticError("Free statement " + SemanticErrors.typeError("expression", e.vartype.toString, Seq(ArrayType.toString, PairType.toString))))
      })
  }
}

