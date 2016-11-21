package wacc

import wacc.codegeneration._

package object StaticCode {
  def readFormat: AsciiData     = AsciiData(LabelAddress("_read_format"), "%d\u0000")
  def staticData: CodeSegment   = new CodeSegment().append(readFormat)
  def readFunctionLabel: String = "read_4_bytes"

  def outputStaticFunctions: CodeSegment = {
    outputReadFunction
  }

  def outputReadFunction: CodeSegment = {
    new CodeSegment()
      .append(DefineLabel(Label(readFunctionLabel)))
      .append(PUSH(Seq(LR)))
      .append(MOV(R1, R0))                      // Move address of variable into r1 as expected by scanf
      .append(LDR(R0, readFormat.labelAddress)) // Load the constant address of the format string into r1
      .append(ADD(R0, R0, ImmOperand(4)))       // TODO: Weird magic
      .append(BL(Label("scanf")))               // Call scanf with two arguments, r0 and r1
      .append(POP(Seq(PC)))
  }
}
