package wacc.constructs

import wacc.arm._
import wacc.codegeneration._

trait Literal extends Expression

case class IntegerLiteral(value: Int) extends Literal {
  override val vartype: Type = Integer
}

case class BoolLiteral(value: Boolean) extends Literal {
  override val vartype: Type = Boolean
}

case class CharLiteral(value: String) extends Literal {
  override val vartype: Type = Character
}

case class StringLiteral(value: String) extends Literal {
  override val vartype: Type = String
}

case class ArrayLiteral(elements: Seq[Expression]) extends AssignValue {
  val vartype: ArrayType = ArrayType(if (elements.nonEmpty) elements.head.vartype else AnyType)

  override def transAssignRhs(registers: Seq[Register]): CodeSegment = {
    val arraySize = 4 + elements.size * vartype.size
    var offset = 4
    var instructions: Seq[Instruction] = Seq()

    for (elem <- elements) {
      val store = vartype.elemtype match {
        case Character | Boolean => STRB(registers(1), RegisterAddress(registers.head, offset))
        case default             => STR(registers(1), RegisterAddress(registers.head, offset))
      }

      instructions ++= elem.transAssignRhs(registers.tail).instructions :+ store
      offset += vartype.elemtype.size

    }

    CodeSegment()
      .extend(LDR(R0, Const(arraySize)))
      .extend(MOV(R1, ImmOperand(vartype.elemtype.enumId)))
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
  override val vartype: Type = PairType(AnyType, AnyType)
}
