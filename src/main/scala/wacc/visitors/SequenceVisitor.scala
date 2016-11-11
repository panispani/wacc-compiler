package wacc.visitors

import antlr.WACCParser.SequenceContext
import antlr.WACCParserBaseVisitor
import wacc.constructs._
import wacc.visitor._

import scala.collection.JavaConversions._

object SequenceVisitor extends WACCParserBaseVisitor[Either[CompilationError, Seq[Statement]]] {
  override def visitSequence(ctx: SequenceContext): Either[CompilationError, Seq[Statement]] = {
    for {
      seq <- sequenceOrLast(ctx.statement() map (_.accept(StatementVisitor))).right
    } yield seq
  }
}
