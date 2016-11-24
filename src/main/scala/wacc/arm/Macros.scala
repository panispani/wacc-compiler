package wacc.arm

import wacc.{SymbolTable, VariableReference}
import wacc.codegeneration.{CodeSegment, StaticCode, TransExpressions}
import wacc.constructs.{ArrayElement, AssignTarget, Boolean, Character, Expression, PairElement, Type}

object Macros {
  def store(lhs: AssignTarget, symbolTable: SymbolTable, regs: Seq[Register]): Seq[Instruction] = {
    lhs match {
      case VariableReference(name, vartype, offset) => {
        lhs.vartype match {
          case Boolean | Character => Seq(STRB(regs.head, RegisterAddress(FP, offset)))
          case default             => Seq(STR(regs.head, RegisterAddress(FP, offset)))
        }
      }
//      case ArrayElement(name, index, vartype) => {
//        TransExpressions.transExpression(index.head, symbolTable, regs) ++
//        Seq(
//          LDR(regs(1), RegisterAddress(FP, 0)),  //
//          LDR(regs(2), )
//        )
//      }
      case PairElement(selector, expression, vartype) => Seq()
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
        BL(StaticCode.checkArrayBoundsLabel)
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
    MOV(left, ImmOperand(1), trueCondition),
    MOV(left, ImmOperand(0), falseCondition)
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

    if (isBranch) start = start.append(PUSH(Seq(LR)))
    start = start.extend(Seq(PUSH(Seq(FP)), MOV(FP, SP)))

    var end = CodeSegment()

    // Handle large scopes by adding / subtracting several times
    val blocks = size / MAX_SIZE
    val remainder = size % MAX_SIZE

    for (i <- 1 to blocks) {
      start = start.append(SUB(SP, SP, ImmOperand(MAX_SIZE)))
      end = end.append(ADD(SP, SP, ImmOperand(MAX_SIZE)))
    }

    start = start.append(SUB(SP, SP, ImmOperand(remainder)))
    end = end.append(ADD(SP, SP, ImmOperand(remainder)))
             .append(POP(Seq(FP)))

    if (isBranch) end = end.append(POP(Seq(PC)))
    (start, end)
  }
}