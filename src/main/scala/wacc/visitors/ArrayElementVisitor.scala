package wacc.visitors

import antlr.WACCParser.ArrayElementContext
import antlr.WACCParserBaseVisitor
import wacc.SymbolTable
import wacc.constructs.{ArrayElement, Error, Function, Variable}
import scala.collection.JavaConversions._

import scala.util.{Failure, Success, Try}

object ArrayElementVisitor extends WACCParserBaseVisitor[Try[ArrayElement]] {

  override def visitArrayElement(ctx: ArrayElementContext): Try[ArrayElement] = {
    val identifier = ctx.IDENT().toString

    SymbolTable.currentTable.lookupAll(identifier) match {
      case Some(variable: Variable)  => Success(ArrayElement(identifier, ctx.expression().toList map (_.accept(ExpressionVisitor))))
      case None                      => Failure(Error("Semantic", "Variable not declared"))
      case default                   => Failure(Error("Semantic" , "Identifier is a function, not an array variable"))
    }
  }

}
