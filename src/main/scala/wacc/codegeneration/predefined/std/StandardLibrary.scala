package wacc.codegeneration.predefined.std

import wacc.arm._
import wacc.codegeneration.CodeSegment

object StandardLibrary {

  var requestedPredefinedFunctions: Set[(Label, CodeSegment)] = Set()

  def intFormat                  = AsciiData("\"%d\\0\"")
  def charReadFormat             = AsciiData("\" %c\\0\"")
  def printStringFormat          = AsciiData("\"%.*s\\0\"")
  def printReferenceFormat       = AsciiData("\"%p\\0\"")

  def emptyString                = AsciiData("\"\\0\"")
  def trueString                 = AsciiData("\"true\\0\"")
  def falseString                = AsciiData("\"false\\0\"")

  def divideOrModuleByZeroString = AsciiData("\"DivideByZeroError: divide or modulo by zero\"")
  def arrayNegativeIndex         = AsciiData("\"ArrayIndexOutOfBoundsError: negative index\"")
  def arrayIndexTooLarge         = AsciiData("\"ArrayIndexOutOfBoundsError: index too large\"")
  def overflowError              = AsciiData("\"OverflowError: the result is too small/large to store in a 4-byte signed-integer.\"")
  def nullReferenceError         = AsciiData("\"NullReferenceError: dereference a null reference\"")

  def staticDataMap: collection.immutable.Map[AsciiData, Label] = collection.immutable.Map(
    printStringFormat          -> Label("print_string_format"),
    intFormat                  -> Label("int_format"),
    charReadFormat             -> Label("char_read_format"),
    printReferenceFormat       -> Label("print_reference"),

    emptyString                -> Label("empty_string"),
    trueString                 -> Label("true_string"),
    falseString                -> Label("false_string"),

    divideOrModuleByZeroString -> Label("divide_by_zero"),
    arrayNegativeIndex         -> Label("array_negative_index"),
    arrayIndexTooLarge         -> Label("array_index_too_large"),
    overflowError              -> Label("overflow_error"),
    nullReferenceError         -> Label("null_reference_error")
  )

  def readInt = CodeSegment()
    .extend(MOV(R1, R0)) // Move address of variable into r1 as expected by scanf
    .extend(LDR(R0, LabelAddress(staticDataMap(intFormat)))) // Load the constant address of the format string into r1
    .extend(ADD(R0, R0, ImmOperand(4)))
    .extend(BL(Label("scanf"))) // Call scanf with two arguments, r0 and r1

  def readChar = CodeSegment()
    .extend(MOV(R1, R0)) // Move address of variable into r1 as expected by scanf
    .extend(LDR(R0, LabelAddress(staticDataMap(charReadFormat)))) // Load the constant address of the format string into r1
    .extend(ADD(R0, R0, ImmOperand(4)))
    .extend(BL(Label("scanf"))) // Call scanf with two arguments, r0 and r1

  def printString = CodeSegment()
    .extend(LDR(R1, RegisterAddress(R0, 0))) // Move the address of the string to print into r1 as expected by printf
    .extend(ADD(R2, R0, ImmOperand(4)))
    .extend(LDR(R0, LabelAddress(staticDataMap(printStringFormat)))) // Load the constant address of the format string into r0
    .extend(ADD(R0, R0, ImmOperand(4)))
    .extend(BL(Label("printf"))) // Print string
    .extend(MOV(R0, ImmOperand(0)))
    .extend(BL(Label("fflush")))

  def printInt = CodeSegment()
    .extend(MOV(R1, R0))
    .extend(LDR(R0, LabelAddress(staticDataMap(intFormat))))
    .extend(ADD(R0, R0, ImmOperand(4)))
    .extend(BL(Label("printf")))
    .extend(MOV(R0, ImmOperand(0)))
    .extend(BL(Label("fflush")))

  def printChar = CodeSegment()
    .extend(BL(Label("putchar")))

  def printBool = CodeSegment()
    .extend(CMP(R0, ImmOperand(0)))
    .extend(LDR(R0, LabelAddress(staticDataMap(trueString)), NE))
    .extend(LDR(R0, LabelAddress(staticDataMap(falseString)), EQ))
    .extend(ADD(R0, R0, ImmOperand(4)))
    .extend(BL(Label("printf")))
    .extend(MOV(R0, ImmOperand(0)))
    .extend(BL(Label("fflush")))

  def printLn = CodeSegment()
    .extend(LDR(R0, LabelAddress(staticDataMap(emptyString)))) // Load the constant address of the empty string into r0
    .extend(ADD(R0, R0, ImmOperand(4)))
    .extend(BL(Label("puts"))) // Print empty string, appended with newline
    .extend(MOV(R0, ImmOperand(0)))
    .extend(BL(Label("fflush")))

  def printReference = CodeSegment()
    .extend(MOV(R1, R0))
    .extend(LDR(R0, LabelAddress(staticDataMap(printReferenceFormat))))  // Load the constant address of the empty string into r0
    .extend(ADD(R0, R0, ImmOperand(4)))
    .extend(BL(Label("printf")))                  // Print empty string, appended with newline
    .extend(MOV(R0, ImmOperand(0)))
    .extend(BL(Label("fflush")))

