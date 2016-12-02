package wacc.codegeneration

import wacc.arm._

object StaticCode {


  def intFormat: AsciiData = AsciiData("\"%d\\0\"")
  def charReadFormat: AsciiData = AsciiData("\" %c\\0\"")
  def printStringFormat: AsciiData = AsciiData("\"%.*s\\0\"")
  def emptyString: AsciiData = AsciiData("\"\\0\"")
  def trueString: AsciiData = AsciiData("\"true\\0\"")
  def falseString: AsciiData = AsciiData("\"false\\0\"")
  def divideOrModuleByZeroString: AsciiData = AsciiData("\"DivideByZeroError: divide or modulo by zero\"")
  def arrayNegativeIndex: AsciiData = AsciiData("\"ArrayIndexOutOfBoundsError: negative index\"")
  def arrayIndexTooLarge: AsciiData = AsciiData("\"ArrayIndexOutOfBoundsError: index too large\"")
  def overflowError: AsciiData = AsciiData("\"OverflowError: the result is too small/large to store in a 4-byte signed-integer.\"")
  def nullReferenceError: AsciiData = AsciiData("\"NullReferenceError: dereference a null reference\"")
  def printReferenceFormat: AsciiData = AsciiData("\"%p\\0\"")

  def staticFunctions: CodeSegment =
    readIntFunction.extend(readCharFunction).extend(printFunction).extend(printIntFunction)
    .extend(printCharFunction).extend(printBoolFunction).extend(printLnFunction)
    .extend(CheckDivideByZero)
    .extend(throwRuntimeError)
    .extend(checkArrayBounds)
    .extend(printReferenceFunction)
    .extend(throwOverflowError)
    .extend(freePairFunction)
    .extend(checkNullPointerFunction)
    .extend(concatinateStrings)

  def staticData: CodeSegment = CodeSegment()
    .extend(DefineLabel(intFormatLabel))
    .extend(intFormat)
    .extend(DefineLabel(charReadFormatLabel))
    .extend(charReadFormat)
    .extend(DefineLabel(printStringFormatLabel))
    .extend(printStringFormat)
    .extend(DefineLabel(emptyStringLabel))
    .extend(emptyString)
    .extend(DefineLabel(trueStringLabel))
    .extend(trueString)
    .extend(DefineLabel(falseStringLabel))
    .extend(falseString)
    .extend(DefineLabel(DivideByZeroErrorLabel))
    .extend(divideOrModuleByZeroString)
    .extend(DefineLabel(arrayNegativeIndexLabel))
    .extend(arrayNegativeIndex)
    .extend(DefineLabel(arrayIndexTooLargeLabel))
    .extend(arrayIndexTooLarge)
    .extend(DefineLabel(printReferenceLabel))
    .extend(printReferenceFormat)
    .extend(DefineLabel(throwOverflowErrorLabel))
    .extend(overflowError)
    .extend(DefineLabel(nullReferenceErrorLabel))
    .extend(nullReferenceError)

  def readIntLabel: Label = Label("read_int")
  def readCharLabel: Label = Label("read_char")
  def printIntLabel: Label = Label("print_int")
  def printCharLabel: Label = Label("print_char")
  def printBoolLabel: Label = Label("print_bool_label")
  def printFunctionLabel: Label = Label("print")
  def printLnFunctionLabel: Label = Label("print_ln")

  def printStringFormatLabel: Label = Label("print_string_format")
  def intFormatLabel: Label = Label("int_format")
  def charReadFormatLabel: Label = Label("char_read_format")
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
  def printReferenceLabel: Label = Label("print_reference")
  def printReferenceFunctionLabel: Label = Label("print_reference_function")
  def throwOverflowErrorFunctionLabel: Label = Label("throw_overflow_error_function")
  def throwOverflowErrorLabel: Label = Label("throw_overflow_error")
  def nullReferenceErrorLabel: Label = Label("null_reference_error")
  def freePairLabel: Label = Label("free_pair")
  def checkNullPointerFunctionLabel: Label = Label("check_null_pointer")
  def concatinateStringsLabel: Label = Label("concatinate_strings")

