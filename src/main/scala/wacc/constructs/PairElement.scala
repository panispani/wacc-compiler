package wacc.constructs

import wacc.arm._
import wacc.codegeneration._

case class PairElement(selector: Selector, expression: Expression, varType: Type) extends AssignValue with AssignTarget {

  def getPairElementPointer(registers: Seq[Register]): CodeSegment = {
    CodeSegment()
      .extend(expression.transAssignRhs(registers))         // Translate expression inside selector
      .extend(MOV(R0, registers.head))                      // Check if address is null
      .extend(BL(StaticCode.checkNullPointerFunctionLabel)) // Check if address is null
      .extend(LDR(registers.head, RegisterAddress(registers.head, selector match {
        case FirstSelector => 0   // Access the left element of the pair (which is a pointer)
        case SecondSelector => 4  // Access the right element of the pair (which is a pointer)
      })))
  }

  override def transAssignRhs(registers: Seq[Register]): CodeSegment = {
    getPairElementPointer(registers)
      .extend(LDR(registers.head, RegisterAddress(registers.head)))  // Dereference the pointer at this element
  }
}

case class Selector(selector: String)
object FirstSelector extends Selector("fst")
object SecondSelector extends Selector("snd")
