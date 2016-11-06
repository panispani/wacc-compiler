package experimental

import antlr.WACCParser._
import antlr.WACCParserBaseVisitor

class SmallVisitor[T] extends WACCParserBaseVisitor[T] {

  var globalTable: SymbolTable = SymbolTable(None)
  var currentTable: SymbolTable = globalTable



  override def visitArrayType(ctx: ArrayTypeContext): T = super.visitArrayType(ctx)

  override def visitPairType(ctx: PairTypeContext): T = super.visitPairType(ctx)

  override def visitBaseType(ctx: BaseTypeContext): T = {
    //println("int is: " + ctx.INT())
    //println("bool is: " + ctx.BOOL())
    super.visitBaseType(ctx)
  }

  override def visitType(ctx: TypeContext): T = super.visitType(ctx)

  override def visitDeclare(ctx: DeclareContext): T = {
    val ident = globalTable.lookup(ctx.IDENT().toString)
    if (ident.isDefined && ident.get.getType().toString != "function") {
      println("Semantic Error: Identifier is re-defined")
      System.exit(200);
    }
    globalTable.addSymbol(ctx.IDENT().toString, new Identifier("variable"))
    super.visitDeclare(ctx)
  }

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

    val x = super.visitProgram(ctx)
    print(globalTable.lookup("a"))
    x
  }
}
