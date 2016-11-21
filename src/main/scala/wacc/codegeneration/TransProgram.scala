package wacc

import wacc.constructs.Program
import wacc.TransFunctions._
import wacc.TransStatements._
import wacc.codegeneration._

package object TransProgram {
  def transProgram(program: Program): Seq[Instruction] = {
    val functionInstructions = program.functions map (s => transFunction (s, Registers.expressionRegs))
    val mainInstructions     = program.statements map (s => transStatement (s, Registers.expressionRegs))
    val stackBytes = VarLog.byteCount()

    new CodeSegment()
      .extend(functionInstructions.flatten)
      .append(SUB(SP, SP, ImmOperand(stackBytes)))
      .extend(mainInstructions.flatten)
      .append(ADD(SP, SP, ImmOperand(stackBytes)))
      .append(MOV(R0, ImmOperand(0))).instructions
  }

}
