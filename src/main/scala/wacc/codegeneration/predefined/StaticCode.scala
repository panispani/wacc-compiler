package wacc.codegeneration.predefined

import wacc.arm._
import wacc.codegeneration.CodeSegment

object StaticCode {

  var requestedPredefinedFunctions: Set[(Label, CodeSegment)] = Set()

  def outputFunctions: CodeSegment = {
    var functions = CodeSegment()
    for ((l, f) <- requestedPredefinedFunctions) {
      functions = functions.extend(DefineLabel(l), NEW_STACK_FRAME).extend(f).extend(RETURN)
    }
    functions
  }

  def getStaticData(data: AsciiData): Label = {
    staticDataMap(data)
  }

  def getStaticFunction(function: CodeSegment): Label = {
    val label = staticFunctionMap(function)
    requestedPredefinedFunctions += (label -> function)
    label
  }

  def outputData: CodeSegment = {
    var data = CodeSegment()
    for ((d, l) <- staticDataMap) {
      data = data.extend(DefineLabel(l)).extend(d)
    }
    data
  }

  val intFormat                  = AsciiData("\"%d\\0\"")
  val charReadFormat             = AsciiData("\" %c\\0\"")
  val printStringFormat          = AsciiData("\"%.*s\\0\"")
  val printReferenceFormat       = AsciiData("\"%p\\0\"")
  val emptyString                = AsciiData("\"\\0\"")
  val trueString                 = AsciiData("\"true\\0\"")
  val falseString                = AsciiData("\"false\\0\"")
  val divideOrModuleByZeroString = AsciiData("\"DivideByZeroError: divide or modulo by zero\"")
  val arrayNegativeIndex         = AsciiData("\"ArrayIndexOutOfBoundsError: negative index\"")
  val arrayIndexTooLarge         = AsciiData("\"ArrayIndexOutOfBoundsError: index too large\"")
  val overflowError              = AsciiData("\"OverflowError: the result is too small/large to store in a 4-byte signed-integer.\"")
  val nullReferenceError         = AsciiData("\"NullReferenceError: dereference a null reference\"")
  val readInt = CodeSegment()
    .extend(MOV(R1, R0)) // Move address of variable into r1 as expected by scanf
    .extend(LDR(R0, LabelAddress(getStaticData(intFormat)))) // Load the constant address of the format string into r1
    .extend(ADD(R0, R0, ImmOperand(4)))
    .extend(BL(Label("scanf"))) // Call scanf with two arguments, r0 and r1

  val readChar = CodeSegment()
    .extend(MOV(R1, R0)) // Move address of variable into r1 as expected by scanf
    .extend(LDR(R0, LabelAddress(getStaticData(charReadFormat)))) // Load the constant address of the format string into r1
    .extend(ADD(R0, R0, ImmOperand(4)))
    .extend(BL(Label("scanf"))) // Call scanf with two arguments, r0 and r1

  val printString = CodeSegment()
    .extend(LDR(R1, RegisterAddress(R0, 0))) // Move the address of the string to print into r1 as expected by printf
    .extend(ADD(R2, R0, ImmOperand(4)))
    .extend(LDR(R0, LabelAddress(getStaticData(printStringFormat)))) // Load the constant address of the format string into r0
    .extend(ADD(R0, R0, ImmOperand(4)))
    .extend(BL(Label("printf"))) // Print string
    .extend(MOV(R0, ImmOperand(0)))
    .extend(BL(Label("fflush")))

  val printInt = CodeSegment()
    .extend(MOV(R1, R0))
    .extend(LDR(R0, LabelAddress(getStaticData(intFormat))))
    .extend(ADD(R0, R0, ImmOperand(4)))
    .extend(BL(Label("printf")))
    .extend(MOV(R0, ImmOperand(0)))
    .extend(BL(Label("fflush")))

  val printChar = CodeSegment()
    .extend(BL(Label("putchar")))

  val printBool = CodeSegment()
    .extend(CMP(R0, ImmOperand(0)))
    .extend(LDR(R0, LabelAddress(getStaticData(trueString)), NE))
    .extend(LDR(R0, LabelAddress(getStaticData(falseString)), EQ))
    .extend(ADD(R0, R0, ImmOperand(4)))
    .extend(BL(Label("printf")))
    .extend(MOV(R0, ImmOperand(0)))
    .extend(BL(Label("fflush")))

