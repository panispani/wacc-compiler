package wacc.visitors

import antlr.WACCParser.UnaryOperatorContext
import antlr.{WACCParser, WACCParserBaseVisitor}
import wacc.constructs.UnaryOperatorExpr

/**
  * Created by panayiotis on 08/11/16.
  */
object UnaryOperatorVisitor extends WACCParserBaseVisitor[UnaryOperatorExpr] {

}