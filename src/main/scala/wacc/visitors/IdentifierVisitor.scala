package wacc.visitors

import antlr.WACCParser.AssignLhsIdentContext
import antlr.WACCParserBaseVisitor
import wacc.constructs.Identifier

object IdentifierVisitor extends WACCParserBaseVisitor[Identifier] {

}
