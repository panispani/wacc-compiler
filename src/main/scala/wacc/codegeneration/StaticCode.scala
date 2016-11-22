package wacc

import wacc.arm._
import wacc.codegeneration._

package object StaticCode {
  def readFormat: AsciiData        = AsciiData(LabelAddress(Label()), "%d")
  def printFormat: AsciiData       = AsciiData(LabelAddress(Label()), "%%.*s")
  def emptyString: AsciiData       = AsciiData(LabelAddress(Label()), "")
  def divideOrModuleByZeroString: AsciiData = AsciiData(LabelAddress(Label()), "DivideByZeroError: divide or modulo by zero\n")
  def staticData: CodeSegment      = new CodeSegment().extend(Seq(readFormat, printFormat, emptyString, divideOrModuleByZeroString))
  def staticFunctions: CodeSegment = readFunction.extend(printFunction).extend(printLnFunction)

  def readFunctionLabel: Label = Label("read")
  def printFunctionLabel: Label = Label("print")
  def printLnFunctionLabel: Label = Label("print_ln")
  def throwRuntimeErrorLabel: Label = Label("throw_runtime_error")
  def checkDivideByZeroLabel: Label = Label("check_divide_by_zero")
  def divisionLabel: Label = Label("__aeabi_idiv")
  def moduleLabel: Label = Label("__aeabi_idivmod")

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

  def outputCheckDivideByZero: CodeSegment = {
    new CodeSegment()
      .append(DefineLabel(checkDivideByZeroLabel))
      .append(NEW_STACK_FRAME)
      .append(CMP(R1, ImmOperand(0))) // Check if the dividend is 0
      .append(LDR(R0, divideOrModuleByZeroString.labelAddress, EQ)) //Todo: Label Address needs to be dynamic //If it is 0, load in R0 the error string
      .append(BL(throwRuntimeErrorLabel, EQ)) //Branch to the function to throw a runtime error
      .append(RETURN)
  }

  def outputThrowRuntimeError: CodeSegment = {
    new CodeSegment()
      .append(DefineLabel(throwRuntimeErrorLabel))
      .append(BL(printFunctionLabel))
      .append(MOV(R0, ImmOperand(-1)))
      .append(BL(Label("exit")))
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
