package wacc.arm

import wacc.{SymbolTable, VariableReference}
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
          .extend(getNestedElementAddress(index, reg1 +: regs))
          .extend(store)
          .instructions
      }

      case pe @ PairElement(selector, variableReference: Expression, elemType) => {
        val instruction = TransAssignRhs.getPairElementPointer(pe, registers.tail)
        val store = pe.vartype match {
          case Character | Boolean => STRB(registers.head, RegisterAddress(registers(1)))
          case default => STR(registers.head, RegisterAddress(registers(1)))
        }

        instruction.extend(store).instructions

      }
    }
  }

  //Assume start of array in reg1
  def getNestedElementAddress(indexes: Seq[Expression], registers: Seq[Register]): CodeSegment = {
    val reg1 +: reg2 +: regs = registers

    indexes.foldLeft(CodeSegment()) ((accumulator, index) => {
      accumulator
        .extend(TransExpressions.transExpression(index, reg2 +: regs))
        .extend(Macros.checkAndGetArrayElemAddress(reg1 +: reg2 +: regs))
    })
  }

  //Assume start of array in reg1 and index in reg2
  private def checkAndGetArrayElemAddress(registers: Seq[Register]): CodeSegment = {
    val reg1 +: reg2 +: regs = registers

    CodeSegment()
      .extend(Seq(
        LDR(reg1, RegisterAddress(reg1, 0)),   //Load size of array in R1
        MOV(R1, reg1),
        MOV(R0, reg2),                       //Load index in R0
        BL(StaticCode.checkArrayBoundsLabel),
        ADD (reg1, reg1, ImmOperand(4)),
        ADDLSL(reg1, reg1, reg2, LSL(2)) // TODO: this will work when all types ar 4 bytes
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
    STR(left, Const(1), trueCondition),
    STR(left, Const(0), falseCondition)
  )

  /**
    * Returns the code for opening and closing a scope
    * If the scope is a function call the isBranch flag must be set to true
    * so that the LR and PC are handled appropriately
    * */
  //    * TODO: make functionally
  def frame(size: Int): (CodeSegment, CodeSegment) = {
    val MAX_SIZE = 1024

    var start = CodeSegment()

    start = start.extend(Seq(PUSH(Seq(FP)), MOV(FP, SP)))

    var end = CodeSegment()

    // Handle large scopes by adding / subtracting several times
    val blocks = size / MAX_SIZE
    val remainder = size % MAX_SIZE

    for (i <- 1 to blocks) {
      end = end.extend(ADD(SP, SP, ImmOperand(MAX_SIZE)))
    }

    end = end.extend(ADD(SP, SP, ImmOperand(remainder)))
             .extend(POP(Seq(FP)))

    (start, end)
  }
}