package wacc.visitors

import antlr.WACCParser.ArrayElementContext
import antlr.WACCParserBaseVisitor
import wacc.SymbolTable
import wacc.constructs.{ArrayElement, Error, Function, Variable}
import scala.collection.JavaConversions._

import scala.util.{Failure, Success, Try}

object ArrayElementVisitor extends WACCParserBaseVisitor[ArrayElement] {

  override def visitArrayElement(ctx: ArrayElementContext): ArrayElement = {
    val identifier = ctx.IDENT().toString

    SymbolTable.currentTable.lookupAll(identifier) match {
      case Some(variable: Variable)  => ArrayElement(identifier, ctx.expression().toList map (_.accept(ExpressionVisitor)))
        /*
      case None                      => Error("Semantic", "Variable not declared")
      case default                   => Error("Semantic" , "Identifier is a function, not an array variable")
      */
    }
  }

}
