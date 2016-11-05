package experimental

import scala.collection.mutable

class SymbolTable(parent: SymbolTable) {

  var map: mutable.Map[String, Identifier] = mutable.Map()

  def addSymbol(symbol: String, identifier: Identifier)
    = map += symbol -> identifier

  def lookup(symbol: String): Option[Identifier]
    = map get symbol

  def lookupAll(symbol: String): Identifier
    = map get symbol match {
      case Some (identifier) => identifier
      case None              => parent lookupAll symbol
    }

}
