package wacc

import scala.collection.mutable

trait Symbol
case class SymbolTable(parent: Option[SymbolTable]) {

  var map: mutable.Map[String, Symbol] = mutable.Map()

  def addSymbol(symbol: String, identifier: Symbol)
    = map += symbol -> identifier

  def lookup(symbol: String): Option[Symbol]
    = map get symbol

  def lookupAll(symbol: String): Option[Symbol]
    = lookup (symbol) match {
      case Some (identifier) => Some (identifier)
      case None => parent match {
        case None => None
        case Some (higherParent) => higherParent lookupAll symbol
      }
    }

  SymbolTable.currentTable = this
}

object SymbolTable {
  val globalTable: SymbolTable = SymbolTable(None)
  var currentTable: SymbolTable = globalTable

  def apply(): SymbolTable = currentTable
}