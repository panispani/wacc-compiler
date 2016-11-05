package experimental

import antlr.WACCParser.{ProgramContext, AssignLhsContext, AssignRhsContext, AssignContext}
import antlr.WACCParserBaseVisitor

class SmallVisitor[T] extends WACCParserBaseVisitor[T] {

  var globalTable: SymbolTable = SymbolTable(Unit)
  var currentTable: SymbolTable = Unit

  override def visitAssignLhs(ctx: AssignLhsContext): T = super.visitAssignLhs(ctx)

  override def visitAssignRhs(ctx: AssignRhsContext): T = super.visitAssignRhs(ctx)

  override def visitAssign(ctx: AssignContext): T = {
    val
    super.visitAssign(ctx)
  }

  override def visitProgram(ctx: ProgramContext): T = {
    globalTable.addSymbol("int", new BaseType(min = -1000000, max = 10000000))
    globalTable.addSymbol("bool", new BaseType(min = 0, max = 1))
    globalTable.addSymbol("char", new BaseType(min = 0, max = 255))
    globalTable.addSymbol("string", new BaseType(min = -1000000, max = 1000000))

    super.visitProgram(ctx)
  }
}
