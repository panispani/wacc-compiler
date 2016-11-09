package wacc

import wacc.constructs.{Type, Typed}

import scala.collection.mutable

case class VariableReference(vartype: Type) extends Typed
case class FunctionReference(returnType: Type, argumentTypes : Seq[Type]) extends Typed {
  override val vartype: Type = returnType
}

case class SymbolTable(parent: Option[SymbolTable]) {

  var map: mutable.Map[String, Typed] = mutable.Map()

  def addTyped(Typed: String, identifier: Typed)
    = map += Typed -> identifier

  def lookup(Typed: String): Option[Typed]
    = map get Typed

  def lookupAll(Typed: String): Option[Typed]
    = lookup (Typed) match {
      case Some (identifier) => Some (identifier)
      case None => parent match {
        case None => None
        case Some (higherParent) => higherParent lookupAll Typed
      }
    }

  SymbolTable.currentTable = this
}

object SymbolTable {
  val globalTable: SymbolTable = SymbolTable(None)
  var currentTable: SymbolTable = globalTable

  def apply(): SymbolTable = currentTable
}