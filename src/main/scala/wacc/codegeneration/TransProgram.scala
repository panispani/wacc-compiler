package wacc.codegeneration

import wacc.arm._
import wacc.constructs.Program

object TransProgram {
  def transProgram(program: Program): CodeSegment = {
    val functionInstructions = program.functions map (s => TransFunctions.transFunction (s, Registers.expressionRegs))
    val mainInstructions     = program.main.statements map (
      s => TransStatements.transStatement(s, Registers.expressionRegs))

    val (beginFrame, endFrame) = Macros.frame(program.main.symbolTable.sizeInBytes)
    val data = StaticCode.staticData.extend(LabelTable.outputLabels)
    val text = CodeSegment()
                  .extend(COMMENT("Static code"))
                  .extend(StaticCode.staticFunctions)

                  .extend(COMMENT("Function definitions"))
                  .extend(functionInstructions.flatten)

                  .extend(COMMENT("Main"))
                  .extend(GLOBAL("main"))
                  .extend(DefineLabel(Label("main")))
                  .extend(PUSH(Seq(LR)))
                  .extend(beginFrame)
                  .extend(COMMENT("----------- MAIN  ------------"))
                  .extend(mainInstructions.flatten)
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
