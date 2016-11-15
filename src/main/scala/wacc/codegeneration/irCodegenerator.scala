package wacc.codegeneration

import wacc.constructs.Program

/**
  * Created by panayiotis on 15/11/16.
  */
trait Register
trait Operand
trait Instruction

case class Add(Rd: Register, Rn: Register, Op2: Operand) extends Instruction

class irCodegenerator {
  def codegen(program: Program): Seq[Instruction] = {
    println("codegen")
    Seq[Instruction]()
  }
}
