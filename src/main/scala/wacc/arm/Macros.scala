package wacc.arm

import wacc.VariableReference
import wacc.codegeneration.{CodeSegment, StaticCode, TransAssignRhs, TransExpressions}
import wacc.constructs._

import scala.collection.+:

object Macros {
  def store(lhs: AssignTarget, registers: Seq[Register]): Seq[Instruction] = {
    lhs match {
      case VariableReference(name, vartype, offset) => {
        lhs.vartype match {
          case Boolean | Character => Seq(STRB(registers.head, RegisterAddress(FP, offset)))
          case default             => Seq(STR(registers.head, RegisterAddress(FP, offset)))
        }
      }
      case ArrayElement(reference, index, elemtype) => {

        val src +: reg1 +: regs = registers

        val store = elemtype match {
          case Character | Boolean => STRB(src, RegisterAddress(reg1))
          case _ => STR(src, RegisterAddress(reg1))
        }

        CodeSegment(ADD(reg1, FP, ImmOperand(reference.offset)))
          .extend(getNestedElementAddress(index, reg1 +: regs, elemtype.size))
          .extend(store)
          .instructions
      }

      case pe @ PairElement(selector, variableReference: Expression, elemType) => {
        val instruction = pe.getPairElementPointer(registers.tail)
        val store = pe.vartype match {
          case Character | Boolean => STRB(registers.head, RegisterAddress(registers(1)))
          case default => STR(registers.head, RegisterAddress(registers(1)))
        }

        instruction.extend(store).instructions

      }
    }
  }

  //Assume start of array in reg1
  def getNestedElementAddress(indexes: Seq[Expression], registers: Seq[Register], elemSize: Int): CodeSegment = {
    val reg1 +: reg2 +: regs = registers

    indexes.foldLeft(CodeSegment()) ((accumulator, index) => {
      accumulator
        .extend(index.transAssignRhs(reg2 +: regs))
        .extend(Macros.checkAndGetArrayElemAddress(reg1 +: reg2 +: regs, elemSize))
    })
  }

  //Assume start of array in reg1 and index in reg2
  private def checkAndGetArrayElemAddress(registers: Seq[Register], elemSize: Int): CodeSegment = {
    val reg1 +: reg2 +: regs = registers

    CodeSegment()
      .extend(Seq(
        LDR(reg1, RegisterAddress(reg1, 0)), // Load in reg1 startOfArray
        MOV(R1, reg1),                       // Load startOfArray in R1
        MOV(R0, reg2),                       // Load index in R0
        BL(StaticCode.checkArrayBoundsLabel),
        ADD(reg1, reg1, ImmOperand(4)),      // Store in reg1 the value startOfArray + 4 (4 indicates the space used to store the size of the array)
        LDR(regs.head, Const(elemSize)),     // Store in regs.head the value elemSize
        MUL(reg2, reg2, regs.head),          // Store in reg2 the value index * elemSize
        ADD(reg1, reg1, reg2)                // Store in reg1 the value startOfArray + 4 + index * elemSize TODO: this will work when all types ar 4 bytes
      ))
  }

  def load(reg1: Register, offset: Int, vartype: Type): Instruction = vartype match {
    case Boolean | Character => LDRB(reg1, RegisterAddress(FP, offset))
    case default => LDR(reg1, RegisterAddress(FP, offset))
  }

  def conditionalExpression(left: Register, right: Register)
                          (trueCondition: Condition, falseCondition: Condition): CodeSegment
  = CodeSegment(
    CMP(left, right),
    LDR(left, Const(1), trueCondition),
    LDR(left, Const(0), falseCondition)
  )

  /**
    * Returns the code for opening and closing a semantic scope
    * based on the size of the given scope (not including child scope sizes)
    * */
  def semanticFrame(size: Int): (CodeSegment, CodeSegment) = {
    val MAX_SIZE = 1024

    // Handle large scopes by adding / subtracting several times
    val blocks = size / MAX_SIZE
    val remainder = size % MAX_SIZE

    val start = (1 to blocks).foldLeft (
      CodeSegment(SUB(SP, SP, ImmOperand(remainder))))(
      (acc, _) => acc.extend(SUB(SP, SP, ImmOperand(MAX_SIZE))))

    val end = (1 to blocks).foldLeft (
      CodeSegment(ADD(SP, SP, ImmOperand(remainder))))(
      (acc, _) => acc.extend(ADD(SP, SP, ImmOperand(MAX_SIZE))))

    (start, end)
  }

  def functionCallFrameStart(size: Int): CodeSegment = {
    val start = CodeSegment(PUSH(Seq(LR)), PUSH(Seq(FP)), MOV(FP, SP))
      .extend(semanticFrame(size)._1)

    start
    // this does not attempt to generate the function frame end because it is contained in return statements
    // which should always be the last statement in a function. Return statements in different branches could
    // end up having different stack sizes to restore
  }
}