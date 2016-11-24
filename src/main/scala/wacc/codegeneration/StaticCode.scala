package wacc

import wacc.arm._
import wacc.codegeneration._

package object StaticCode {

  def readFormat: AsciiData = AsciiData("\"%d\"")
  def printStringFormat: AsciiData = AsciiData("\"%.*s\\0\"")
  def printIntFormat: AsciiData = AsciiData("\"%d\\0\"")
  def emptyString: AsciiData = AsciiData("\"\\0\"")
  def trueString: AsciiData = AsciiData("\"true\\0\"")
  def falseString: AsciiData = AsciiData("\"false\\0\"")
  def divideOrModuleByZeroString: AsciiData = AsciiData("\"DivideByZeroError: divide or modulo by zero\"")
  def arrayNegativeIndex: AsciiData = AsciiData("\"ArrayIndexOutOfBoundsError: negative index\"")
  def arrayIndexTooLarge: AsciiData = AsciiData("\"ArrayIndexOutOfBoundsError: index too large\"")

  def staticFunctions: CodeSegment =
    readFunction
    .extend(printFunction)
    .extend(printIntFunction)
    .extend(printCharFunction)
    .extend(printBoolFunction)
    .extend(printLnFunction)
    .extend(CheckDivideByZero)
    .extend(throwRuntimeError)
    .extend(checkArrayBounds)

  def staticData: CodeSegment = CodeSegment()
    .append(DefineLabel(readFormatLabel))
    .append(readFormat)
    .append(DefineLabel(printStringFormatLabel))
    .append(printStringFormat)
    .append(DefineLabel(printIntFormatLabel))
    .append(printIntFormat)
    .append(DefineLabel(emptyStringLabel))
    .append(emptyString)
    .append(DefineLabel(trueStringLabel))
    .append(trueString)
    .append(DefineLabel(falseStringLabel))
    .append(falseString)
    .append(DefineLabel(DivideByZeroErrorLabel))
    .append(divideOrModuleByZeroString)
    .append(DefineLabel(arrayNegativeIndexLabel))
    .append(arrayNegativeIndex)
    .append(DefineLabel(arrayIndexTooLargeLabel))
    .append(arrayIndexTooLarge)


  def readFunctionLabel: Label = Label("read")
  def printFunctionLabel: Label = Label("print")
  def printLnFunctionLabel: Label = Label("print_ln")
  def printIntLabel: Label = Label("print_int")
  def printCharLabel: Label = Label("print_char") // standard C library function
  def printBoolLabel: Label = Label("print_bool_label")
  def printStringFormatLabel: Label = Label("print_string_format")
  def printIntFormatLabel: Label = Label("print_int_format")
  def readFormatLabel: Label = Label("read_format")
  def emptyStringLabel: Label = Label("empty_string")
  def trueStringLabel: Label = Label("true_string")
  def falseStringLabel: Label = Label("false_string")
  def throwRuntimeErrorLabel: Label = Label("throw_runtime_error")
  def DivideByZeroErrorLabel: Label = Label("divide_by_zero")
  def checkDivideByZeroLabel: Label = Label("check_divide_by_zero")
  def divisionLabel: Label = Label("__aeabi_idiv")
  def moduleLabel: Label = Label("__aeabi_idivmod")
  def arrayNegativeIndexLabel: Label = Label("array_negative_index")
  def arrayIndexTooLargeLabel: Label = Label("array_index_too_large")
  def checkArrayBoundsLabel: Label = Label("check_array_bounds")

  def readFunction: CodeSegment = {
    CodeSegment()
      .append(DefineLabel(readFunctionLabel))
      .append(NEW_STACK_FRAME)
      .append(MOV(R1, R0)) // Move address of variable into r1 as expected by scanf
      .append(LDR(R0, LabelAddress(readFormatLabel))) // Load the constant address of the format string into r1
      .append(BL(Label("scanf"))) // Call scanf with two arguments, r0 and r1
      .append(RETURN)
  }

  def printFunction: CodeSegment = {
    CodeSegment()
      .append(DefineLabel(printFunctionLabel))
      .append(NEW_STACK_FRAME)
      .append(LDR(R1, RegisterAddress(R0, 0))) // Move the address of the string to print into r1 as expected by printf
      .append(ADD(R2, R0, ImmOperand(4)))
      .append(LDR(R0, LabelAddress(printStringFormatLabel))) // Load the constant address of the format string into r0
      .append(ADD(R0, R0, ImmOperand(4)))
      .append(BL(Label("printf"))) // Print string
      .append(MOV(R0, ImmOperand(0)))
      .append(BL(Label("fflush")))
      .append(RETURN)
  }

  def CheckDivideByZero: CodeSegment = {
    CodeSegment()
      .append(DefineLabel(checkDivideByZeroLabel))
      .append(NEW_STACK_FRAME)
      .append(CMP(R1, ImmOperand(0))) // Check if the dividend is 0
      .append(LDR(R0, LabelAddress(DivideByZeroErrorLabel), EQ)) //If it is 0, load in R0 the error string
      .append(BL(throwRuntimeErrorLabel, EQ)) //Branch to the function to throw a runtime error
      .append(RETURN)
  }

  def throwRuntimeError: CodeSegment = {
    CodeSegment()
      .append(DefineLabel(throwRuntimeErrorLabel))
      .append(BL(printFunctionLabel))
      .append(MOV(R0, ImmOperand(-1)))
      .append(BL(Label("exit")))
  }

  def printIntFunction: CodeSegment = {
    CodeSegment()
        .append(DefineLabel(printIntLabel))
        .append(NEW_STACK_FRAME)
        .append(MOV(R1, R0))
        .append(LDR(R0, LabelAddress(printIntFormatLabel)))
        .append(ADD(R0, R0, ImmOperand(4)))
        .append(BL(Label("printf")))
        .append(MOV(R0, ImmOperand(0)))
        .append(BL(Label("fflush")))
        .append(RETURN)
  }

  def printCharFunction: CodeSegment = {
    CodeSegment()
      .append(DefineLabel(printCharLabel))
      .append(BL(Label("putchar")))
  }

  def printBoolFunction: CodeSegment = {
    CodeSegment()
      .append(DefineLabel(printBoolLabel))
      .append(NEW_STACK_FRAME)
      .append(CMP(R0, ImmOperand(0)))
      .append(LDR(R0, LabelAddress(trueStringLabel), NE))
      .append(LDR(R0, LabelAddress(falseStringLabel), EQ))
      .append(ADD(R0, R0, ImmOperand(4)))
      .append(BL(Label("printf")))
      .append(MOV(R0, ImmOperand(0)))
      .append(BL(Label("fflush")))
      .append(RETURN)
  }


  def printLnFunction: CodeSegment = {
    CodeSegment()
      .append(DefineLabel(printLnFunctionLabel))
      .append(NEW_STACK_FRAME)
      .append(LDR(R0, LabelAddress(emptyStringLabel)))  // Load the constant address of the empty string into r0
      .append(ADD(R0, R0, ImmOperand(4)))
      .append(BL(Label("puts")))                  // Print empty string, appended with newline
      .append(MOV(R0, ImmOperand(0)))
      .append(BL(Label("fflush")))
      .append(RETURN)
  }

  def checkArrayBounds: CodeSegment = {
    CodeSegment()
      .append(DefineLabel(checkArrayBoundsLabel))
      .append(CMP(R0, ImmOperand(0)))
      .append(LDR(R0, LabelAddress(arrayNegativeIndexLabel), LT))
      .append(BL(throwRuntimeErrorLabel, LT))
      .append(LDR(R1, RegisterAddress(R1, 0)))
      .append(CMP(R0, R1))
      .append(LDR(R0, LabelAddress(arrayIndexTooLargeLabel), CS))
      .append(BL(throwRuntimeErrorLabel, CS))
      .append(RETURN)
  }
}
