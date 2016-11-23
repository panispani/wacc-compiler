package wacc.arm

import wacc.codegeneration._

/**
  * Conventions follow the ARM documentation
  * Rd  - destination register
  * Rn  - 1st operand register
  * Op2 - 2nd operand (immediate value or shifted register)
  */
abstract class Instruction {
  val name: String = this.getClass.getSimpleName
  override def toString: String = name
}

abstract class ConditionalInstruction extends Instruction {
  val cond: Condition

  override def toString: String = s"${super.toString}$cond"
}


abstract class OneOpInstruction extends ConditionalInstruction {
  val op1: Label
  override def toString: String = s"${super.toString} $op1"
}

case class B(op1: Label, cond: Condition = ALWAYS) extends OneOpInstruction

case class BL(op1: Label, cond: Condition = ALWAYS) extends OneOpInstruction


abstract class TwoRegOneOpInstruction extends ConditionalInstruction {
  val Rd: Register
  val Rn: Register
  val Op1: Operand

  override def toString: String = s"${super.toString} $Rd, $Rn, $Op1"
}

case class ADD(Rd: Register, Rn: Register, Op1: Operand, cond: Condition = ALWAYS) extends TwoRegOneOpInstruction

case class ADDS(Rd: Register, Rn: Register, Op1: Operand, cond: Condition = ALWAYS) extends TwoRegOneOpInstruction

case class SUB(Rd: Register, Rn: Register, Op1: Operand, cond: Condition = ALWAYS) extends TwoRegOneOpInstruction

case class SUBS(Rd: Register, Rn: Register, Op1: Operand, cond: Condition = ALWAYS) extends TwoRegOneOpInstruction

case class AND(Rd: Register, Rn: Register, Op1: Operand, cond: Condition = ALWAYS) extends TwoRegOneOpInstruction

case class EOR(Rd: Register, Rn: Register, Op1: Operand, cond: Condition = ALWAYS) extends TwoRegOneOpInstruction

case class ORR(Rd: Register, Rn: Register, Op1: Operand, cond: Condition = ALWAYS) extends TwoRegOneOpInstruction

case class RSBS(Rd: Register, Rn: Register, Op1: Operand, cond: Condition = ALWAYS) extends TwoRegOneOpInstruction


abstract class OneRegOneOpInstruction extends ConditionalInstruction {
  val Rd: Register
  val Op1: Operand

  override def toString: String = s"${super.toString} $Rd, $Op1"
}

case class CMP(Rd: Register, Op1: Operand, cond: Condition = ALWAYS) extends OneRegOneOpInstruction

case class CMN(Rd: Register, Op1: Operand, cond: Condition = ALWAYS) extends OneRegOneOpInstruction

case class TST(Rd: Register, Op1: Operand, cond: Condition = ALWAYS) extends OneRegOneOpInstruction

case class MOV(Rd: Register, Op1: Operand, cond: Condition = ALWAYS) extends OneRegOneOpInstruction

case class LDR(Rd: Register, Op1: Address, cond: Condition = ALWAYS) extends OneRegOneOpInstruction

case class LDRSB(Rd: Register, Op1: Address, cond: Condition = ALWAYS) extends OneRegOneOpInstruction

case class LDRB(Rd: Register, Op1: Address, cond: Condition = ALWAYS) extends OneRegOneOpInstruction

case class STR(Rd: Register, Op1: Address, cond: Condition = ALWAYS) extends OneRegOneOpInstruction

case class STRB(Rd: Register, Op1: Address, cond: Condition = ALWAYS) extends OneRegOneOpInstruction


abstract class ThreeRegNoOpInstruction extends ConditionalInstruction {
  val Rd: Register
  val Rn: Register
  val Rs: Register

  override def toString = s"${super.toString} $Rd, $Rn, $Rs"
}

case class MUL(Rd: Register, Rn: Register, Rs: Register, cond: Condition = ALWAYS) extends ThreeRegNoOpInstruction

case class MULS(Rd: Register, Rn: Register, Rs: Register, cond: Condition = ALWAYS) extends ThreeRegNoOpInstruction


abstract class RegListInstruction extends ConditionalInstruction {
  val reglist: Seq[Register]

  override def toString = s"${super.toString} {${reglist.mkString(", ")}}"
}

case class PUSH(reglist: Seq[Register], cond: Condition = ALWAYS) extends RegListInstruction

case class POP(reglist: Seq[Register], cond: Condition = ALWAYS) extends RegListInstruction

