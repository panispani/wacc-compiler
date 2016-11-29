package wacc.constructs

import wacc.arm.{Label, R0}
import wacc.codegeneration._
import wacc.arm._
import wacc.{SymbolTable, VariableReference}

abstract class Statement {
  def transStatement(registers: Seq[Register]): CodeSegment
}

case class ExitStatement(exitCode: Expression) extends Statement {

  override def transStatement(registers: Seq[Register]): CodeSegment = {
    CodeSegment()
      .extend(TransExpressions.transExpression(exitCode, registers))
      .extend(MOV(R0, registers.head))
      .extend(BL(Label("exit")))
  }
}

case class ReturnStatement(returnValue: Expression) extends Statement {

  override def transStatement(registers: Seq[Register]): CodeSegment = {

    CodeSegment()
      .extend(TransExpressions.transExpression(returnValue, registers))
      .extend(MOV(R0, registers.head))

  }
}

abstract class AbstractPrintStatement extends Statement {
  val expression: Expression

  def transStatement(registers: Seq[Register]): CodeSegment = {
    val printLabel: Label = expression.vartype match {
      case Integer        => StaticCode.printIntLabel
      case Character      => StaticCode.printCharLabel
      case Boolean        => StaticCode.printBoolLabel
      case String         => StaticCode.printFunctionLabel
      case ArrayType(_)   => StaticCode.printReferenceFunctionLabel
      case PairType(_, _) => StaticCode.printReferenceFunctionLabel
      case _              => StaticCode.printFunctionLabel
    }

    CodeSegment()
      .extend(TransExpressions.transExpression(expression, registers)) // eval expression to print
      .extend(MOV(R0, registers.head)) // setup function call
      .extend(BL(printLabel))
  }
}

case class PrintStatement(expression: Expression) extends AbstractPrintStatement
case class PrintLnStatement(expression: Expression) extends AbstractPrintStatement {

  override def transStatement(registers: Seq[Register]): CodeSegment = {
      super.transStatement(registers).extend(BL(StaticCode.printLnFunctionLabel))
  }
}

case class AssignStatement(lhs: AssignTarget, rhs: AssignValue) extends Statement {

  override def transStatement(registers: Seq[Register]): CodeSegment = {
    CodeSegment()
      .extend(TransAssignRhs.transAssignRhs(rhs, registers))
      .extend(Macros.store(lhs, registers))
  }
}

case class FreeStatement(expression: Expression) extends Statement {

  override def transStatement(registers: Seq[Register]): CodeSegment = {
    expression match {
      case VariableReference(name, vartype, offset) => {
        CodeSegment()
          .extend(LDR(registers.head, RegisterAddress(FP, offset)))
          .extend(MOV(R0, registers.head))
          .extend(BL(StaticCode.freePairLabel))
      }
    }
  }
}

case class ScopeStatement(statements: Seq[Statement], symbolTable: SymbolTable) extends Statement {

  override def transStatement(registers: Seq[Register]): CodeSegment = {
    // Make sure the same registers are available after each statement is translated! TODO
    val instructions = statements map(TransStatements.transStatement(_, registers))
    val (beginFrame, endFrame) = Macros.frame(this.symbolTable.sizeInBytes)

    CodeSegment()
      .extend(beginFrame)
      .extend(instructions.flatten)
      .extend(endFrame)
  }
}

case class ReadStatement(target: AssignTarget) extends Statement {

  override def transStatement(registers: Seq[Register]): CodeSegment = {
    val instructions: CodeSegment = target match {
      case vr: VariableReference => CodeSegment().extend(ADD(registers.head, FP, ImmOperand(vr.offset)))
      case pe: PairElement       => CodeSegment().extend(TransAssignRhs.getPairElementPointer(pe, registers))
      case ae @ ArrayElement(vr, _, _) => {
        //TODO: This assumes a single index (not nested arrays)
        CodeSegment().extend(TransExpressions.transExpression(ae.index.head, registers))
          .extend(MOV(R0, registers.head))
          .extend(Macros.getArrayElemAddress(vr, registers, target.vartype))
      }
    }

    val readLabel: Label = target.vartype match {
      case Integer   => StaticCode.readIntLabel
      case Character => StaticCode.readCharLabel
    }

    CodeSegment()
      .extend(instructions)
      .extend(MOV(R0, registers.head))
      .extend(BL(readLabel))
  }
}

case class SkipStatement() extends Statement {

  override def transStatement(registers: Seq[Register]): CodeSegment = {
    CodeSegment()
  }
}

case class ConditionalStatement(expression: Expression, trueStatements: ScopeStatement, falseStatements: ScopeStatement) extends Statement {

  override def transStatement(registers: Seq[Register]): CodeSegment = {
    val L0 = Label()
    val L1 = Label()

    //stack allocation is not done TODO- experimental
    CodeSegment()
      .extend(TransExpressions.transExpression(expression, registers))
      .extend(CMP(registers.head, ImmOperand(1)))
      .extend(B(L0, EQ))
      .extend(falseStatements.transStatement(registers))
      .extend(B(L1))
      .extend(DefineLabel(L0))
      .extend(trueStatements.transStatement(registers))
      .extend(DefineLabel(L1))
  }
}

// Identifier is a new reference here so it will always have the correct offset at parse-time
// Otherwise we would need to have it as a String and do additional lookup during code generation
case class DeclareStatement(vartype: Type, newReference: VariableReference, value: AssignValue) extends Statement {

  override def transStatement(registers: Seq[Register]): CodeSegment = {
    CodeSegment()
      .extend(TransAssignRhs.transAssignRhs(value, registers))
      .extend(Macros.store(newReference, registers))
      .extend(SUB(SP, SP, ImmOperand(vartype.size)))
  }
}

case class LoopStatement(condition: Expression, statements: Seq[Statement], symbolTable: SymbolTable) extends Statement {

  override def transStatement(registers: Seq[Register]): CodeSegment = {
    val L0 = Label()
    val L1 = Label()

    val (beginFrame, endFrame) = Macros.frame(this.symbolTable.sizeInBytes)

    beginFrame
      .extend(B(L0))
      .extend(DefineLabel(L1))
      .extend(TransStatements.transStatementSequence(statements, registers))
      .extend(DefineLabel(L0))
      .extend(TransExpressions.transExpression(condition, registers))
      .extend(CMP(registers.head, ImmOperand(1)))
      .extend(B(L1, EQ))
      .extend(endFrame)
  }
}


