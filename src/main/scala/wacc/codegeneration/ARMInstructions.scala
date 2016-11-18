package wacc.codegeneration

/**
  * Created by panayiotis on 15/11/16.
  *
  * Conventions follow the ARM documentation
  * Rd  - destination register
  * Rn  - 1st operand register
  * Op2 - 2nd operand (immediate value or shifted register)
  */
trait Instruction {
//  val name: String
//
//  val cond: String = ""
//  val setsConditionCodes = false
//
//  override def toString: String =
//    name +
//    cond +
//    (if (setsConditionCodes) "S" else "")
}

case class Label(name: String)

// Branch
case class BRANCH(label: Label) extends Instruction // B{<cond>} label
case class BL(label: Label) extends Instruction // BL{<cond>} sub_routine_label

// Arithmetic <Operation>{<cond>}{S} Rd, Rn, Operand2
case class ADC(Rd: Register, Rn: Register, Op2: Operand) extends Instruction
case class ADD(Rd: Register, Rn: Register, Op2: Operand) extends Instruction
case class ADDS(Rd: Register, Rn: Register, i12: Integer) extends Instruction
case class SUB(Rd: Register, Rn: Register, Op2: Operand) extends Instruction
case class SUBS(Rn: Register, Op2: Operand) extends Instruction
// SUBC, RSB, RSC were not covered
case class SUB2(Rd: Register, Rn: Register, i12: Integer) extends Instruction
case class SUB2S(Rn: Register, i12: Integer) extends Instruction

// Comparisons <Operation>{<cond>} Rn, Operand2
case class CMP(Rn: Register, Op2: Operand) extends Instruction
// CMN, TST, TEQ

// Logical <Operation>{<cond>}{S} Rd, Rn, Operand2
case class AND(Rd: Register, Rn: Register, Op2: Operand) extends Instruction
case class EOR(Rn: Register, Op2: Operand) extends Instruction
case class ORR(Rd: Register, Rn: Register, Op2: Operand) extends Instruction
// BIC

// Data movement <Operation>{<cond>}{S} Rd, Operand2
case class MOV(Rd: Register, Op2: Operand) extends Instruction
case class MOVS(Rd: Register, i16: Int) extends Instruction
case class MOVCH(Rd: Register, ch: Char) extends Instruction
// MVN

// Multiplication
// MUL{<cond>}{S} Rd, Rm, Rs
case class MUL(Rd: Register, Rn: Register, Rm: Register) extends Instruction
case class MULS(Rn: Register, Rm: Register) extends Instruction
// MLA{<cond>}{S} Rd, Rm, Rs,Rn ; consider multiply long, too

// Single register data transfer <LDR|STR>{<cond>}{<size>} Rd, <address>
case class LDR(Rt: Register, Rn: Register, offset: Integer) extends Instruction // use stack pointer
case class LDRIMM(Rt: Register, immediateOperand: Integer) extends Instruction
case class STR(Rt: Register, Rn: Register, offset: Integer) extends Instruction
case class STRB(Rt: Register, Rn: Register, offset: Integer) extends Instruction

// Stack
case class PUSH(reglist: Seq[Register]) extends Instruction
case class POP(reglist: Seq[Register]) extends Instruction
