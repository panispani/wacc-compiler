package wacc.constructs

import wacc.arm._
import wacc.codegeneration._

case class PairConstructor(firstExp: Expression, secondExp: Expression) extends AssignValue {
  override val varType: Type = PairType(firstExp.varType, secondExp.varType)

  override def transAssignRhs(registers: Seq[Register]): CodeSegment = {
    val firstType = firstExp.varType
    val secondType = secondExp.varType

    val store1 = firstType match {
      case Boolean | Character => STRB(registers(1), RegisterAddress(R0))
      case _ => STR(registers(1), RegisterAddress(R0))
    }

    val store2 = secondType match {
      case Boolean | Character => STRB(registers(1), RegisterAddress(R0))
      case _ => STR(registers(1), RegisterAddress(R0))
    }

    CodeSegment()
        .extend(MOV(R0, ImmOperand(firstType.enumId)))
        .extend(MOV(R1, ImmOperand(secondType.enumId)))
        .extend(BL(Label("new_pair")))           // R0: pair *newpair = new_pair_constructor
        .extend(MOV(registers.head, R0))                     // return register = newpair
        .extend(LDR(R0, RegisterAddress(R0)))                // R0 = *newpair (first elem address)
        .extend(firstExp.transAssignRhs(registers.tail))     // next free reg = expression1
        .extend(store1)                                      // Store the value for the first element in its memory
        .extend(LDR(R0, RegisterAddress(registers.head, 4))) // R0 = *(newpair + 4)
        .extend(secondExp.transAssignRhs(registers.tail))    // next free reg = expression2
        .extend(store2)                                      // Store the value for the first element in its memory

    /*
    Commented out for the purposes of GC.
    TODO Make an if to check for -gc flag when compiling

    CodeSegment()
      .extend(LDR(R0, Const(8)))                           // Load the size of the pair in R0
      .extend(BL(Label("malloc")))
      .extend(MOV(registers.head, R0))
      .extend(firstExp.transAssignRhs(registers.tail))
      .extend(LDR(R0, Const(firstType.size)))
      .extend(BL(Label("malloc")))
      .extend(store1)                                      // Store the value for the first element in its memory
      .extend(STR(R0, RegisterAddress(registers.head)))    // Put address of first element in memory of pair
      .extend(secondExp.transAssignRhs(registers.tail))
      .extend(LDR(R0, Const(secondType.size)))
      .extend(BL(Label("malloc")))
      .extend(store2)                                      // Store the value for the second element in its memory
      .extend(STR(R0, RegisterAddress(registers.head, 4))) // Put address of second element in memory of pair with offset
    */
  }
}
