package wacc

import wacc.constructs._

import scala.collection.mutable

trait Reference extends Typed {
  val name: String
  val offset: Int
}
case class VariableReference(name: String, vartype: Type, offset: Int) extends Reference
case class FunctionReference(name: String, returnType: Type, arguments : Seq[VariableReference]) extends Reference {
  override val vartype: Type = returnType
  override val offset: Int = 0
}

case class SymbolTable(parent: Option[SymbolTable]) {

  private var currentOffset: Int = 0
  private var argOffset: Int = 4 // PC is at 0
  private var map: mutable.Map[String, Reference] = mutable.Map()

  def addFunctionArgument(arg: VariableReference): Unit = {
    map += arg.name -> VariableReference(arg.name, arg.vartype, argOffset)
    argOffset += arg.vartype.size
  }

  def addLocalVariable(identifier: String, vartype: Type): VariableReference = {
    val variableReference = VariableReference(identifier, vartype, currentOffset)
    println(identifier)
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
    case None => parent flatMap (_.lookupWithOffsetAccumulator(identifier, currentOffset))
  }

  /**
    * Compute the offset of a variable relative to the scope which initiates the lookup
    *
    * Symbol table                                           ARM11 Stack (offsets relative to SP at time of adding)
    *
    *  ---                                                   |z:5|       |
    * |x:0|                                                  |y:1|       | stack grows this way by subtraction
    * |y:1|                                                  |x:0|       v
    * |z:5| <- parent <- ---  <-- lookup relative to here     ---  - no real separation (just for display purposes)
    *  --- size=9       |a:0|                                |a:0| <- SP is here when lookup needs to happen
    *                    ---
    *
    * lookup(x).offset = +4
    * lookup(y).offset = +5
    * lookup(z).offset = +9
    * lookup(a).offset = 0
    * */
  private def lookupWithOffsetAccumulator(identifier: String, offset: Int): Option[Reference]
  = lookup(identifier) match {
    // Base case does the offset computation
    case Some(ref) =>
      Some(VariableReference(identifier, ref.vartype, ref.offset + offset))
    // Recursive case just accumulates the offset
    case None => parent flatMap (_.lookupWithOffsetAccumulator(identifier, offset + currentOffset))
  }

  def clear() = {
    map.clear()
    currentOffset = 0
  }

  def sizeInBytes = currentOffset
}

object SymbolTable {

  val globalTable: SymbolTable = SymbolTable(None)
  var functionTable: Map[String, SymbolTable] = Map()
  private var currentTable: SymbolTable = globalTable

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

  /**
    * Adds the name to the global table and creates an entry in the function table
    * associated to this name.
    * */
  def addFunction(identifier: String, function: FunctionReference): Unit = {
    globalTable.map += identifier -> function
    functionTable += identifier -> SymbolTable()
  }

  def apply(): SymbolTable = currentTable
}