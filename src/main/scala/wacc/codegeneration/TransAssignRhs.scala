package wacc.codegeneration

import wacc.arm._
import wacc.constructs._

/**
  * Created by panayiotis on 16/11/16.
  */
object TransAssignRhs {

  def transAssignRhs(value: AssignValue, registers: Seq[Register]): Seq[Instruction] = {
    value match {
      case e: Expression       => e.transAssignRhs(registers).instructions
      case sl: StructLiteral   => sl.transAssignRhs(registers).instructions
      case al: ArrayLiteral    => transDeclareRhsArrayLiteral(al, registers)
      case pc: PairConstructor => transPairConstructor(pc, registers)
      case pe: PairElement     => transDeclareRhsPairElement(pe, registers)
      case fc: FunctionCall    => transFunctionCall(fc, registers)
      case default  => println("Assign rhs not implemented for " + value); Seq()
    }
  }


  def getPairElementPointer(pe: PairElement, registers: Seq[Register]): CodeSegment = {
    CodeSegment()
      .extend(pe.transAssignRhs(registers)) // Translate expression inside selector
      .extend(MOV(R0, registers.head))                                                 // Check if address is null
      .extend(BL(StaticCode.checkNullPointerFunctionLabel))                            // Check if address is null
      .extend(LDR(registers.head, RegisterAddress(registers.head, pe.selector match {
      case FirstSelector => 0   // Access the left element of the pair (which is a pointer)
      case SecondSelector => 4  // Access the right element of the pair (which is a pointer)
    })))
  }

  def transDeclareRhsPairElement(pe: PairElement, registers: Seq[Register]): Seq[Instruction] = {
    getPairElementPointer(pe, registers)
      .extend(LDR(registers.head, RegisterAddress(registers.head)))  // Dereference the pointer at this element
      .instructions
  }

  def transDeclareRhsArrayLiteral(al: ArrayLiteral, registers: Seq[Register]): Seq[Instruction] = {
    val elements = al.elements
    val arraySize = 4 + elements.size * al.varType.size
    var offset = 4
    var instructions: Seq[Instruction] = Seq()

    for (elem <- al.elements) {
      instructions ++= elem.transAssignRhs(registers.tail).instructions :+ STR(registers(1), RegisterAddress(registers.head, offset))
      offset += al.varType.elemtype.size
    }

    Seq(
      LDR(R0, Const(arraySize)),
      BL(Label("malloc")),
      MOV(registers.head, R0),
      LDR(registers(1), Const(elements.size)),
      STR(registers(1), RegisterAddress(registers.head, 0))
    ) ++ instructions
  }

  def transFunctionCall(fc: FunctionCall, registers: Seq[Register]): Seq[Instruction] = {
    val argumentsSize = ImmOperand(fc.args.map(_.varType.size).sum)
    CodeSegment()
      .extend(fc.args.reverse flatMap (e => {
        // Evaluate each argument and push them on stack in reverse order (first arg is closest to new frame)
        val argumentEvalInstructions = e.transAssignRhs(registers).instructions
        argumentEvalInstructions :+ (e.varType match {
          case Character | Boolean => STRB(registers.head, RegisterAddress(SP, -e.varType.size, writeback = true))
          case _ => STR(registers.head, RegisterAddress(SP, -e.varType.size, writeback = true))
        })
      }))
      .extend(BL(Label(fc.identifier)))
      .extend(ADD(SP, SP, argumentsSize))
      .extend(MOV(registers.head, R0))
      .instructions
  }

  def transPairConstructor(pc: PairConstructor, registers: Seq[Register]): Seq[Instruction] = {
    val firstType = pc.firstExp.varType
    val secondType = pc.secondExp.varType

    val store1 = firstType match {
      case Boolean | Character => STRB(registers(1), RegisterAddress(R0))
      case _ => STR(registers(1), RegisterAddress(R0))
    }

    val store2 = secondType match {
      case Boolean | Character => STRB(registers(1), RegisterAddress(R0))
      case _ => STR(registers(1), RegisterAddress(R0))
    }

    Seq(
      LDR(R0, Const(8)),      //Load the size of the pair in R0
      BL(Label("malloc")),
      MOV(registers.head, R0)
    ) ++ pc.firstExp.transAssignRhs(registers.tail).instructions ++
      Seq (
        LDR(R0, Const(firstType.size)),
        BL(Label("malloc")),
        store1,  //Store the value for the first element in its memory
        STR(R0, RegisterAddress(registers.head)) //Put address of first element in memory of pair
      ) ++ pc.secondExp.transAssignRhs(registers.tail).instructions ++
      Seq(
        LDR(R0, Const(secondType.size)),
        BL(Label("malloc")),
        store2,  //Store the value for the second element in its memory
        STR(R0, RegisterAddress(registers.head, 4))   //Put address of second element in memory of pair with offset
        //STR(registers.head, RegisterAddress(FP, variableRef.offset))
      )
  }
}
