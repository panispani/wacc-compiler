package wacc.codegeneration

import wacc.arm._
import wacc.codegeneration.predefined.StaticCode
import wacc.constructs.Program

object TransProgram {
  def transProgram(program: Program): CodeSegment = {
    val functionInstructions = program.functions map (s => TransFunctions.transFunction(s, Registers.expressionRegs))
    val mainInstructions     = program.main.statements map (s => TransStatements.transStatement(s, Registers.expressionRegs))

    val (beginFrame, endFrame) = Macros.frame(program.main.symbolTable.sizeInBytes, isBranch = true)
    val data = StaticCode.outputData.extend(LabelTable.outputLabels)

    val text = CodeSegment()
                  .extend(COMMENT("Static code"))
                  .extend(StaticCode.outputFunctions)

                  .extend(COMMENT("Function definitions"))
                  .extend(functionInstructions.flatten : _*)

                  .extend(COMMENT("Main"))
                  .extend(GLOBAL("main"))
                  .extend(DefineLabel(Label("main")))
                  .extend(PUSH(Seq(LR)))
                  .extend(MOV(FP, SP))
                  .extend(beginFrame)
                  .extend(COMMENT("----------- MAIN  ------------"))
                  .extend(mainInstructions.flatten : _*)
                  .extend(COMMENT("----------- /MAIN ------------"))
                  .extend(MOV(R0, ImmOperand(0)))
                  .extend(endFrame)
                  .extend(POP(Seq(PC)))


    CodeSegment()
      .extend(ARMSection("data"))
      .extend(data)
      .extend(NEWLINE)

      .extend(ARMSection("text"))
      .extend(text)
  }

}
