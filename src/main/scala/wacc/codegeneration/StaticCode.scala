package wacc.codegeneration

import wacc.arm._

object StaticCode {

  val staticFunctionMap: collection.immutable.Map[CodeSegment, Label] = collection.immutable.Map(
    readIntFunction          -> Label("read_int"),
    readCharFunction         -> Label("read_char"),
    printIntFunction         -> Label("print_int"),
    printCharFunction        -> Label("print_char"),
    printBoolFunction        -> Label("print_bool_label"),
    printFunction            -> Label("print"),
    printLnFunction          -> Label("print_ln"),
    printReferenceFunction   -> Label("print_reference_function"),
    checkNullPointerFunction -> Label("check_null_pointer"),
    freePairFunction         -> Label("free_pair"),
    checkArrayBounds         -> Label("check_array_bounds"),
    CheckDivideByZero        -> Label("check_divide_by_zero"),
    throwRuntimeError        -> Label("throw_runtime_error")

    // identity                 -> Label("__aeabi_idiv"),
    // identity                 -> Label("__aeabi_idivmod"),
    // identity                 -> Label("thr ow_overflow_error_function"),
    // identity                 -> Label("thr ow_overflow_error")
  )

  val staticDataMap: collection.immutable.Map[AsciiData, Label] = collection.immutable.Map(
    printStringFormat          -> Label("print_string_format"),
    intFormat                  -> Label("int_format"),
    charReadFormat             -> Label("char_read_format"),

    emptyString                -> Label("empty_string"),
    trueString                 -> Label("true_string"),
    falseString                -> Label("false_string"),

    divideOrModuleByZeroString -> Label("divide_by_zero"),
    arrayNegativeIndex         -> Label("array_negative_index"),
    arrayIndexTooLarge         -> Label("array_index_too_large"),
    printReferenceFormat       -> Label("print_reference"),
    nullReferenceError         -> Label("null_reference_error")
  )

  def outputFunctions: CodeSegment = {
    val instructions = staticFunctionMap map {
      case (function, label) =>
        CodeSegment()
          .extend(DefineLabel(label))
          .extend(NEW_STACK_FRAME)
          .extend(function)
          .extend(RETURN)
    }

    CodeSegment().extend(derp)
  }

  def outputData: CodeSegment = {
    val instructions = staticDataMap map {
      case (data, label) =>
        CodeSegment()
          .extend(DefineLabel(label))
          .extend(data)
    }

    CodeSegment().extend(instructions.flatten)
  }

  def intFormat:                  AsciiData = AsciiData("\"%d\\0\"")
  def charReadFormat:             AsciiData = AsciiData("\" %c\\0\"")
  def printStringFormat:          AsciiData = AsciiData("\"%.*s\\0\"")
  def emptyString:                AsciiData = AsciiData("\"\\0\"")
  def trueString:                 AsciiData = AsciiData("\"true\\0\"")
  def falseString:                AsciiData = AsciiData("\"false\\0\"")
  def divideOrModuleByZeroString: AsciiData = AsciiData("\"DivideByZeroError: divide or modulo by zero\"")
  def arrayNegativeIndex:         AsciiData = AsciiData("\"ArrayIndexOutOfBoundsError: negative index\"")
  def arrayIndexTooLarge:         AsciiData = AsciiData("\"ArrayIndexOutOfBoundsError: index too large\"")
  def overflowError:              AsciiData = AsciiData("\"OverflowError: the result is too small/large to store in a 4-byte signed-integer.\"")
  def nullReferenceError:         AsciiData = AsciiData("\"NullReferenceError: dereference a null reference\"")
  def printReferenceFormat:       AsciiData = AsciiData("\"%p\\0\"")

  def getStaticData(data: AsciiData): Label = {
    staticDataMap.getOrElse(data, Label(""))
  }

  def getStaticFunction(function: CodeSegment): Label = {
    staticFunctionMap.getOrElse(function, Label(""))
  }

  def readIntFunction: CodeSegment = {
    CodeSegment()
      .extend(MOV(R1, R0)) // Move address of variable into r1 as expected by scanf
      .extend(LDR(R0, LabelAddress(getStaticData(intFormat)))) // Load the constant address of the format string into r1
      .extend(ADD(R0, R0, ImmOperand(4)))
      .extend(BL(Label("scanf"))) // Call scanf with two arguments, r0 and r1
  }

  def readCharFunction: CodeSegment = {
    CodeSegment()
      .extend(MOV(R1, R0)) // Move address of variable into r1 as expected by scanf
      .extend(LDR(R0, LabelAddress(getStaticData(charReadFormat)))) // Load the constant address of the format string into r1
      .extend(ADD(R0, R0, ImmOperand(4)))
      .extend(BL(Label("scanf"))) // Call scanf with two arguments, r0 and r1
  }

