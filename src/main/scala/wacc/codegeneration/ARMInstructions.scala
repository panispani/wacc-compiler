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
case class B(label: Label) extends Instruction // B{<cond>} label, R15=<address>
case class BL(label: Label) extends Instruction // BL{<cond>} sub_routine_label, R14=R15 R15=<address>

// Arithmetic <Operation>{<cond>}{S} Rd, Rn, Operand2
case class ADD(Rd: Register, Rn: Register, Op2: Operand) extends Instruction //Rd=Rn+Op2
case class ADDS(Rd: Register, Rn: Register, Op2: Operand) extends Instruction
case class SUB(Rd: Register, Rn: Register, Op2: Operand) extends Instruction
case class SUBS(Rd: Register, Rn: Register, Op2: Operand) extends Instruction
// SUBC, RSB, RSC were not covered

// Comparisons <Operation>{<cond>} Rn, Operand2
case class CMP(Rn: Register, Op2: Operand) extends Instruction // CPSR flags=Rn-Op2
case class CMN(Rn: Register, Op2: Operand) extends Instruction // CPSR flags=Rn+Op2
case class TST(Rn: Register, Op2: Operand) extends Instruction // CPSR flags=RnANDOp2
// CMN, TST, TEQ

// Logical <Operation>{<cond>}{S} Rd, Rn, Operand2
case class AND(Rd: Register, Rn: Register, Op2: Operand) extends Instruction //Rd=Rn AND Op2
case class EOR(Rn: Register, Op2: Operand) extends Instruction
case class ORR(Rd: Register, Rn: Register, Op2: Operand) extends Instruction
// BIC

// Data movement <Operation>{<cond>}{S} Rd, Operand2
case class MOV(Rd: Register, Op2: Operand) extends Instruction
// MVN

// Multiplication
// MUL{<cond>}{S} Rd, Rm, Rs
case class MUL(Rd: Register, Rn: Register, Rm: Register) extends Instruction
case class MULS(Rd: Register, Rn: Register, Rm: Register) extends Instruction
// MLA{<cond>}{S} Rd, Rm, Rs,Rn ; consider multiply long, too

// Single register data transfer <LDR|STR>{<cond>}{<size>} Rd, <address>
case class LDR(Rd: Register, address: Address) extends Instruction // Rd=<address>
case class STR(Rd: Register, address: Address) extends Instruction // <address>=Rd
case class STRB(Rt: Register, address: Address) extends Instruction

// Stack
case class PUSH(reglist: Seq[Register]) extends Instruction // up to 8
case class POP(reglist: Seq[Register]) extends Instruction