  def throwRuntimeError = CodeSegment()
    .extend(MOV(R0, ImmOperand(-1)))
    .extend(BL(Label("exit")))

  def checkDivideByZero = CodeSegment()
    .extend(CMP(R1, ImmOperand(0))) // Check if the dividend is 0
    .extend(LDR(R0, LabelAddress(staticDataMap(divideOrModuleByZeroString)), EQ)) //If it is 0, load in R0 the error string
    .extend(BL(getStaticFunction(throwRuntimeError), EQ)) //Branch to the function to throw a runtime error

  def checkArrayBounds = CodeSegment()
    .extend(CMP(R0, ImmOperand(0)))
    .extend(LDR(R0, LabelAddress(staticDataMap(arrayNegativeIndex)), LT))
    .extend(BL(getStaticFunction(throwRuntimeError), LT))
    .extend(LDR(R1, RegisterAddress(R1, 0)))
    .extend(CMP(R0, R1))
    .extend(LDR(R0, LabelAddress(staticDataMap(arrayIndexTooLarge)), CS))
    .extend(BL(getStaticFunction(throwRuntimeError), CS))

  def checkNullPointer = CodeSegment()
    .extend(CMP(R0, ImmOperand(0)))
    .extend(LDR(R0, LabelAddress(staticDataMap(nullReferenceError)), EQ))
    .extend(B(getStaticFunction(throwRuntimeError), EQ))

  def freePair = CodeSegment()
    .extend(BL(getStaticFunction(checkNullPointer)))
    .extend(PUSH(Seq(R0)))
    .extend(LDR(R0, RegisterAddress(R0, 0)))
    .extend(BL(Label("free")))
    .extend(LDR(R0, RegisterAddress(SP)))
    .extend(LDR(R0, RegisterAddress(R0, 4)))
    .extend(BL(Label("free")))
    .extend(POP(Seq(R0)))
    .extend(BL(Label("free")))

  def throwOverflowError = CodeSegment()
    .extend(LDR(R0, LabelAddress(staticDataMap(overflowError))))
    .extend(BL(getStaticFunction(throwRuntimeError)))

  def concatinateStrings = CodeSegment(
      PUSH(Seq(R1)),                  // Save stringB
      PUSH(Seq(R0)),                  // Save stringA
      LDR(R2, RegisterAddress(R0)),   // R2 = len(stringA)
      LDR(R0, RegisterAddress(R1)),   // R0 = len(stringB)
      MOV(R3, R0),
      ADD(R0, R0, R2),                // R0 = len(stringA) + len(stringB)
      PUSH(Seq(R0, R2, R3)),
      BL(Label("malloc")),            // R0 = newstring*
      POP(Seq(R1, R2, R3)),
      STR(R1, RegisterAddress(R0)),   // *newstring = length(stringA) + len(stringB)
      ADD(R0, R0, ImmOperand(4)),     // newstring++
      POP(Seq(R1)),                   // Restore stringA into R1
      ADD(R1, R1, ImmOperand(4)),
      PUSH(Seq(R2, R3)),
      BL(Label("memcpy")),            // memcpy(newstring, stringA, len(stringA))
      POP(Seq(R2, R3)),
      POP(Seq(R1)),                   // Restore stringB into R1
      ADD(R1, R1, ImmOperand(4)),
      PUSH(Seq(R0)),
      ADD(R0, R0, R2),                // newstring += len(stringA)
      MOV(R2, R3),
      BL(Label("memcpy")),            // memcpy(newstring, stringB, len(stringB))
      POP(Seq(R0)),
      SUB(R0, R0, ImmOperand(4))
    )

  def div = CodeSegment(
    BL(getStaticFunction(checkDivideByZero)),
    BL(Label("__aeabi_idiv")))

  def mod = CodeSegment(
    BL(getStaticFunction(checkDivideByZero)),
    BL(Label("__aeabi_idivmod")))

  def staticFunctionMap: Map[CodeSegment, Label] = Map(
    readInt            -> Label("read_int"),
    readChar           -> Label("read_char"),

    printInt           -> Label("print_int"),
    printChar          -> Label("print_char"),
    printBool          -> Label("print_bool_label"),
    printString        -> Label("print"),
    printLn            -> Label("print_ln"),
    printReference     -> Label("print_reference_function"),

    throwRuntimeError  -> Label("throw_runtime_error"),
    throwOverflowError -> Label("throw_overflow_error"),

    checkNullPointer   -> Label("check_null_pointer"),
    checkArrayBounds   -> Label("check_array_bounds"),
    checkDivideByZero  -> Label("check_divide_by_zero"),

    concatinateStrings -> Label("concatinate_strings"),
    freePair           -> Label("free_pair"),
    div                -> Label("div"),
    mod                -> Label("mod")
  )

  def getStaticFunction(function: CodeSegment): Label = {
    val label = staticFunctionMap.apply(function)
    requestedPredefinedFunctions += (label -> function)
    label
  }
}
