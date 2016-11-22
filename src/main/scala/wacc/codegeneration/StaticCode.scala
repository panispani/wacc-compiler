package wacc

import wacc.arm._
import wacc.codegeneration._

package object StaticCode {
  def readFormat: AsciiData     = AsciiData(LabelAddress("_read_format"), "%d")
  def printFormat: AsciiData    = AsciiData(LabelAddress("_print_format"), "%%.*s")
  def staticData: CodeSegment   = new CodeSegment()
                                     .append(readFormat)


  //TODO: Perhaps these need to be generated with the LabelCreator to avoid clashes
  def readFunctionLabel: String = "read_4_bytes"
  def printFunctionLabel: String = "print_string"

  /* TODO: This will be called and embedded in every program we compile, or we do something smarter and only output
     the functions which actually get called at least once */
  def outputStaticFunctions: CodeSegment = {
    outputReadFunction
  }

  def outputReadFunction: CodeSegment = {
    new CodeSegment()
      .append(DefineLabel(Label(readFunctionLabel)))
      .append(NEW_STACK_FRAME)
      .append(MOV(R1, R0))                      // Move address of variable into r1 as expected by scanf
      .append(LDR(R0, readFormat.labelAddress)) // Load the constant address of the format string into r1
      .append(BL(Label("scanf")))               // Call scanf with two arguments, r0 and r1
      .append(RETURN)
  }

  def outputPrintFunction: CodeSegment = {
    new CodeSegment()
      .append(DefineLabel(Label(printFunctionLabel)))
      .append(NEW_STACK_FRAME)
      .append(MOV(R1, R0))                        // Move the address of the string to print into r1 as expected by printf
      .append(LDR(R0, printFormat.labelAddress))  // Load the constant address of the format string into r0
      .append(BL(Label("printf")))                // Print string
      .append(MOV(R0, ImmOperand(0)))             // TODO: No idea
      .append(BL(Label("fflush")))                // TODO: Flush buffer?
      .append(RETURN)
  }
}
