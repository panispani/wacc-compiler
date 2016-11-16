package wacc.codegeneration

import wacc.constructs._
import wacc.codegeneration.Weight.weight
import wacc.transStatements._
import wacc.transFunctions._
import wacc.transPrograms._
import wacc.transAssigns._
import wacc.transExpressions._

/**
  * Created by panayiotis on 15/11/16.
  */
//accumulator register approach
class irCodegenerator {
  val allRegisters = Seq(R1, R2, R3, R4, R5, R6, R7, R8, R9, R10, R11, R12)

  def codegen(program: Program): Seq[Instruction] = {
    transNext(program, allRegisters)
  }

}
