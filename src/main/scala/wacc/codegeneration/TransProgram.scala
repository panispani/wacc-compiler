package wacc

import wacc.TransFunctions._
import wacc.TransStatements._
import wacc.codegeneration._
import wacc.arm._
import wacc.constructs.Program

package object TransProgram {
  def transProgram(program: Program): CodeSegment = {
    val functionInstructions = program.functions map (s => transFunction (s, Registers.expressionRegs))
    val mainInstructions     = program.main.statements map (
      s => transStatement (s, program.main.symbolTable, Registers.expressionRegs))

    val data = LabelTable.outputLabels
    val text = new CodeSegment()
                   .append(COMMENT("Function definitions"))
                   .extend(functionInstructions.flatten)
                   .append(NEWLINE)

                   .append(COMMENT("Stack setup"))
                   .append(MOV(R11, SP))
                   .append(SUB(SP, SP, ImmOperand(program.main.symbolTable.sizeInBytes)))
                   .append(NEWLINE)

                   .append(COMMENT("Main"))
                   .extend(mainInstructions.flatten)
                   .append(NEWLINE)

                   .append(COMMENT("Stack setup"))
                   .append(ADD(SP, SP, ImmOperand(program.main.symbolTable.sizeInBytes)))
                   .append(MOV(R0, ImmOperand(0)))

    new CodeSegment()
      .append(ARMSection("data"))
      .extend(data)

      .append(ARMSection("text"))
      .extend(StaticCode.outputStaticFunctions)
      .extend(text)
  }

}
