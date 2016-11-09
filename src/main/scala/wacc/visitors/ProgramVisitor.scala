package wacc.visitors

import antlr.WACCParser.ProgramContext
import antlr.WACCParserBaseVisitor
import wacc.constructs.{CompilationError, Program}

import scala.collection.JavaConversions._

object ProgramVisitor extends WACCParserBaseVisitor[Either[CompilationError, Program]] {
  override def visitProgram(ctx: ProgramContext): Either[CompilationError, Program] = {

    def sequence[A, B](s: Seq[Either[A, B]]): Either[A, Seq[B]] =
      s.foldRight(Right(Nil): Either[A, List[B]]) {
        (e, acc) => for (xs <- acc.right; x <- e.right) yield x :: xs
      }

    for {
      functions <- sequence(ctx.function().toList map (e => e.accept(FunctionVisitor))).right
      statement <- ctx.statement().accept(StatementVisitor).right
    } yield Program(functions, statement)
  }
}
