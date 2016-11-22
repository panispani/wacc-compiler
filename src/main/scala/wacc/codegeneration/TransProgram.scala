package wacc

import wacc.constructs.Program
import wacc.TransFunctions._
import wacc.TransStatements._
import wacc.codegeneration._

package object TransProgram {
  def transProgram(program: Program): CodeSegment = {
    val functionInstructions = program.functions map (s => transFunction (s, Registers.expressionRegs))
    val mainInstructions     = program.main.statements map (
      s => transStatement (s, program.main.symbolTable, Registers.expressionRegs))

    new CodeSegment()
      .extend(functionInstructions.flatten)
      .append(SUB(SP, SP, ImmOperand(program.main.symbolTable.sizeInBytes)))
      .extend(mainInstructions.flatten)
      .append(ADD(SP, SP, ImmOperand(program.main.symbolTable.sizeInBytes)))
      .append(MOV(R0, ImmOperand(0)))
  }

}
