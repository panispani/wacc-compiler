package experimental

import antlr.WACCParser.{ProgramContext, AssignLhsContext, AssignRhsContext, AssignContext}
import antlr.WACCParserBaseVisitor

class SmallVisitor[T] extends WACCParserBaseVisitor[T] {

  var globalTable: SymbolTable = SymbolTable(None)
  var currentTable: SymbolTable = globalTable

  override def visitAssignLhs(ctx: AssignLhsContext): T = super.visitAssignLhs(ctx)

  override def visitAssignRhs(ctx: AssignRhsContext): T = super.visitAssignRhs(ctx)

  override def visitAssign(ctx: AssignContext): T = {
    super.visitAssign(ctx)
  }

  override def visitProgram(ctx: ProgramContext): T = {
    globalTable.addSymbol("int", BaseType(min = -1000000, max = 10000000))
    globalTable.addSymbol("bool", BaseType(min = 0, max = 1))
    globalTable.addSymbol("char", BaseType(min = 0, max = 255))
    globalTable.addSymbol("string", BaseType(min = -1000000, max = 1000000))

    super.visitProgram(ctx)
  }
}
