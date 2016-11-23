package wacc

import wacc.arm._
import wacc.codegeneration._

package object StaticCode {
  def readFormat: AsciiData        = AsciiData("\"%d\"")
  def printFormat: AsciiData       = AsciiData("\"%%.*s\"")
  def emptyString: AsciiData       = AsciiData("\"\"")
  def divideOrModuleByZeroString: AsciiData = AsciiData("\"DivideByZeroError: divide or modulo by zeroz\"")
  def arrayNegativeIndex: AsciiData = AsciiData("\"ArrayIndexOutOfBoundsError: negative index\"")
  def arrayIndexTooLarge: AsciiData = AsciiData("\"ArrayIndexOutOfBoundsError: index too large\"")
  def staticFunctions: CodeSegment =
    readFunction
    .extend(printFunction)
    .extend(printLnFunction)
    .extend(CheckDivideByZero)
    .extend(throwRuntimeError)
    .extend(checkArrayBounds)

  def staticData: CodeSegment = new CodeSegment()
    .append(DefineLabel(readFormatLabel))
    .append(readFormat)
    .append(DefineLabel(printFormatLabel))
    .append(printFormat)
    .append(DefineLabel(emptyStringLabel))
    .append(emptyString)
    .append(DefineLabel(DivideByZeroErrorLabel))
    .append(divideOrModuleByZeroString)
    .append(DefineLabel(arrayNegativeIndexLabel))
    .append(arrayNegativeIndex)
    .append(DefineLabel(arrayIndexTooLargeLabel))
    .append(arrayIndexTooLarge)

  def readFunctionLabel: Label = Label("read")
  def printFunctionLabel: Label = Label("print")
  def printLnFunctionLabel: Label = Label("print_ln")
  def readFormatLabel: Label = Label("read_format")
  def printFormatLabel: Label = Label("print_format")
  def emptyStringLabel: Label = Label("empty_string")
  def throwRuntimeErrorLabel: Label = Label("throw_runtime_error")
  def DivideByZeroErrorLabel: Label = Label("divide_by_zero")
  def checkDivideByZeroLabel: Label = Label("check_divide_by_zero")
  def divisionLabel: Label = Label("__aeabi_idiv")
  def moduleLabel: Label = Label("__aeabi_idivmod")
  def arrayNegativeIndexLabel: Label = Label("array_negative_index")
  def arrayIndexTooLargeLabel: Label = Label("array_index_too_large")
  def checkArrayBoundsLabel: Label = Label("check_array_bounds")

  def readFunction: CodeSegment = {
    new CodeSegment()
      .append(DefineLabel(readFunctionLabel))
      .append(NEW_STACK_FRAME)
      .append(MOV(R1, R0))                      // Move address of variable into r1 as expected by scanf
      .append(LDR(R0, LabelAddress(readFormatLabel))) // Load the constant address of the format string into r1
      .append(BL(Label("scanf")))               // Call scanf with two arguments, r0 and r1
      .append(RETURN)
  }

  def printFunction: CodeSegment = {
    new CodeSegment()
      .append(DefineLabel(printFunctionLabel))
      .append(NEW_STACK_FRAME)
      .append(MOV(R1, R0))                        // Move the address of the string to print into r1 as expected by printf
      .append(LDR(R0, LabelAddress(printFormatLabel)))  // Load the constant address of the format string into r0
      .append(BL(Label("printf")))                // Print string
      .append(MOV(R0, ImmOperand(0)))             // TODO: No idea
      .append(BL(Label("fflush")))                // TODO: Flush buffer?
      .append(RETURN)
  }

  def CheckDivideByZero: CodeSegment = {
    new CodeSegment()
      .append(DefineLabel(checkDivideByZeroLabel))
      .append(NEW_STACK_FRAME)
      .append(CMP(R1, ImmOperand(0))) // Check if the dividend is 0
      .append(LDR(R0, LabelAddress(DivideByZeroErrorLabel), EQ)) //If it is 0, load in R0 the error string
      .append(BL(throwRuntimeErrorLabel, EQ)) //Branch to the function to throw a runtime error
      .append(RETURN)
  }

  def throwRuntimeError: CodeSegment = {
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
      .append(LDR(R0, LabelAddress(emptyStringLabel)))  // Load the constant address of the empty string into r0
      .append(BL(Label("puts")))                  // Print empty string, appended with newline
      .append(MOV(R0, ImmOperand(0)))             // TODO: No idea
      .append(BL(Label("fflush")))                // TODO: Flush buffer?
      .append(RETURN)
  }

  def checkArrayBounds: CodeSegment = {
    new CodeSegment()
      .append(DefineLabel(checkArrayBoundsLabel))
      .append(CMP(R0, ImmOperand(0)))
      .append(LDR(R0, LabelAddress(arrayNegativeIndexLabel), LT))
      .append(BL(throwRuntimeErrorLabel, LT))
      .append(LDR(R1, RegisterAddress(R1, 0)))
      .append(CMP(R0, R1))
      .append(LDR(R0, LabelAddress(arrayIndexTooLargeLabel), CS))
      .append(BL(throwRuntimeErrorLabel, CS))
      .append(RETURN)

//    PUSH {lr}
//    50		CMP r0, #0
//    51		LDRLT r0, =msg_0
//    52		BLLT p_throw_runtime_error
//    53		LDR r1, [r1]
//    54		CMP r0, r1
//    55		LDRCS r0, =msg_1
//    56		BLCS p_throw_runtime_error
//    57		POP {pc}
  }
}
