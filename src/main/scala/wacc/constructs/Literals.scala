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
      .extend(BL(Label("malloc")))
      .extend(MOV(registers.head, R0))
      .extend(LDR(registers(1), Const(elements.size)))
      .extend(STR(registers(1), RegisterAddress(registers.head, 0)))
      .extend(instructions)
  }
}

case class PairLiteral() extends Literal {
  override val varType: Type = PairType(AnyType, AnyType)
}

case class StructLiteral(members: Seq[Expression]) extends AssignValue {
  override val varType: Type = StructType("$$$", members map (member => ("", member.varType)))

  override def transAssignRhs(registers: Seq[Register]): CodeSegment = {
    val structSize = 4 + members.map(m => m.varType.size).sum
    var offset = 0
    var instructions = CodeSegment(
      LDR(R0, Const(structSize)),
      BL(Label("malloc")),
      MOV(registers.head, R0)
    )

    for (member <- members) {
      instructions = instructions.extend(member.transAssignRhs(registers.tail).instructions :+ STR(registers(1), RegisterAddress(registers.head, offset)))
      offset += member.varType.size
    }

    instructions
  }
}
