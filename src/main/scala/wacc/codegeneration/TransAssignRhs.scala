package wacc.codegeneration

import wacc.{SymbolTable, VariableReference}
import wacc.arm._
import wacc.constructs._

/**
  * Created by panayiotis on 16/11/16.
  */
object TransAssignRhs {


  def transAssignRhs(value: AssignValue, symbolTable: SymbolTable, registers: Seq[Register]): Seq[Instruction] = {
    value match {
      case e: Expression       => TransExpressions.transExpression(e, symbolTable, registers)
      case al: ArrayLiteral    => transDeclareRhsArrayLiteral(al, symbolTable, registers)
      case pc: PairConstructor => transPairConstructor(pc, symbolTable, registers)
      case pe: PairElement     => transDeclareRhsPairElement(pe, symbolTable, registers)
      case fc: FunctionCall    => transFunctionCall(fc, symbolTable, registers)
      case default  => println("Assign rhs not implemented for " + value); Seq()
    }
  }

  def transDeclareRhsPairElement(pe: PairElement, symbolTable: SymbolTable, registers: Seq[Register]): Seq[Instruction] = {
    //TODO: Check for null pointer

    CodeSegment()
      .extend(TransExpressions.transExpression(pe.expression, symbolTable, registers)) // Translate expression inside selector
      .append(LDR(registers.head, RegisterAddress(registers.head, pe.selector match {
        case FirstSelector => 0
        case SecondSelector => 4
      })))                                                                             // Access the right element of the pair
      .append(LDR(registers.head, RegisterAddress(registers.head)))                    // Dereference the pointer at this element
      .instructions
  }

  def transDeclareRhsArrayLiteral(al: ArrayLiteral, symbolTable: SymbolTable, registers: Seq[Register]): Seq[Instruction] = {
    val elements = al.elements
    val arraySize = 4 + elements.size * elements.size
    Seq(
      LDR(R0, Const(arraySize)),
      BL(Label("malloc")),
      MOV(registers.head, R0),
      LDR(registers(1), Const(elements.size)),
      STR(registers(1), RegisterAddress(registers.head, 0))
    )
  }

  def transFunctionCall(fc: FunctionCall, symbolTable: SymbolTable, registers: Seq[Register]): Seq[Instruction] = {
    val argumentsSize = ImmOperand(fc.args.map(_.vartype.size).sum)
    CodeSegment()
      .extend(fc.args.reverse flatMap (e => {
        // Evaluate each argument and push them on stack in reverse order (first arg is closest to new frame)
        val argumentEvalInstructions = TransExpressions.transExpression(e, symbolTable, registers)
        argumentEvalInstructions :+ STR(registers.head, RegisterAddress(SP, -e.vartype.size, writeback = true))
      }))
      .append(BL(Label(fc.identifier)))
      .append(ADD(SP, SP, argumentsSize))
      .append(MOV(registers.head, R0))
      .instructions
  }

  def transPairConstructor(pc: PairConstructor, symbolTable: SymbolTable, registers: Seq[Register]): Seq[Instruction] = {
    val firstType = pc.firstExp.vartype
    val secondType = pc.secondExp.vartype

    Seq(
      LDR(R0, Const(firstType.size + secondType.size)),      //Load the size of the pair in R0
      BL(Label("malloc")),
      MOV(registers.head, R0)
    ) ++ TransExpressions.transExpression(pc.firstExp, symbolTable, registers.tail) ++
      Seq (
        LDR(R0, Const(firstType.size)),
        BL(Label("malloc")),
        STR(registers(1), RegisterAddress(R0, 0)),  //Store the value for the first element in its memory
        STR(R0, RegisterAddress(registers(0), 0)) //Put address of first element in memory of pair
      ) ++ TransExpressions.transExpression(pc.secondExp, symbolTable, registers.tail) ++
      Seq(
        LDR(R0, Const(secondType.size)),
        BL(Label("malloc")),
        STR(registers(1), RegisterAddress(R0, 0)),  //Store the value for the second element in its memory
        STR(R0, RegisterAddress(registers(0), firstType.size))   //Put address of second element in memory of pair with offset
        //STR(registers.head, RegisterAddress(FP, variableRef.offset))
      )
  }
}
