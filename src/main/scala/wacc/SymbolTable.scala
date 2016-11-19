package wacc

import wacc.constructs._

import scala.collection.mutable

trait Reference extends Typed {
  val offset: Int
}
case class VariableReference(name: String, vartype: Type, offset: Int) extends Reference with Expression
case class FunctionReference(name: String, returnType: Type, arguments : Seq[VariableReference]) extends Reference {
  override val vartype: Type = returnType
  override val offset: Int = 0
}

case class SymbolTable(parent: Option[SymbolTable]) {

  private var currentOffset: Int = 0
  private var map: mutable.Map[String, Reference] = mutable.Map()

  def addFunctionArgument(ident: String, variable: VariableReference, reference: FunctionReference): Unit = {
    // should have negative offsets, lookup funciton symbol table
  }

  def addFunction(identifier: String, function: FunctionReference): Unit = {
    //TODO, add function symbol table
  }

  def addLocalVariable(identifier: String, vartype: Type) = {
    map += identifier -> VariableReference(identifier, vartype, currentOffset)
    currentOffset += variableSize(vartype)
  }

  def lookupTyped(identifier: String): Option[Reference]
    = map get identifier match {
    case Some(reference) => Some(reference)
    case None => None
  }

  // change to reference

  def lookupAllTyped(identifier: String): Option[Typed]
    = lookupTyped (identifier) match {
      case Some (ident) => Some (ident)
      case None => parent match {
        case None => None
        case Some (higherParent) => higherParent lookupAllTyped identifier
      }
    }

  def clear() = map.clear()

  private def variableSize(vartype: Type): Int = {
    vartype match {
      case Integer                   => 4
      case Boolean                   => 1
      case Character                 => 1
      case String                    => 4 //keep on heap, this is a pointer
      case ArrayType(elemtype: Type) => 4 //keep on heap
      case PairType(ftype, sType)    => 4 //keep on heap
    }
  }

}

object SymbolTable {
  def clearAll() = {
    globalTable.clear()
    currentTable = globalTable
  }

  def openScope() = {
    currentTable = SymbolTable(Some(currentTable))
  }

  def closeScope() = {
    currentTable = currentTable.parent.get
  }

  val globalTable: SymbolTable = SymbolTable(None)
  var currentTable: SymbolTable = globalTable

  def apply(): SymbolTable = currentTable
}