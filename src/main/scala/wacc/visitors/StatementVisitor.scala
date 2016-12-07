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
      if (compatibleTypes(varType, rhs.varType)) {
        SymbolTable().lookup(identifier) match {
          case None =>
            val variableReference = SymbolTable().addLocalVariable(identifier, varType)
            Right(DeclareStatement(varType, variableReference, rhs))
          case Some(_) => Left(SemanticError(
            "Identifier " + identifier + " already declared in current scope",
            ctx.start))
        }
      } else Left(
        SemanticError("Declare statement " + SemanticErrors.typeError("expression", rhs.varType, varType),
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
      case (l, r) if compatibleTypes(l.varType, r.varType)
        => Right(AssignStatement(l, r))
      case (l, r)
        => Left(SemanticError("Cannot assign " + r.varType + " to " + l.varType, ctx.start))
    }
  }

  override def visitSkip(ctx: SkipContext): Either[CompilationError, SkipStatement] = {
    Right(SkipStatement())
  }


  override def visitExit(ctx: ExitContext): Either[CompilationError, ExitStatement] = {
    ctx.expression().accept(ExpressionVisitor).right.flatMap(e => e.varType match {
      case Integer => Right(ExitStatement(e))
      case default => Left(SemanticError(
          "Exit statement " + SemanticErrors.typeError("expression", e.varType, Integer),
          ctx.start))
    })
  }

  override def visitRead(ctx: ReadContext): Either[CompilationError, ReadStatement] = {
    ctx.assignLhs().accept(AssignLhsVisitor).right.flatMap(lhs => lhs.varType match {
      case Integer | Character => Right(ReadStatement(lhs))
      case default => Left(SemanticError(
        "Read statement " + SemanticErrors.typeError("target", lhs.varType, Seq(Integer, Character)),
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

  override def visitWhile(ctx: WhileContext): Either[CompilationError, LoopStatement] = {
    constructLoopStatement(ctx.expression(), ctx.sequence())
  }

  override def visitDoWhile(ctx: DoWhileContext): Either[CompilationError, LoopStatement] = {
    constructLoopStatement(ctx.expression(), ctx.sequence(), doWhile = true)
  }

  override def visitFor(ctx: ForContext): Either[CompilationError, Statement] = {

    def syntaxErrorIfNotDeclaration(statement: Statement): Either[SyntaxError, DeclareStatement] = statement match {
      case init : DeclareStatement => Right(init)
      case _ => Left(SyntaxError("First statement of loop must be a declaration", ctx.start))
    }

    SymbolTable.openScope()

    for {
      init <- ctx.init.accept(StatementVisitor).right.flatMap(syntaxErrorIfNotDeclaration).right
      cond <- ctx.cond.accept(ExpressionVisitor).right
      step <- ctx.step.accept(StatementVisitor).right
      body <- ctx.body.accept(SequenceVisitor).right
    } yield ForLoopStatement(init, cond, step, body, SymbolTable.closeScope())
    // The symbol table is for the whole statement, not just the body
  }

  private def constructLoopStatement(expressionContext: ExpressionContext,
                                     sequenceContext: SequenceContext,
                                     doWhile: Boolean = false): Either[CompilationError, LoopStatement] = {

    def semanticErrorIfNotBoolean(expression: Expression): Either[SemanticError, Expression] = expression.varType match {
      case Boolean => Right(expression)
      case _ => Left(SemanticError(
        "Loop statement " + SemanticErrors.typeError("expression", expression.varType, Boolean),
        expressionContext.start))
    }

    SymbolTable.openScope()
    for {
      expression <- expressionContext.accept(ExpressionVisitor).right.flatMap(semanticErrorIfNotBoolean).right
      statements <- sequenceOrLast(sequenceContext.statement().toList map (s => s.accept(StatementVisitor))).right
    } yield LoopStatement(expression, statements, SymbolTable.closeScope(), doWhile = doWhile)
  }

  override def visitConditional(ctx: ConditionalContext): Either[CompilationError, Statement] = {
    ctx.conditionalStatement.accept(ConditionalVisitor)
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
      ctx.expression().accept(ExpressionVisitor).right flatMap (e => e.varType match {
        case ArrayType(_) | PairType(_, _)    => Right(FreeStatement(e))
        case default                          =>
          Left(SemanticError(
            "Free statement " + SemanticErrors.typeError("expression", e.varType.toString, Seq(ArrayType.toString, PairType.toString)),
            ctx.start))
      })
  }
}