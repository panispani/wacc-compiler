package wacc

import wacc.constructs._

import scala.collection.mutable

trait Reference extends Typed {
  val name: String
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
    map += identifier -> function
  }

  def addLocalVariable(identifier: String, vartype: Type): VariableReference = {
    val variableReference = VariableReference(identifier, vartype, currentOffset)
    map += identifier -> variableReference
    currentOffset += vartype.size
    variableReference
  }

  def lookup(identifier: String): Option[Reference]
    = map get identifier match {
    case Some(reference) => Some(reference)
    case None => None
  }

  def lookupDeep(identifier: String): Option[Reference]
  = lookup(identifier) match {
    case Some(ident) => Some(ident)
    case None => parent flatMap (_.lookupWithOffsetAccumulator(identifier, 0))
  }

  /**
    * Compute the offset of a variable relative to the scope which initiates the lookup
    *  ---
    * |x:0|
    * |y:1|
    * |z:5| <- parent <- ---  <-- lookup relative to here
    *  --- size=9       |a:0|
    *                    ---
    *
    * lookup(x).offset = -9
    * lookup(y).offset = -8
    * lookup(z).offset = -4
    * lookup(a).offset = 0
    * */
  private def lookupWithOffsetAccumulator(identifier: String, offset: Int): Option[Reference]
  = lookup(identifier) match {
    // Base case does the offset computation
    case Some(ref) =>
      Some(VariableReference(identifier, ref.vartype, ref.offset - currentOffset - offset))
    // Recursive case just accumulates the offset
    case None => parent flatMap (_.lookupWithOffsetAccumulator(identifier, offset + currentOffset))
  }

  def clear() = {
    map.clear()
    currentOffset = 0
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