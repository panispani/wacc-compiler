package wacc

import wacc.constructs.{Type, Typed}

import scala.collection.mutable

case class VariableReference(vartype: Type) extends Typed
case class FunctionReference(returnType: Type, argumentTypes : Seq[Type]) extends Typed {
  override val vartype: Type = returnType
}

case class SymbolTable(parent: Option[SymbolTable]) {

  var map: mutable.Map[String, Typed] = mutable.Map()

  def addTyped(identifier: String, symbol: Typed)
    = map += identifier -> symbol

  def lookup(identifier: String): Option[Typed]
    = map get identifier

  def lookupAll(identifier: String): Option[Typed]
    = lookup (identifier) match {
      case Some (ident) => Some (ident)
      case None => parent match {
        case None => None
        case Some (higherParent) => higherParent lookupAll identifier
      }
    }

  def clear() = map.clear()

}

object SymbolTable {
  def clearAll() = {
    globalTable.clear()
    currentTable.clear()
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