  val printLn = CodeSegment()
    .extend(LDR(R0, LabelAddress(getStaticData(emptyString)))) // Load the constant address of the empty string into r0
    .extend(ADD(R0, R0, ImmOperand(4)))
    .extend(BL(Label("puts"))) // Print empty string, appended with newline
    .extend(MOV(R0, ImmOperand(0)))
    .extend(BL(Label("fflush")))

  val printReference = CodeSegment()
      .extend(MOV(R1, R0))
      .extend(LDR(R0, LabelAddress(getStaticData(printReferenceFormat))))  // Load the constant address of the empty string into r0
      .extend(ADD(R0, R0, ImmOperand(4)))
      .extend(BL(Label("printf")))                  // Print empty string, appended with newline
      .extend(MOV(R0, ImmOperand(0)))
      .extend(BL(Label("fflush")))

  val throwRuntimeError: CodeSegment = {
    CodeSegment()
      .extend(MOV(R0, ImmOperand(-1)))
      .extend(BL(Label("exit")))
  }

  val checkDivideByZero: CodeSegment = {
    CodeSegment()
      .extend(CMP(R1, ImmOperand(0))) // Check if the dividend is 0
      .extend(LDR(R0, LabelAddress(getStaticData(divideOrModuleByZeroString)), EQ)) //If it is 0, load in R0 the error string
      .extend(BL(getStaticFunction(throwRuntimeError), EQ)) //Branch to the function to throw a runtime error
  }

  val checkArrayBounds: CodeSegment = {
    CodeSegment()
      .extend(CMP(R0, ImmOperand(0)))
      .extend(LDR(R0, LabelAddress(getStaticData(arrayNegativeIndex)), LT))
      .extend(BL(getStaticFunction(throwRuntimeError), LT))
      .extend(LDR(R1, RegisterAddress(R1, 0)))
      .extend(CMP(R0, R1))
      .extend(LDR(R0, LabelAddress(getStaticData(arrayIndexTooLarge)), CS))
      .extend(BL(getStaticFunction(throwRuntimeError), CS))
  }
  val checkNullPointer = CodeSegment()
    .extend(CMP(R0, ImmOperand(0)))
    .extend(LDR(R0, LabelAddress(getStaticData(nullReferenceError)), EQ))
    .extend(B(getStaticFunction(throwRuntimeError), EQ))

  val freePair = CodeSegment()
    .extend(BL(getStaticFunction(checkNullPointer)))
    .extend(PUSH(Seq(R0)))
    .extend(LDR(R0, RegisterAddress(R0, 0)))
    .extend(BL(Label("free")))
    .extend(LDR(R0, RegisterAddress(SP)))
    .extend(LDR(R0, RegisterAddress(R0, 4)))
    .extend(BL(Label("free")))
    .extend(POP(Seq(R0)))
    .extend(BL(Label("free")))

  val throwOverflowError = CodeSegment()
    .extend(LDR(R0, LabelAddress(getStaticData(overflowError))))
    .extend(BL(getStaticFunction(throwRuntimeError)))

  val div = CodeSegment(
    BL(StaticCode.getStaticFunction(StaticCode.checkDivideByZero)),
    BL(Label("__aeabi_idiv")))

  val mod = CodeSegment(
    BL(StaticCode.getStaticFunction(StaticCode.checkDivideByZero)),
    BL(Label("__aeabi_idivmod")))

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
  val staticFunctionMap: Map[CodeSegment, Label] = Map(
    readInt            -> Label("read_int"),
    readChar           -> Label("read_char"),
    printInt           -> Label("print_int"),
    printChar          -> Label("print_char"),
    printBool          -> Label("print_bool_label"),
    printString        -> Label("print"),
    printLn            -> Label("print_ln"),
    printReference     -> Label("print_reference_function"),
    checkNullPointer   -> Label("check_null_pointer"),
    freePair           -> Label("free_pair"),
    checkArrayBounds   -> Label("check_array_bounds"),
    checkDivideByZero  -> Label("check_divide_by_zero"),
    throwRuntimeError  -> Label("throw_runtime_error"),
    throwOverflowError -> Label("throw_overflow_error"),
    div                -> Label("div"),
    mod                -> Label("mod")

    // identity                 -> Label("thr ow_overflow_error_function"),
    // identity                 -> Label("thr ow_overflow_error")
  )
}
