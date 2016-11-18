package wacc.codegeneration

import wacc.constructs.Program
import wacc.TransPrograms._

/**
  * Created by panayiotis on 15/11/16.
  */
//accumulator register approach
class IrCodegenerator {
  val allRegisters = Seq(R0, R1, R2, R3, R4, R5, R6, R7, R8, R9, R10, R11, R12)

  def codegen(program: Program): Seq[Instruction] = {
    transNext(program, allRegisters)
  }

}
