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
                   .append(COMMENT("Static code"))
                   .extend(StaticCode.staticFunctions)

                   .append(COMMENT("Function definitions"))
                   .extend(functionInstructions.flatten)

                   .append(COMMENT("Stack setup"))
                   .append(MOV(BP, SP))
                   .append(SUB(SP, SP, ImmOperand(program.main.symbolTable.sizeInBytes)))

                   .append(COMMENT("Main"))
                   .append(GLOBAL("main"))
                   .append(DefineLabel(Label("main")))
                   .extend(mainInstructions.flatten)

                   .append(COMMENT("Stack setup"))
                   .append(ADD(SP, SP, ImmOperand(program.main.symbolTable.sizeInBytes)))
                   .append(MOV(R0, ImmOperand(0)))

    new CodeSegment()
      .append(ARMSection("data"))
      .extend(data)
      .append(NEWLINE)

      .append(ARMSection("text"))
      .extend(text)
  }

}
