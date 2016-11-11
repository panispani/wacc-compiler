package wacc.visitors

import antlr.WACCParser.PairElementContext
import antlr.WACCParserBaseVisitor
import wacc.constructs._
import wacc.util.SemanticErrors

object PairElementVisitor extends WACCParserBaseVisitor[Either[CompilationError, PairElement]] {
  override def visitPairElement(ctx: PairElementContext): Either[CompilationError, PairElement] = {
    ctx.expression().accept(ExpressionVisitor).right flatMap (e => e.vartype match {
      case PairType(type1, type2)       =>
        val selector = Selector(ctx.selector.getText)
        val pairElementType = selector match {
          case FirstSelector => type1
          case SecondSelector => type2
        }
        Right(PairElement(selector, e, pairElementType))

      case default => Left(SemanticError(
        "Pair element " + SemanticErrors.typeError("expression", e.vartype.toString, PairType.toString()),
        ctx.start))
    })
  }

}
