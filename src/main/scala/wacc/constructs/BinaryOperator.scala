package wacc.constructs

import wacc.arm._
import wacc.codegeneration.CodeSegment
import wacc.codegeneration.predefined.StaticCode

/**
  * Created by panayiotis on 08/11/16.
  */
case class BinaryOperatorExpr(expression1: Expression, binaryOperator: BinaryOperator, expression2: Expression) extends Expression {
  override val vartype = binaryOperator match {
    case TimesBinOp
       | DivBinOp
       | ModBinOp
       | PlusBinOp
       | MinusBinOp
      => Integer

    case GtBinOp
       | GteBinOp
       | LtBinOp
       | LteBinOp
       | EqualsBinOp
       | NequalsBinOp
       | AndBinOp
       | OrBinOp
      => Boolean
  }
}

case class BinaryOperator(binaryOperator: String) {
  def translate(dest: Register, operand: Register): CodeSegment = CodeSegment()
}

/* Integers */
object TimesBinOp extends BinaryOperator("*") {
  override def translate(dest: Register, operand: Register): CodeSegment
  = CodeSegment(MUL(dest, dest, operand))
}
object DivBinOp extends BinaryOperator("/") {
  override def translate(dest: Register, operand: Register): CodeSegment
  = CodeSegment(
    MOV(R0, dest),
    MOV(R1, operand),
    BL(StaticCode.getStaticFunction(StaticCode.checkDivideByZero)),
    BL(Label("__aeabi_idiv")),
    MOV(dest, R0)
  )
}
object ModBinOp extends BinaryOperator("%") {
  override def translate(dest: Register, operand: Register): CodeSegment
  = CodeSegment(
    MOV(R0, dest),
    MOV(R1, operand),
    BL(StaticCode.getStaticFunction(StaticCode.checkDivideByZero)),
    BL(Label("__aeabi_idiv")),
    MOV(dest, R1)
  )
}
object PlusBinOp extends BinaryOperator("+") {
  override def translate(dest: Register, operand: Register): CodeSegment = CodeSegment(ADD(dest, dest, operand))
}
object MinusBinOp extends BinaryOperator("-") {
  override def translate(dest: Register, operand: Register): CodeSegment = CodeSegment(SUB(dest, dest, operand))
}

/* Booleans */
object GtBinOp extends BinaryOperator(">") {
  override def translate(dest: Register, operand: Register): CodeSegment
  = Macros.conditionalExpression(dest, operand)(GT, LE)
}
object GteBinOp extends BinaryOperator(">=") {
  override def translate(dest: Register, operand: Register): CodeSegment
  = Macros.conditionalExpression(dest, operand)(GE, LT)
}
object LtBinOp extends BinaryOperator("<") {
  override def translate(dest: Register, operand: Register): CodeSegment
  = Macros.conditionalExpression(dest, operand)(LT, GE)
}
object LteBinOp extends BinaryOperator("<=") {
  override def translate(dest: Register, operand: Register): CodeSegment
  = Macros.conditionalExpression(dest, operand)(LE, GT)
}
object EqualsBinOp extends BinaryOperator("==") {
  override def translate(dest: Register, operand: Register): CodeSegment
  = Macros.conditionalExpression(dest, operand)(EQ, NE)
}
object NequalsBinOp extends BinaryOperator("!=") {
  override def translate(dest: Register, operand: Register): CodeSegment
  = Macros.conditionalExpression(dest, operand)(NE, EQ)
}
object AndBinOp extends BinaryOperator("&&") {
  override def translate(dest: Register, operand: Register): CodeSegment
  = CodeSegment(AND(dest, dest, operand))
}
object OrBinOp extends BinaryOperator("||") {
  override def translate(dest: Register, operand: Register): CodeSegment
  = CodeSegment(ORR(dest, dest, operand))
}
