package wacc.visitors

import antlr.WACCParser.{BinaryOperatorContext, VariableReferenceContext}
import antlr.WACCParserBaseVisitor
import wacc.{FunctionReference, SymbolTable, VariableReference}
import wacc.constructs._

object VariableReferenceVisitor extends WACCParserBaseVisitor[Either[CompilationError, VariableReferenceExpression]] {

}
