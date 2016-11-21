package wacc.codegeneration

import wacc.constructs.Program
import wacc.TransFunctions._
import wacc.TransStatements._

/**
  * Created by panayiotis on 15/11/16.
  */
//accumulator register approach
class IntermediateReprCodeGenerator {
  def generateCode(program: Program, registers: Seq[Register]): Seq[Instruction] = {
    val functionInstructions = program.functions map (s => transFunction (s, registers))
    val mainInstructions     = program.statements map (s => transStatement (s, registers))
    val stackBytes = VarLog.byteCount()

    // add labels later
    new CodeSegment()
      .extend(functionInstructions.flatten)
      .append(SUB(SP, SP, ImmOperand(stackBytes)))
      .extend(mainInstructions.flatten)
      .append(ADD(SP, SP, ImmOperand(stackBytes)))
      .append(MOV(R0, ImmOperand(0))).instructions
  }

}