  def concatinateStrings: CodeSegment = {
    //R0 stringA, R1 stringB
    CodeSegment(
      DefineLabel(concatinateStringsLabel),
      NEW_STACK_FRAME,
      PUSH(Seq(R0)),                  // Save stringA
      BL(Label("strlen")),
      MOV(R2, R0),                    // R2 = len(stringA)
      MOV(R0, R1),
      BL(Label("strlen")),            // R0 = len(stringB)
      ADD(R0, R0, R2),                // R0 = len(stringA) + len(stringB)
      ADD(R0, R0, ImmOperand(1)),     // R0 = len(stringA) + len(stringB) + 1
      BL(Label("malloc")),            // R0 = newstring*
      POP(Seq(R0)),                   // Restore stringA
      BL(Label("strcat")),            // *newstring += stringA
      MOV(R0, R1),
      BL(Label("strcat")),            // *newstring += stringB
      RETURN
    )
  }

  def readIntFunction: CodeSegment = {
    CodeSegment()
      .extend(DefineLabel(readIntLabel))
      .extend(NEW_STACK_FRAME)
      .extend(MOV(R1, R0)) // Move address of variable into r1 as expected by scanf
      .extend(LDR(R0, LabelAddress(intFormatLabel))) // Load the constant address of the format string into r1
      .extend(ADD(R0, R0, ImmOperand(4)))
      .extend(BL(Label("scanf"))) // Call scanf with two arguments, r0 and r1
      .extend(RETURN)
  }

  def readCharFunction: CodeSegment = {
    CodeSegment()
      .extend(DefineLabel(readCharLabel))
      .extend(NEW_STACK_FRAME)
      .extend(MOV(R1, R0)) // Move address of variable into r1 as expected by scanf
      .extend(LDR(R0, LabelAddress(charReadFormatLabel))) // Load the constant address of the format string into r1
      .extend(ADD(R0, R0, ImmOperand(4)))
      .extend(BL(Label("scanf"))) // Call scanf with two arguments, r0 and r1
      .extend(RETURN)
  }

  def printFunction: CodeSegment = {
    CodeSegment()
      .extend(DefineLabel(printFunctionLabel))
      .extend(NEW_STACK_FRAME)
      .extend(LDR(R1, RegisterAddress(R0, 0))) // Move the address of the string to print into r1 as expected by printf
      .extend(ADD(R2, R0, ImmOperand(4)))
      .extend(LDR(R0, LabelAddress(printStringFormatLabel))) // Load the constant address of the format string into r0
      .extend(ADD(R0, R0, ImmOperand(4)))
      .extend(BL(Label("printf"))) // Print string
      .extend(MOV(R0, ImmOperand(0)))
      .extend(BL(Label("fflush")))
      .extend(RETURN)
  }

  def CheckDivideByZero: CodeSegment = {
    CodeSegment()
      .extend(DefineLabel(checkDivideByZeroLabel))
      .extend(NEW_STACK_FRAME)
      .extend(CMP(R1, ImmOperand(0))) // Check if the dividend is 0
      .extend(LDR(R0, LabelAddress(DivideByZeroErrorLabel), EQ)) //If it is 0, load in R0 the error string
      .extend(BL(throwRuntimeErrorLabel, EQ)) //Branch to the function to throw a runtime error
      .extend(RETURN)
  }

  def throwRuntimeError: CodeSegment = {
    CodeSegment()
      .extend(DefineLabel(throwRuntimeErrorLabel))
      .extend(BL(printFunctionLabel))
      .extend(MOV(R0, ImmOperand(-1)))
      .extend(BL(Label("exit")))
  }

  def printIntFunction: CodeSegment = {
    CodeSegment()
        .extend(DefineLabel(printIntLabel))
        .extend(NEW_STACK_FRAME)
        .extend(MOV(R1, R0))
        .extend(LDR(R0, LabelAddress(intFormatLabel)))
        .extend(ADD(R0, R0, ImmOperand(4)))
        .extend(BL(Label("printf")))
        .extend(MOV(R0, ImmOperand(0)))
        .extend(BL(Label("fflush")))
        .extend(RETURN)
  }

  def printCharFunction: CodeSegment = {
    CodeSegment()
      .extend(DefineLabel(printCharLabel))
      .extend(NEW_STACK_FRAME)
      .extend(BL(Label("putchar")))
      .extend(RETURN)
  }

