package experimental

import antlr.WACCParser._
import antlr.WACCParserBaseVisitor

import scala.collection.JavaConversions._

object ProgramVisitor extends WACCParserBaseVisitor[Program] {
  override def visitProgram(ctx: ProgramContext): Program = {
    val functions = ctx.function().toList map (_.accept(FunctionVisitor))
    val statement = ctx.statement().accept(StatementVisitor)

    Program(functions, statement)
  }
}

trait SemanticallyCheckable {
  def semanticCheck() = System.out.println("checking")

  semanticCheck()
}

case class Identifier(name: String)
trait BaseType
case class PrimitiveType(identifier: Identifier) extends BaseType

object String extends PrimitiveType(Identifier("string"))
object Integer extends PrimitiveType(Identifier("int"))
object Boolean extends PrimitiveType(Identifier("bool"))
object Character extends PrimitiveType(Identifier("char"))

case class Program(functions: Seq[Function], statement: Statement)
case class Variable(identifier: Identifier, vartype: BaseType)
case class Statement()
case class FunctionParam(variable: Variable)
case class ArrayType(elemtype: BaseType) extends BaseType
case class PairType(firstType: BaseType, secondType: BaseType) extends BaseType
case class ErasedPair() extends BaseType
case class Function(identifier: Identifier, params: List[FunctionParam], returns: BaseType) extends SemanticallyCheckable {

  override def semanticCheck(): Unit = System.out.println("overriden")
}

object ParamVisitor extends WACCParserBaseVisitor[FunctionParam] {

}

object IdentifierVisitor extends WACCParserBaseVisitor[Identifier] {

}

object StatementVisitor extends WACCParserBaseVisitor[Statement] {
  override def visitAssign(ctx: AssignContext): Statement = {
    Statement()
  }
}

object TypeVisitor extends WACCParserBaseVisitor[BaseType] {
  override def visitPrimitiveType(ctx: PrimitiveTypeContext): BaseType =
    PrimitiveType(Identifier(ctx.toString))

  override def visitPairType(ctx: PairTypeContext): BaseType = {
    val firstType = ctx.pairElementType(0).accept(TypeVisitor)
    val secondType = ctx.pairElementType(0).accept(TypeVisitor)
    PairType(firstType, secondType)
  }

  //TODO: Make this functional
  override def visitArrayType(ctx: ArrayTypeContext): BaseType = {
    val elemtype = ctx.notNestedArrayType().accept(TypeVisitor)
    var arrayType: ArrayType = ArrayType(elemtype)

    for (i <- 1 to ctx.RB().size()) {
      arrayType = ArrayType(arrayType)
    }

    arrayType
  }

  override def visitErasedPair(ctx: ErasedPairContext): BaseType = {
    ErasedPair()
  }
}

object FunctionVisitor extends WACCParserBaseVisitor[Function] {

  override def visitFunction(ctx: FunctionContext): Function = {
    Function(
      ctx.IDENT().accept(IdentifierVisitor),
      ctx.parameterList().parameter().toList map (_.accept(ParamVisitor)),
      ctx.`type`().accept(TypeVisitor)
    )
  }
}

/*
TODO: This is old code. Sorry. Fix it. New patterns. Functional stuff. Have fun <3

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


  // Next to impelement
  def thereIsReturn(stmt: StatementNode): Boolean = {
    true
  }

  def returnIsLastStmt(stmt: StatementNode): Boolean = {
    true
  }

  def returnTypesSameWithFunction(stmt: StatementNode, returnType: Type): Boolean = {
    true
  }

  def functionParamsAreNotDuplicated(paramList: List[ParamNode]): Boolean = {
    true
  }

  def functionIsNotRedefined(fname: Ident): Boolean = {
    true
  }

  // function must have a return statement - syntax error
  // return statement is the last statement is the function - semantic error
  // return values(s) are the same(type) with the function type - semantic error
  // function parameters are not duplicated - semantic error
  //-----------------------------------------------------------------------------
  // function parameters are nor reserved keywords ? should be fine by grammar - to check
  // case class FunctionNode(retType: Type, fname: Ident, paramList: List[ParamNode], stmt: StatementNode) extends Node
   override def visitFunction(ctx: FunctionContext): Node = {
    val childCount = ctx.getChildCount
    val retType = visit(ctx.getChild(0)).asInstanceOf[Type]
    val fname = visit(ctx.getChild(1)).asInstanceOf[Ident]
    val paramList = visit(ctx.getChild(2)).asInstanceOf[List[ParamNode]]
    val stmt = visit(ctx.getChild(3)).asInstanceOf[StatementNode]
    if (thereIsReturn(stmt)
      && returnIsLastStmt(stmt)
      && returnTypesSameWithFunction(stmt, retType)
      && functionParamsAreNotDuplicated(paramList)
      && functionIsNotRedefined(fname)) {
        FunctionNode(retType, fname, paramList, stmt)
    }
    else {
      println("Semantic Error: ")
      System.exit(200);
    }
  }

  override def visitProgram(ctx: ProgramContext): Node = {
    val childCount = ctx.getChildCount
    val functions =
      for (i <- List.range(0, childCount - 2)) yield visit(ctx.getChild(i)).asInstanceOf[FunctionNode]
    val stmt = visit(ctx.getChild(childCount - 1)).asInstanceOf[StatementNode]
    ProgramNode(functions, stmt)
  }
*/