  def printFunction: CodeSegment = {
    CodeSegment()
      .extend(LDR(R1, RegisterAddress(R0, 0))) // Move the address of the string to print into r1 as expected by printf
      .extend(ADD(R2, R0, ImmOperand(4)))
      .extend(LDR(R0, LabelAddress(getStaticData(printStringFormat)))) // Load the constant address of the format string into r0
      .extend(ADD(R0, R0, ImmOperand(4)))
      .extend(BL(Label("printf"))) // Print string
      .extend(MOV(R0, ImmOperand(0)))
      .extend(BL(Label("fflush")))
  }

  def CheckDivideByZero: CodeSegment = {
    CodeSegment()
      .extend(CMP(R1, ImmOperand(0))) // Check if the dividend is 0
      .extend(LDR(R0, LabelAddress(getStaticData(divideOrModuleByZeroString)), EQ)) //If it is 0, load in R0 the error string
      .extend(BL(getStaticFunction(throwRuntimeError), EQ)) //Branch to the function to throw a runtime error
  }

  def throwRuntimeError: CodeSegment = {
    CodeSegment()
      .extend(MOV(R0, ImmOperand(-1)))
      .extend(BL(Label("exit")))
  }

  def printIntFunction: CodeSegment = {
    CodeSegment()
        .extend(MOV(R1, R0))
        .extend(LDR(R0, LabelAddress(getStaticData(intFormat))))
        .extend(ADD(R0, R0, ImmOperand(4)))
        .extend(BL(Label("printf")))
        .extend(MOV(R0, ImmOperand(0)))
        .extend(BL(Label("fflush")))
  }

  def printCharFunction: CodeSegment = {
    CodeSegment()
      .extend(BL(Label("putchar")))
  }

  def printBoolFunction: CodeSegment = {
    CodeSegment()
      .extend(CMP(R0, ImmOperand(0)))
      .extend(LDR(R0, LabelAddress(getStaticData(trueString)), NE))
      .extend(LDR(R0, LabelAddress(getStaticData(falseString)), EQ))
      .extend(ADD(R0, R0, ImmOperand(4)))
      .extend(BL(Label("printf")))
      .extend(MOV(R0, ImmOperand(0)))
      .extend(BL(Label("fflush")))
  }


  def printLnFunction: CodeSegment = {
    CodeSegment()
      .extend(LDR(R0, LabelAddress(getStaticData(emptyString))))  // Load the constant address of the empty string into r0
      .extend(ADD(R0, R0, ImmOperand(4)))
      .extend(BL(Label("puts")))                  // Print empty string, appended with newline
      .extend(MOV(R0, ImmOperand(0)))
      .extend(BL(Label("fflush")))
  }

  def printReferenceFunction: CodeSegment = {
    CodeSegment()
      .extend(MOV(R1, R0))
      .extend(LDR(R0, LabelAddress(getStaticData(printReferenceFormat))))  // Load the constant address of the empty string into r0
      .extend(ADD(R0, R0, ImmOperand(4)))
      .extend(BL(Label("printf")))                  // Print empty string, appended with newline
      .extend(MOV(R0, ImmOperand(0)))
      .extend(BL(Label("fflush")))
  }

  def checkArrayBounds: CodeSegment = {
    CodeSegment()
      .extend(CMP(R0, ImmOperand(0)))
      .extend(LDR(R0, LabelAddress(getStaticData(arrayNegativeIndex)), LT))
      .extend(BL(getStaticFunction(throwRuntimeError), LT))
      .extend(LDR(R1, RegisterAddress(R1, 0)))
      .extend(CMP(R0, R1))
      .extend(LDR(R0, LabelAddress(getStaticData(arrayIndexTooLarge)), CS))
      .extend(BL(getStaticFunction(throwRuntimeError), CS))
  }

  def checkNullPointerFunction: CodeSegment = {
    CodeSegment()
      .extend(CMP(R0, ImmOperand(0)))
      .extend(LDR(R0, LabelAddress(getStaticData(nullReferenceError)), EQ))
      .extend(B(getStaticFunction(throwRuntimeError), EQ))
  }

  def freePairFunction: CodeSegment = {
    CodeSegment()
      .extend(BL(getStaticFunction(checkNullPointerFunction)))
      .extend(PUSH(Seq(R0)))
      .extend(LDR(R0, RegisterAddress(R0, 0)))
      .extend(BL(Label("free")))
      .extend(LDR(R0, RegisterAddress(SP)))
      .extend(LDR(R0, RegisterAddress(R0, 4)))
      .extend(BL(Label("free")))
      .extend(POP(Seq(R0)))
      .extend(BL(Label("free")))
  }

  def throwOverflowError: CodeSegment = {
    CodeSegment()
      .extend(LDR(R0, LabelAddress(getStaticData(overflowError))))
      .extend(BL(getStaticFunction(throwRuntimeError)))
  }
}
