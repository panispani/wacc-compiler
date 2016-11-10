package wacc.visitors

import antlr.WACCParser.SequenceContext
import antlr.WACCParserBaseVisitor
import wacc.constructs._
import wacc.visitor._

import scala.collection.JavaConversions._

/**
  * Created by panayiotis on 10/11/16.
  */
object SequenceVisitor extends WACCParserBaseVisitor[Either[CompilationError, Seq[Statement]]] {
  override def visitSequence(ctx: SequenceContext): Either[CompilationError, Seq[Statement]] = {
    for {
      seq <- sequence(ctx.statement() map (_.accept(StatementVisitor))).right
    } yield seq
  }
}
