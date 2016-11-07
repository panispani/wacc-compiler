package wacc

import wacc.constructs.Identifier

import scala.collection.mutable

case class SymbolTable(parent: Option[SymbolTable]) {

  var map: mutable.Map[String, Identifier] = mutable.Map()

  def addSymbol(symbol: String, identifier: Identifier)
    = map += symbol -> identifier

  def lookup(symbol: String): Option[Identifier]
    = map get symbol

  def lookupAll(symbol: String): Option[Identifier]
    = lookup (symbol) match {
      case Some (identifier) => Some (identifier)
      case None => parent match {
        case None => None
        case Some (higherParent) => higherParent lookupAll symbol
      }
    }

}
