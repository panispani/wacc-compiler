package wacc.constructs

import wacc.arm.{Label, R0}
import wacc.codegeneration._
import wacc.arm._
import wacc.codegeneration.predefined.StaticCode
import wacc.{SymbolTable, VariableReference}

abstract class Statement {
  def transStatement(symbolTable: SymbolTable, registers: Seq[Register]): CodeSegment
}

case class ExitStatement(exitCode: Expression) extends Statement {

  override def transStatement(symbolTable: SymbolTable, registers: Seq[Register]): CodeSegment = {
    CodeSegment(TransExpressions.transExpression(exitCode, symbolTable, registers) : _*)
      .extend(MOV(R0, registers.head))
      .extend(BL(Label("exit")))
  }
}

case class ReturnStatement(returnValue: Expression) extends Statement {

  override def transStatement(symbolTable: SymbolTable, registers: Seq[Register]): CodeSegment = {

    CodeSegment()
      .extend(TransExpressions.transExpression(returnValue, symbolTable, registers) : _*)
      .extend(MOV(R0, registers.head))

  }
}

abstract class AbstractPrintStatement extends Statement {
  val expression: Expression

  def transStatement(symbolTable: SymbolTable, registers: Seq[Register]): CodeSegment = {
    val printLabel: Label = expression.vartype match {
      case Integer        => StaticCode.getStaticFunction(StaticCode.printInt)
      case Character      => StaticCode.getStaticFunction(StaticCode.printChar)
      case Boolean        => StaticCode.getStaticFunction(StaticCode.printBool)
      case String         => StaticCode.getStaticFunction(StaticCode.printString)
      case ArrayType(_)   => StaticCode.getStaticFunction(StaticCode.printReference)
      case PairType(_, _) => StaticCode.getStaticFunction(StaticCode.printReference)
      case _              => StaticCode.getStaticFunction(StaticCode.printString)
    }

    CodeSegment()
      .extend(TransExpressions.transExpression(expression, symbolTable, registers) : _*) // eval expression to print
      .extend(MOV(R0, registers.head)) // setup function call
      .extend(BL(printLabel))
  }
}

case class PrintStatement(expression: Expression) extends AbstractPrintStatement
case class PrintLnStatement(expression: Expression) extends AbstractPrintStatement {

  override def transStatement(symbolTable: SymbolTable, registers: Seq[Register]): CodeSegment = {
      super.transStatement(symbolTable, registers)
        .extend(BL(StaticCode.getStaticFunction(StaticCode.printLn)))
  }
}

case class AssignStatement(lhs: AssignTarget, rhs: AssignValue) extends Statement {

  override def transStatement(symbolTable: SymbolTable, registers: Seq[Register]): CodeSegment = {
    CodeSegment()
      .extend(TransAssignRhs.transAssignRhs(rhs, symbolTable, registers) : _*)
      .extend(Macros.store(lhs, symbolTable, registers) : _*)
  }
}

case class FreeStatement(expression: Expression) extends Statement {

  override def transStatement(symbolTable: SymbolTable, registers: Seq[Register]): CodeSegment = {
    expression match {
      case VariableReference(name, vartype, offset) => {
        CodeSegment()
          .extend(LDR(registers.head, RegisterAddress(FP, offset)))
          .extend(MOV(R0, registers.head))
          .extend(BL(StaticCode.getStaticFunction(StaticCode.freePair)))
      }
    }
  }
}

case class ScopeStatement(statements: Seq[Statement], symbolTable: SymbolTable) extends Statement {

  override def transStatement(symbolTable: SymbolTable, registers: Seq[Register]): CodeSegment = {
    // Make sure the same registers are available after each statement is translated! TODO
    val instructions = statements map(TransStatements.transStatement(_, this.symbolTable, registers))
    val (beginFrame, endFrame) = Macros.frame(this.symbolTable.sizeInBytes)

    CodeSegment()
      .extend(beginFrame)
      .extend(instructions.flatten : _*)
      .extend(endFrame)
  }
}

case class ReadStatement(target: AssignTarget) extends Statement {

  override def transStatement(symbolTable: SymbolTable, registers: Seq[Register]): CodeSegment = {
    val instructions: CodeSegment = target match {
      case vr: VariableReference => CodeSegment().extend(ADD(registers.head, FP, ImmOperand(vr.offset)))
      case pe: PairElement       => CodeSegment().extend(TransAssignRhs.getPairElementPointer(pe, symbolTable, registers))
      case ae: ArrayElement      => {
        //TODO: This should already be in the ArrayElement construct instead of identifier
        val vr: VariableReference = symbolTable.lookupDeep(ae.identifier).get
        //TODO: This assumes a single index (not nested arrays)
        CodeSegment(TransExpressions.transExpression(ae.index.head, symbolTable, registers) : _*)
          .extend(MOV(R0, registers.head))
          .extend(Macros.getArrayElemAddress(vr, symbolTable, registers, target.vartype))
      }
    }

    val readLabel: Label = target.vartype match {
      case Integer   => StaticCode.getStaticFunction(StaticCode.readInt)
      case Character => StaticCode.getStaticFunction(StaticCode.readChar)
    }

    CodeSegment()
      .extend(instructions)
      .extend(MOV(R0, registers.head))
      .extend(BL(readLabel))
  }
}

case class SkipStatement() extends Statement {

  override def transStatement(symbolTable: SymbolTable, registers: Seq[Register]): CodeSegment = {
    CodeSegment()
  }
}

case class ConditionalStatement(expression: Expression, trueStatements: ScopeStatement, falseStatements: ScopeStatement) extends Statement {

  override def transStatement(symbolTable: SymbolTable, registers: Seq[Register]): CodeSegment = {
    val L0 = Label()
    val L1 = Label()

    //stack allocation is not done TODO- experimental
    CodeSegment(TransExpressions.transExpression(expression, symbolTable, registers) : _*)
      .extend(CMP(registers.head, ImmOperand(1)))
      .extend(B(L0, EQ))
      .extend(falseStatements.transStatement(falseStatements.symbolTable, registers))
      .extend(B(L1))
      .extend(DefineLabel(L0))
      .extend(trueStatements.transStatement(trueStatements.symbolTable, registers))
      .extend(DefineLabel(L1))
  }
}

// Identifier is a new reference here so it will always have the correct offset at parse-time
// Otherwise we would need to have it as a String and do additional lookup during code generation
case class DeclareStatement(vartype: Type, newReference: VariableReference, value: AssignValue) extends Statement {

  override def transStatement(symbolTable: SymbolTable, registers: Seq[Register]): CodeSegment = {
    CodeSegment(TransAssignRhs.transAssignRhs(value, symbolTable, registers) : _*)
      .extend(Macros.store(newReference, symbolTable, registers) : _*)
      .extend(SUB(SP, SP, ImmOperand(vartype.size)))
  }
}

case class LoopStatement(condition: Expression, statements: Seq[Statement], symbolTable: SymbolTable) extends Statement {

  override def transStatement(symbolTable: SymbolTable, registers: Seq[Register]): CodeSegment = {
    val L0 = Label()
    val L1 = Label()

    val (beginFrame, endFrame) = Macros.frame(this.symbolTable.sizeInBytes)

    beginFrame
      .extend(B(L0))
      .extend(DefineLabel(L1))
      .extend(TransStatements.transStatementSequence(statements, this.symbolTable, registers) : _*)
      .extend(DefineLabel(L0))
      .extend(TransExpressions.transExpression(condition, symbolTable, registers) : _*)
      .extend(CMP(registers.head, ImmOperand(1)))
      .extend(B(L1, EQ))
      .extend(endFrame)
  }
}


