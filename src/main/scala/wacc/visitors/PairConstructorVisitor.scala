package wacc.visitors

import antlr.WACCParser.{PairConstructorContext, AssignRhsPairConstructorContext}
import antlr.WACCParserBaseVisitor
import wacc.constructs.{CompilationError, PairConstructor}

object PairConstructorVisitor extends WACCParserBaseVisitor[Either[CompilationError, PairConstructor]] {

  override def visitPairConstructor(ctx: PairConstructorContext): Either[CompilationError, PairConstructor] = {

    for {
      exp1 <- ctx.expression1.accept(ExpressionVisitor).right
      exp2 <- ctx.expression2.accept(ExpressionVisitor).right
    } yield PairConstructor(exp1, exp2)

  }
}
