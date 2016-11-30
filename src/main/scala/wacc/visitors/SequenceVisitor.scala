package wacc.visitors

import antlr.WACCParser.SequenceContext
import antlr.WACCParserBaseVisitor
import wacc.SymbolTable
import wacc.constructs.{CompilationError, _}

import scala.collection.JavaConversions._

object SequenceVisitor extends WACCParserBaseVisitor[Either[CompilationError, Seq[Statement]]] {
  override def visitSequence(ctx: SequenceContext): Either[CompilationError, Seq[Statement]] = {
    sequenceOrLast(ctx.statement() map (_.accept(StatementVisitor)))
  }

  def visitScopedSequence(ctx: SequenceContext): Either[CompilationError, ScopeStatement] = {
    SymbolTable.openScope()
    val statements = sequenceOrLast(ctx.statement().map(_.accept(StatementVisitor)))
    val scope = statements.right.map(ScopeStatement(_, SymbolTable()))
    SymbolTable.closeScope()

    scope
  }
}
