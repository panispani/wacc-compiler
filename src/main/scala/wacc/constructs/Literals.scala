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
    val arraySize = 4 + elements.size * varType.size
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

    CodeSegment()
      .extend(LDR(R0, Const(arraySize)))
      .extend(MOV(R1, ImmOperand(varType.elemtype.enumId)))
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
    val structSize = members.map(m => m.varType.size).sum
    var offset = 0
    var instructions = CodeSegment(
      LDR(R0, Const(structSize)),
      BL(Label("malloc")),
      MOV(registers.head, R0)
    )

    val store =

    for (member <- members) {
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
