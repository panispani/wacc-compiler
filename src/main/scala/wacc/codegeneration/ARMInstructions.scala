package wacc.codegeneration

/**
  * Created by panayiotis on 15/11/16.
  */
trait Instruction
trait Operand
case class Add(Rd: Register, Rn: Register, Op2: Operand) extends Instruction

