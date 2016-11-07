package experimental

import antlr.WACCParser._
import antlr.WACCParserBaseVisitor
import scala.collection.immutable.List

class SmallVisitor extends WACCParserBaseVisitor[Node] {

  var globalTable: SymbolTable = SymbolTable(None)
  var currentTable: SymbolTable = globalTable

  private def notInBounds(n: String): Boolean = {
    val num = BigInt(n)
    return num > 2147483647 || num < -2147483648
  }

  override def visitIntLiteral(ctx: IntLiteralContext): Node = {
    if(notInBounds(ctx.NUMBER().toString)) {
      println("Runtime Error: Number is not in integer bounds")
    }
    super.visitIntLiteral(ctx)
  }

  override def visitArrayType(ctx: ArrayTypeContext): Node = super.visitArrayType(ctx)

  override def visitPairType(ctx: PairTypeContext): Node = super.visitPairType(ctx)

  override def visitBaseType(ctx: BaseTypeContext): Node = {
    if (ctx.INT() != null) {
      //how? what is currrent variable
    }
    //println("int is: " + ctx.INT())
    //println("bool is: " + ctx.BOOL())
    super.visitBaseType(ctx)
  }

  override def visitType(ctx: TypeContext): Node = super.visitType(ctx)

  override def visitDeclare(ctx: DeclareContext): Node = {
    val ident = currentTable.lookup(ctx.IDENT().toString)

    if (ident.isDefined && ident.get.getType().toString != "function") {
      println("Semantic Error: Identifier is re-defined")
      System.exit(200)
    }
    currentTable.addSymbol(ctx.IDENT().toString, new Identifier("variable"))
    super.visitDeclare(ctx)
  }

  override def visitAssign(ctx: AssignContext): Node = {
    super.visitAssign(ctx)
  }

  /*  statements can't be empty
   *
   */
  override def visitProgram(ctx: ProgramContext): Node = {
    val childCount = ctx.getChildCount
    val functions =
      for (i <- List.range(0, childCount - 1)) yield visit(ctx.getChild(i)).asInstanceOf[FunctionNode]
    val stmt = visit(ctx.getChild(childCount - 1)).asInstanceOf[StatementNode]
    ProgramNode(functions, stmt)
  }
}
