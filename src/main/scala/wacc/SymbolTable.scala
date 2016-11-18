package wacc

import wacc.constructs._

import scala.collection.mutable

case class VariableReference(name: String, vartype: Type) extends Typed
case class FunctionReference(name: String, returnType: Type, arguments : Seq[Param]) extends Typed {
  override val vartype: Type = returnType
}

case class SymbolTable(parent: Option[SymbolTable]) {

  var map: mutable.Map[String, MemoryObject] = mutable.Map()

  def addTyped(identifier: String, symbol: Typed)
    = map += identifier -> MemoryObject(symbol)

  def addMemoryObject(identifier: String, symbol: Typed, memoryLocation: MemoryLocation)
  = map += identifier -> MemoryObject(symbol, memoryLocation)

  def lookupTyped(identifier: String): Option[Typed]
    = map get identifier match {
    case Some(memoryObject) => Some(memoryObject.typed)
    case None => None
  }

  def lookupAllTyped(identifier: String): Option[Typed]
    = lookupTyped (identifier) match {
      case Some (ident) => Some (ident)
      case None => parent match {
        case None => None
        case Some (higherParent) => higherParent lookupAllTyped identifier
      }
    }

  def lookupMemoryObject(identifier: String): Option[MemoryObject]
  = map get identifier

  def lookupAllMemoryObject(identifier: String): Option[MemoryObject]
  = lookupMemoryObject (identifier) match {
    case Some (ident) => Some (ident)
    case None => parent match {
      case None => None
      case Some (higherParent) => higherParent lookupAllMemoryObject identifier
    }
  }

  def clear() = map.clear()

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