  def printBoolFunction: CodeSegment = {
    CodeSegment()
      .extend(DefineLabel(printBoolLabel))
      .extend(NEW_STACK_FRAME)
      .extend(CMP(R0, ImmOperand(0)))
      .extend(LDR(R0, LabelAddress(trueStringLabel), NE))
      .extend(LDR(R0, LabelAddress(falseStringLabel), EQ))
      .extend(ADD(R0, R0, ImmOperand(4)))
      .extend(BL(Label("printf")))
      .extend(MOV(R0, ImmOperand(0)))
      .extend(BL(Label("fflush")))
      .extend(RETURN)
  }


  def printLnFunction: CodeSegment = {
    CodeSegment()
      .extend(DefineLabel(printLnFunctionLabel))
      .extend(NEW_STACK_FRAME)
      .extend(LDR(R0, LabelAddress(emptyStringLabel)))  // Load the constant address of the empty string into r0
      .extend(ADD(R0, R0, ImmOperand(4)))
      .extend(BL(Label("puts")))                  // Print empty string, appended with newline
      .extend(MOV(R0, ImmOperand(0)))
      .extend(BL(Label("fflush")))
      .extend(RETURN)
  }

  def printReferenceFunction: CodeSegment = {
    CodeSegment()
      .extend(DefineLabel(printReferenceFunctionLabel))
      .extend(NEW_STACK_FRAME)
      .extend(MOV(R1, R0))
      .extend(LDR(R0, LabelAddress(printReferenceLabel)))  // Load the constant address of the empty string into r0
      .extend(ADD(R0, R0, ImmOperand(4)))
      .extend(BL(Label("printf")))                  // Print empty string, appended with newline
      .extend(MOV(R0, ImmOperand(0)))
      .extend(BL(Label("fflush")))
      .extend(RETURN)
  }

  def checkArrayBounds: CodeSegment = {
    CodeSegment()
      .extend(DefineLabel(checkArrayBoundsLabel))
      .extend(NEW_STACK_FRAME)
      .extend(CMP(R0, ImmOperand(0)))
      .extend(LDR(R0, LabelAddress(arrayNegativeIndexLabel), LT))
      .extend(BL(throwRuntimeErrorLabel, LT))
      .extend(LDR(R1, RegisterAddress(R1, 0)))
      .extend(CMP(R0, R1))
      .extend(LDR(R0, LabelAddress(arrayIndexTooLargeLabel), CS))
      .extend(BL(throwRuntimeErrorLabel, CS))
      .extend(RETURN)
  }

  def checkNullPointerFunction: CodeSegment = {
    CodeSegment()
      .extend(DefineLabel(checkNullPointerFunctionLabel))
      .extend(NEW_STACK_FRAME)
      .extend(CMP(R0, ImmOperand(0)))
      .extend(LDR(R0, LabelAddress(nullReferenceErrorLabel), EQ))
      .extend(B(throwRuntimeErrorLabel, EQ))
      .extend(RETURN)
  }

  def freePairFunction: CodeSegment = {
    CodeSegment()
      .extend(DefineLabel(freePairLabel))
      .extend(NEW_STACK_FRAME)
      .extend(BL(checkNullPointerFunctionLabel))
      .extend(PUSH(Seq(R0)))
      .extend(LDR(R0, RegisterAddress(R0, 0)))
      .extend(BL(Label("free")))
      .extend(LDR(R0, RegisterAddress(SP)))
      .extend(LDR(R0, RegisterAddress(R0, 4)))
      .extend(BL(Label("free")))
      .extend(POP(Seq(R0)))
      .extend(BL(Label("free")))
      .extend(RETURN)

//    PUSH {lr}
//    41		CMP r0, #0
//    42		LDREQ r0, =msg_0
//    43		BEQ p_throw_runtime_error
//    44		PUSH {r0}
//    45		LDR r0, [r0]
//    46		BL free
//    47		LDR r0, [sp]
//    48		LDR r0, [r0, #4]
//    49		BL free
//    50		POP {r0}
//    51		BL free
//    52		POP {pc}
  }

  def throwOverflowError: CodeSegment = {
    CodeSegment()
      .extend(DefineLabel(throwOverflowErrorFunctionLabel))
      .extend(LDR(R0, LabelAddress(throwOverflowErrorLabel)))
      .extend(BL(throwRuntimeErrorLabel))
  }
}
