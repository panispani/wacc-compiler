package wacc.arm

import wacc.codegeneration.predefined.StaticCode
import wacc.{SymbolTable, VariableReference}
import wacc.codegeneration.{CodeSegment, TransAssignRhs, TransExpressions}
import wacc.constructs._

object Macros {
  def store(lhs: AssignTarget, symbolTable: SymbolTable, registers: Seq[Register]): Seq[Instruction] = {
    lhs match {
      case VariableReference(name, vartype, offset) => {
        lhs.vartype match {
          case Boolean | Character => Seq(STRB(registers.head, RegisterAddress(FP, offset)))
          case default             => Seq(STR(registers.head, RegisterAddress(FP, offset)))
        }
      }
      case ArrayElement(name, index, elemtype) => {
        val variableReference = symbolTable.lookupDeep(name).get

        registers match {
          case (src +: reg1 +: reg2 +: regs) => {
            val check = Macros.checkArrayBounds(variableReference, index.head, symbolTable, reg1 +: reg2 +: regs).instructions
            //reg1 is now going to contain the value of the index expression

            val store = elemtype match {
              case Character | Boolean => STRB(src, RegisterAddress(reg1, 0))
              case default => STR(src, RegisterAddress(reg1, 0))
            }

            check ++ Macros.getArrayElemAddress(variableReference, symbolTable, reg1 +: reg2 +: regs, elemtype).instructions ++ Seq(store)
          }
        }
      }
      case pe @ PairElement(selector, variableReference: Expression, elemType) => {
        val instruction = TransAssignRhs.getPairElementPointer(pe, symbolTable, registers.tail)
        val store = pe.vartype match {
          case Character | Boolean => STRB(registers.head, RegisterAddress(registers(1)))
          case default => STR(registers.head, RegisterAddress(registers(1)))
        }

        instruction.extend(store).instructions

//        LDR r4, =1
//        18		LDR r5, [sp]
//        19		MOV r0, r5
//        20		BL p_check_null_pointer
//        21		LDR r5, [r5]
//        22		STR r4, [r5]
      }
    }
  }

  def checkArrayBounds(array: VariableReference, index: Expression,
                       symbolTable: SymbolTable, registers: Seq[Register]): CodeSegment = {
    CodeSegment()
      .extend(TransExpressions.transExpression(index, symbolTable, registers))
      .extend(Seq(
        ADD(R1, FP, ImmOperand(array.offset)),   // Put the start of the array in the first register
        LDR(R1, RegisterAddress(R1, 0)),   //Load size of array in first register
        MOV(R0, registers.head),
        BL(StaticCode.getStaticFunction(StaticCode.checkArrayBounds))
      ))
  }

  def getArrayElemAddress(array: VariableReference,
                       symbolTable: SymbolTable, registers: Seq[Register], elemType: Type): CodeSegment = {
    registers match {
      case (reg1 +: reg2 +: regs) => {
        CodeSegment()
          .extend (Seq(
            LDR (reg2, RegisterAddress (FP, array.offset) ), // Put the start of the array in the second register
            LDR (regs.head, Const(elemType.size)), //Put size of one element in third register
            MUL (reg1, reg1, regs.head), //Put elemSize * index in first register
            ADD (reg1, reg1, reg2),
            LDR (reg2, Const(4)),
            ADD (reg1, reg1, reg2)
          ))
      }
    }
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
  def frame(size: Int, isBranch: Boolean = false): (CodeSegment, CodeSegment) = {
    val MAX_SIZE = 1024

    var start = CodeSegment()

    if (isBranch) start = start.extend(PUSH(Seq(LR)))
    start = start.extend(Seq(PUSH(Seq(FP)), MOV(FP, SP)))

    var end = CodeSegment()

    // Handle large scopes by adding / subtracting several times
    val blocks = size / MAX_SIZE
    val remainder = size % MAX_SIZE

    for (i <- 1 to blocks) {
      //start = start.append(SUB(SP, SP, ImmOperand(MAX_SIZE)))
      end = end.extend(ADD(SP, SP, ImmOperand(MAX_SIZE)))
    }

    //start = start.append(SUB(SP, SP, ImmOperand(remainder)))
    end = end.extend(ADD(SP, SP, ImmOperand(remainder)))
             .extend(POP(Seq(FP)))

    if (isBranch) end = end.extend(POP(Seq(PC)))
    (start, end)
  }
}