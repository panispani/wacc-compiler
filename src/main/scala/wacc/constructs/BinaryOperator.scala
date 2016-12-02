package wacc.constructs

import wacc.arm._
import wacc.codegeneration.{CodeSegment, StaticCode}

/**
  * Created by panayiotis on 08/11/16.
  */
case class BinaryOperatorExpr(expression1: Expression, binaryOperator: BinaryOperator, expression2: Expression) extends Expression {
  override val vartype = binaryOperator match {
    case TimesBinOp
       | DivBinOp
       | ModBinOp
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

    case PlusBinOp => if (expression1.vartype == Integer && expression2.vartype == Integer) Integer else
                      if (expression1.vartype == String && expression2.vartype == String) String
  }
}

abstract class BinaryOperator(val binaryOperator: String) {
  def translate(dest: Register, operand: Register): CodeSegment
}

object BinaryOperator {
  def apply(binaryOperator: String): BinaryOperator = binaryOperator match {
    case "*" => TimesBinOp
    case "/" => DivBinOp
    case "%" => ModBinOp
    case "+" => PlusBinOp
    case "-" => MinusBinOp
    case ">" => GtBinOp
    case ">=" => GteBinOp
    case "<" => LtBinOp
    case "<=" => LteBinOp
    case "==" => EqualsBinOp
    case "!=" => NequalsBinOp
    case "&&" => AndBinOp
    case "||" => OrBinOp
  }
}

/* Integers */
object TimesBinOp extends BinaryOperator("*") {
  override def translate(dest: Register, operand: Register): CodeSegment
    = CodeSegment(
        SMULL(dest, operand, dest, operand),
        CMPSHIFT(operand, dest, ASR(31)),
        BL(StaticCode.throwOverflowErrorFunctionLabel, NE))
}

object DivBinOp extends BinaryOperator("/") {
  override def translate(dest: Register, operand: Register): CodeSegment
    = CodeSegment(
        MOV(R0, dest),
        MOV(R1, operand),
        BL(StaticCode.checkDivideByZeroLabel),
        BL(StaticCode.divisionLabel),
        MOV(dest, R0))
}

object ModBinOp extends BinaryOperator("%") {
  override def translate(dest: Register, operand: Register): CodeSegment
    = CodeSegment(
        MOV(R0, dest),
        MOV(R1, operand),
        BL(StaticCode.checkDivideByZeroLabel),
        BL(StaticCode.moduleLabel),
        MOV(dest, R1))
}
object PlusBinOp extends BinaryOperator("+") {
  override def translate(dest: Register, operand: Register): CodeSegment
    = CodeSegment(
        ADDS(dest, dest, operand),
        BL(StaticCode.throwOverflowErrorFunctionLabel, VS))
}

object MinusBinOp extends BinaryOperator("-") {
  override def translate(dest: Register, operand: Register): CodeSegment
    = CodeSegment(
        SUBS(dest, dest, operand),
        BL(StaticCode.throwOverflowErrorFunctionLabel, VS))
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
