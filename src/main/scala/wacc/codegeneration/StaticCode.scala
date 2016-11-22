package wacc

import wacc.arm._
import wacc.codegeneration._

package object StaticCode {
  def readFormat: AsciiData        = AsciiData(LabelAddress(Label()), "%d")
  def printFormat: AsciiData       = AsciiData(LabelAddress(Label()), "%%.*s")
  def emptyString: AsciiData       = AsciiData(LabelAddress(Label()), "")
  def staticData: CodeSegment      = new CodeSegment().extend(Seq(readFormat, printFormat, emptyString))
  def staticFunctions: CodeSegment = readFunction.extend(printFunction).extend(printLnFunction)

  def readFunctionLabel: Label = Label("read")
  def printFunctionLabel: Label = Label("print")
  def printLnFunctionLabel: Label = Label("print_ln")

  def readFunction: CodeSegment = {
    new CodeSegment()
      .append(DefineLabel(readFunctionLabel))
      .append(NEW_STACK_FRAME)
      .append(MOV(R1, R0))                      // Move address of variable into r1 as expected by scanf
      .append(LDR(R0, readFormat.labelAddress)) // Load the constant address of the format string into r1
      .append(BL(Label("scanf")))               // Call scanf with two arguments, r0 and r1
      .append(RETURN)
  }

  def printFunction: CodeSegment = {
    new CodeSegment()
      .append(DefineLabel(printFunctionLabel))
      .append(NEW_STACK_FRAME)
      .append(MOV(R1, R0))                        // Move the address of the string to print into r1 as expected by printf
      .append(LDR(R0, printFormat.labelAddress))  // Load the constant address of the format string into r0
      .append(BL(Label("printf")))                // Print string
      .append(MOV(R0, ImmOperand(0)))             // TODO: No idea
      .append(BL(Label("fflush")))                // TODO: Flush buffer?
      .append(RETURN)
  }

  def printLnFunction: CodeSegment = {
    new CodeSegment()
      .append(DefineLabel(printLnFunctionLabel))
      .append(NEW_STACK_FRAME)
      .append(LDR(R0, emptyString.labelAddress))  // Load the constant address of the empty string into r0
      .append(BL(Label("puts")))                  // Print empty string, appended with newline
      .append(MOV(R0, ImmOperand(0)))             // TODO: No idea
      .append(BL(Label("fflush")))                // TODO: Flush buffer?
      .append(RETURN)
  }
}
