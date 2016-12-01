package wacc.visitors

import antlr.WACCParser._
import antlr.WACCParserBaseVisitor
import wacc.SymbolTable
import wacc.constructs._
import wacc.util.SemanticErrors

import scala.collection.JavaConversions._

object StatementVisitor extends WACCParserBaseVisitor[Either[CompilationError, Statement]] {

  override def visitDeclare(ctx: DeclareContext): Either[CompilationError, DeclareStatement] = {
    val varType = ctx.`type`().accept(TypeVisitor)
    val identifier = ctx.IDENT().toString

    ctx.assignRhs().accept(AssignRhsVisitor).right.flatMap(rhs => {
      if (compatibleTypes(varType, rhs.vartype)) {
        SymbolTable().lookup(identifier) match {
          case None =>
            val variableReference = SymbolTable().addLocalVariable(identifier, varType)
            Right(DeclareStatement(varType, variableReference, rhs))
          case Some(_) => Left(SemanticError(
            "Identifier " + identifier + " already declared in current scope",
            ctx.start))
        }
      } else Left(
        SemanticError("Declare statement " + SemanticErrors.typeError("expression", rhs.vartype, varType),
          ctx.start))
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
        => Left(SemanticError("Cannot assign " + r.vartype + " to " + l.vartype, ctx.start))
    }
  }

  override def visitSkip(ctx: SkipContext): Either[CompilationError, SkipStatement] = {
    Right(SkipStatement())
  }


  override def visitExit(ctx: ExitContext): Either[CompilationError, ExitStatement] = {
    ctx.expression().accept(ExpressionVisitor).right.flatMap(e => e.vartype match {
      case Integer => Right(ExitStatement(e))
      case default => Left(SemanticError(
          "Exit statement " + SemanticErrors.typeError("expression", e.vartype, Integer),
          ctx.start))
    })
  }

  override def visitRead(ctx: ReadContext): Either[CompilationError, ReadStatement] = {
    ctx.assignLhs().accept(AssignLhsVisitor).right.flatMap(lhs => lhs.vartype match {
      case Integer | Character => Right(ReadStatement(lhs))
      case default => Left(SemanticError(
        "Read statement " + SemanticErrors.typeError("target", lhs.vartype, Seq(Integer, Character)),
        ctx.start))
    })
  }

  override def visitReturn(ctx: ReturnContext): Either[CompilationError, ReturnStatement] = {
    ctx.expression().accept(ExpressionVisitor).right map ReturnStatement
  }

  override def visitPrint(ctx: PrintContext): Either[CompilationError, PrintStatement] = {
    ctx.expression().accept(ExpressionVisitor).right flatMap (e => Right(PrintStatement(e)))
  }

  override def visitPrintLn(ctx: PrintLnContext): Either[CompilationError, PrintLnStatement] = {
    ctx.expression().accept(ExpressionVisitor).right flatMap (e => Right(PrintLnStatement(e)))
  }

  override def visitConditional(ctx: ConditionalContext): Either[CompilationError, ConditionalStatement] = {

    val tuple = for {
      expression <- ctx.expression().accept(ExpressionVisitor).right
      _ <- Right(SymbolTable.openScope()).right
      trueStatements <- sequenceOrLast(ctx.trueSequence.statement().toList map (s => s.accept(StatementVisitor))).right
      trueTable <- Right(SymbolTable.closeScope()).right
      _ <- Right(SymbolTable.openScope()).right
      falseStatements <- sequenceOrLast(ctx.falseSequence.statement().toList map (s => s.accept(StatementVisitor))).right
    } yield (expression, trueStatements, falseStatements, trueTable)
    val falseTable = SymbolTable.closeScope()
    val conditional = tuple match {
      case Left(error)                                                => Left(error)
      case Right((expression, trueStatements, falseStatements, trueTable)) => expression.vartype match {
        case Boolean => Right(ConditionalStatement(expression, ScopeStatement(trueStatements, trueTable), ScopeStatement(falseStatements, falseTable)))
        case default => Left(
          SemanticError(
            "Conditional statement " + SemanticErrors.typeError("expression", expression.vartype, Boolean),
            ctx.start))
      }
    }


    conditional
  }


  override def visitWhile(ctx: WhileContext): Either[CompilationError, LoopStatement] = {
    constructLoopStatement(ctx.expression(), ctx.sequence())
  }

  override def visitDoWhile(ctx: DoWhileContext): Either[CompilationError, Statement] = {
    constructLoopStatement(ctx.expression(), ctx.sequence(), doWhile = true)
  }

  override def visitFor(ctx: ForContext): Either[CompilationError, Statement] = {
    ctx.body.statement().add(0, ctx.init)
    val loopScope = SequenceVisitor.visitScopedSequence(ctx.sequence())

    /** cond and step are translated out of the loop scope but should have
      * access to the variable reference from init which is declared in the
      * child scope
      * */
    loopScope.right.flatMap(loop => loop.statements.head match {
      case init @ DeclareStatement(_, ref, _) =>
        // inject init in parent scope temporarily
        val oldRef = SymbolTable().injectReference(ref)
        for {
          cond <- ctx.cond.accept(ExpressionVisitor).right
          step <- ctx.step.accept(StatementVisitor).right
          // restore original reference or remove injected
          _    <- Right(oldRef match {
            case Some(old) => SymbolTable().injectReference(old)
            case None => SymbolTable().removeReference(ref.name)
          }).right
        } yield ForLoopStatement(init, cond, step, loop.statements.tail)
      case _ => Left(SyntaxError("First statement of for loop must be a declaration", ctx.start))
    })
  }

  private def constructLoopStatement(expressionContext: ExpressionContext, sequenceContext: SequenceContext, doWhile: Boolean = false): Either[CompilationError, LoopStatement] with Product with Serializable = {
    SymbolTable.openScope()
    val pair = for {
      expression <- expressionContext.accept(ExpressionVisitor).right
      statements <- sequenceOrLast(sequenceContext.statement().toList map (s => s.accept(StatementVisitor))).right
    } yield (expression, statements)

    val loop = pair match {
      case Left(error) => Left(error)
      case Right((expression, statements)) => expression.vartype match {
        case Boolean => Right(LoopStatement(expression, statements, SymbolTable(), doWhile))
        case default => Left(SemanticError(
          "Loop statement " + SemanticErrors.typeError("expression", expression.vartype, Boolean),
          expressionContext.start))
      }
    }

    SymbolTable.closeScope()
    loop
  }



  override def visitScope(ctx: ScopeContext): Either[CompilationError, ScopeStatement] = {
    SymbolTable.openScope()

    val scope = for {
      block <- sequenceOrLast(ctx.sequence().statement().toList map(_.accept(StatementVisitor))).right
    } yield ScopeStatement(block, SymbolTable())

    SymbolTable.closeScope()

    scope
  }

  override def visitFree(ctx: FreeContext): Either[CompilationError, FreeStatement] = {
      ctx.expression().accept(ExpressionVisitor).right flatMap (e => e.vartype match {
        case ArrayType(_) | PairType(_, _)    => Right(FreeStatement(e))
        case default                          =>
          Left(SemanticError(
            "Free statement " + SemanticErrors.typeError("expression", e.vartype.toString, Seq(ArrayType.toString, PairType.toString)),
            ctx.start))
      })
  }
}