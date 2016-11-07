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

object FunctionVisitor extends WACCParserBaseVisitor[Function]  {

  override def visitFunction(ctx: FunctionContext): Function = {
    Function(
      ctx.IDENT().accept(IdentifierVisitor),
      ctx.parameterList().parameter().toList map (_.accept(ParamVisitor)),
      ctx.`type`().accept(TypeVisitor)
    )
  }
}
