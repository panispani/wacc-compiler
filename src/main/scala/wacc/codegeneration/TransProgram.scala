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

    val (beginFrame, endFrame) = Macros.frame(program.main.symbolTable.sizeInBytes)
    val data = new CodeSegment().extend(StaticCode.staticData).extend(LabelTable.outputLabels)
    val text = new CodeSegment()
                  .append(COMMENT("Static code"))
                  .extend(StaticCode.staticFunctions)

                  .append(COMMENT("Function definitions"))
                  .extend(functionInstructions.flatten)

                  .append(COMMENT("Main"))
                  .append(GLOBAL("main"))
                  .append(DefineLabel(Label("main")))
                  .append(COMMENT("Stack setup"))

                  .extend(beginFrame)
                  .extend(mainInstructions.flatten)
                  .append(COMMENT("Stack setup"))
                  .append(MOV(R0, ImmOperand(0)))
                  .extend(endFrame)

    new CodeSegment()
      .append(ARMSection("data"))
      .extend(data)
      .append(NEWLINE)

      .append(ARMSection("text"))
      .extend(text)
  }

}
