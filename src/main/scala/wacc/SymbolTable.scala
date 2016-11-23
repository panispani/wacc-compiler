package wacc

import wacc.constructs._

import scala.collection.mutable

trait Reference extends Typed {
  val name: String
  val offset: Int
}
case class VariableReference(name: String, vartype: Type, offset: Int) extends Reference with Expression
case class FunctionReference(name: String, returnType: Type, argumentTypes : Seq[Type])

case class SymbolTable(parent: Option[SymbolTable]) {

  private var currentOffset: Int = 4
  def sizeInBytes = currentOffset - 4

  private var map: mutable.Map[String, VariableReference] = mutable.Map()

  // The frame pointer will store the stack both LR and the parent FP have been stored
  // The offset to argument 0 is 8 and the rest depend on the argument sizes
  // We could treat all types as 4 bytes and simplify this (it will also let us do PUSH of multiple
  // registers which simplifies the code even more)
  def addFunctionArgument(name: String, vartype: Type): Unit = {
    map += name -> VariableReference(name, vartype, currentOffset)
    currentOffset += vartype.size
  }

  // The offset here is relative to the frame pointer so it starts from -4 and goes down
  def addLocalVariable(identifier: String, vartype: Type): VariableReference = {
    val variableReference = VariableReference(identifier, vartype, -currentOffset)
    map += identifier -> variableReference
    currentOffset += vartype.size
    variableReference
  }

  def lookup(identifier: String): Option[VariableReference]
    = map get identifier match {
    case Some(reference) => Some(reference)
    case None => None
  }

  def lookupDeep(identifier: String): Option[VariableReference]
  = lookup(identifier) match {
    case Some(ident) => Some(ident)
    case None => parent.flatMap(_.lookupWithOffsetAccumulator(identifier, 0))
  }

  /**
    * Compute the offset of a variable relative to the FP of the scope which initiates the lookup
    *
    * Symbol table                 ARM11 Stack (offsets relative to SP at time of adding)
    *                              On entering a scope the order is: PUSH FP, FP = SP, SP -= size of this scope
    *  ---
    * |x:-4|                              store each local variable      STR Ri, [FP, #offset]
    *  --- size=4                         access any reachable variable  LDR Ri, [FP, #offset]
    *                              |x:-4|
    *   parent of                  | FP |
    *       ---                    |y:-4|
    *      |y:-4|                  |z:-8|
    *      |z:-5|                  | FP | <- The value of FP is the address above it
    *       --- size=5             |a:-4|
    *        parent of |a:-4|
    *
    * lookup(x).offset = 16
    * lookup(y).offset = 8
    * lookup(z).offset = 4
    * lookup(a).offset = -4
    * */
  private def lookupWithOffsetAccumulator(identifier: String, offset: Int): Option[VariableReference]
  = lookup(identifier) match {
    // Base case does the offset computation
    case Some(ref) => Some(VariableReference(identifier, ref.vartype,  ref.offset + currentOffset + offset))
    // Recursive case just accumulates the offset (parent frame pointer and parent size)
    case None => parent flatMap (
      parent => parent.lookupWithOffsetAccumulator(identifier, currentOffset + offset))
  }

  def clear() = {
    map.clear()
    currentOffset = 4
  }
}

case class FunctionTable(reference: FunctionReference, symbolTable: SymbolTable)

object SymbolTable {

  val globalTable: SymbolTable = SymbolTable(None)
  private var currentTable: SymbolTable = globalTable
  val functionsTable: mutable.Map[String, FunctionTable] = mutable.Map()

  def clearAll() = {
    globalTable.clear()
    functionsTable.clear()
    currentTable = globalTable
  }

  def openScope() = {
    currentTable = SymbolTable(Some(currentTable))
  }

  def closeScope() = {
    currentTable = currentTable.parent.get
  }

  /**
    * Adds the name to the global table. The current table is set to be the newly created
    * function table so that it can be populated by the parser
    * */
  def declareFunction(function: FunctionReference): Unit = {

    /** Function tables have no parent as they start a fresh scope
      * However, the arguments in a function table will have positive offset
      * w.r.t the frame pointer (all function parameters are passed on the stack for now)
      * */
    currentTable = SymbolTable(None)
    functionsTable += function.name -> FunctionTable(function, currentTable)
    currentTable.currentOffset = 8 // over PC and FP for computing offsets to arguments
  }

  // A helper that must be called in order to close the scope for a function table
  // because it has no parent (the global table is naturally used)
  def completeFunctionDeclaration() = {
    currentTable.currentOffset = 4 // right under FP for later
    currentTable = globalTable
  }

  // A helper that must be called with the name of a declared function
  def defineFunction(identifier: String): Seq[VariableReference] = {
    val function = functionsTable(identifier)
    currentTable = function.symbolTable
    function.symbolTable.map.values.toList
  }

  def completeFunctionDefinition(): SymbolTable = {
    val functionTable = currentTable
    currentTable = globalTable
    functionTable
  }

  def apply(): SymbolTable = currentTable
}