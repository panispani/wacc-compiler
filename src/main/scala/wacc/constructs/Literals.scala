package wacc.constructs

import wacc.arm._
import wacc.codegeneration._

trait Literal extends Expression

case class IntegerLiteral(value: Int) extends Literal {
  override val varType: Type = Integer
}

case class BoolLiteral(value: Boolean) extends Literal {
  override val varType: Type = Boolean
}

case class CharLiteral(value: String) extends Literal {
  override val varType: Type = Character
}

case class StringLiteral(value: String) extends Literal {
  override val varType: Type = String
}

case class ArrayLiteral(elements: Seq[Expression]) extends AssignValue {
  val varType: ArrayType = ArrayType(if (elements.nonEmpty) elements.head.varType else AnyType)

  override def transAssignRhs(registers: Seq[Register]): CodeSegment = {

    // This value is required when generating code without GC
    // See the commented out code segment below
    //val arraySize = 4 + elements.size * varType.size

    var offset = 4
    var instructions: Seq[Instruction] = Seq()

    for (elem <- elements) {
      val store = varType.elemtype match {
        case Character | Boolean => STRB(registers(1), RegisterAddress(registers.head, offset))
        case default             => STR(registers(1), RegisterAddress(registers.head, offset))
      }

      instructions ++= elem.transAssignRhs(registers.tail).instructions :+ store
      offset += varType.elemtype.size

    }

    // new_array_literal takes the length of the array and computes the size
    // based on the enumId (unique identifier based on the data type)
    CodeSegment()
      .extend(LDR(R0, Const(elements.size)))
      .extend(LDR(R1, Const(varType.elemtype.enumId)))
      .extend(BL(Label("new_array_literal")))
      .extend(MOV(registers.head, R0))
      .extend(LDR(registers(1), Const(elements.size)))
      .extend(STR(registers(1), RegisterAddress(registers.head)))
      .extend(instructions)

    /* TODO Commented out for GC, add if check for -gc flag
    CodeSegment()
      .extend(LDR(R0, Const(arraySize)))
      .extend(BL(Label("malloc")))
      .extend(MOV(registers.head, R0))
      .extend(LDR(registers(1), Const(elements.size)))
      .extend(STR(registers(1), RegisterAddress(registers.head)))
      .extend(instructions)
      */
  }
}

case class PairLiteral() extends Literal {
  override val varType: Type = PairType(AnyType, AnyType)
}

case class StructLiteral(members: Seq[Expression]) extends AssignValue {
  override val varType: Type = StructType("$$$", members map (member => ("", member.varType)))

  override def transAssignRhs(registers: Seq[Register]): CodeSegment = {
    val memberTypes = members map(m => m.varType)
    val structSize =  (memberTypes map (m => m.size)).sum
    var offset = 0
    var instructions = CodeSegment(
      LDR(R0, Const(memberTypes.size * 4)),                         // R0 = size of array of types
      BL(Label("malloc"))).extend(
        memberTypes.zipWithIndex flatMap { case (m, i) => Seq(      // Fill array with enumIDs of types
           MOV(R1, ImmOperand(m.enumId)),
           STR(R1, RegisterAddress(R0, i * 4)))
      }).extend(CodeSegment(
        MOV(R2, R0),                                                // Put array address as third argument
        PUSH(Seq(R0)),                                              // Save array address on stack for later free
        LDR(R0, Const(structSize)),
        LDR(R1, Const(members.size)),
        BL(Label("new_struct_literal")),                            // new_struct_literal(structSize, members.size, types)
        MOV(registers.head, R0),
        POP(Seq(R1)),                                               // Pop address of types array into R1
        MOV(R0, R1),
        BL(Label("free")))                                          // Free the types array
    )

    /* TODO enable if -gc not enabled
    var instructions = CodeSegment(
      LDR(R0, Const(structSize)),
      BL(Label("malloc")),
      MOV(registers.head, R0)
    )
    */

    val store = for (member <- members) {
      val store = member.varType match {
        case Character | Boolean => STRB(registers(1), RegisterAddress(registers.head, offset))
        case default => STR(registers(1), RegisterAddress(registers.head, offset))
      }

      instructions = instructions.extend(member.transAssignRhs(registers.tail).instructions :+ store)
      offset += member.varType.size
    }

    instructions
  }
}
