package wacc.codegeneration

/**
  * Created by panayiotis on 15/11/16.
  */
trait Instruction
case class ADC(Rd: Register, Rn: Register, Op2: Operand) extends Instruction
case class ADD(Rd: Register, Rn: Register, Op2: Operand) extends Instruction
case class ADDS(Rd: Register, Rn: Register, i12: Imm12) extends Instruction
case class AND(Rd: Register, Rn: Register, Op2: Operand) extends Instruction
case class BRANCH() extends Instruction
case class CMP(Rn: Register, Op2: Operand) extends Instruction
case class EOR(Rn: Register, Op2: Operand) extends Instruction
case class LDR(Rt: Register, Rn: Register, offset: Integer) extends Instruction // use stack pointer
case class MOV(Rd: Register, Op2: Operand) extends Instruction
case class MOVS(Rd: Register, i16: Imm16) extends Instruction
case class MUL(Rd: Register, Rn: Register, Rm: Register) extends Instruction
case class MULS(Rn: Register, Rm: Register) extends Instruction
case class ORR(Rd: Register, Rn: Register, Op2: Operand)
case class SUB(Rd: Register, Rn: Register, Op2: Operand)
case class SUBS(Rn: Register, Op2: Operand)
case class SUB2(Rd: Register, Rn: Register, i12: Imm12)
case class SUB2S(Rn: Register, i12: Imm12)
case class STR(Rt: Register, Rn: Register, offset: Integer) extends Instruction
case class PUSH(reglist: Seq[Register])
case class POP(reglist: Seq[Register])
