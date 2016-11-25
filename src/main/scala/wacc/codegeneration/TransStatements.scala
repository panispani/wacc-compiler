package wacc.codegeneration

import wacc.{SymbolTable, VariableReference}
import wacc.arm._
import wacc.constructs._

/**
  * Created by panayiotis on 16/11/16.
  */
object TransStatements {


  def transStatement(statement: Statement, symbolTable: SymbolTable, registers: Seq[Register]): Seq[Instruction] = {

    statement match {
      case dec: DeclareStatement
        => transDeclareStatement(dec, symbolTable, registers)

      case AssignStatement(lhs: AssignTarget, rhs: AssignValue)
        => transAssignStatement(lhs, rhs, symbolTable, registers)

      case ExitStatement(exitCode: Expression)
        => transExitStatement(exitCode, symbolTable, registers)

      case ReturnStatement(returnValue: Expression)
        => transReturnStatement(returnValue, symbolTable, registers)

      case SkipStatement()
        => transSkipStatement();

      case stat @ PrintStatement(expression)
        => transPrintStatement(stat, symbolTable, registers);

      case stat @ PrintLnStatement(expression)
        => transPrintLnStatement(stat, symbolTable, registers);

      case ConditionalStatement(expression, trueStatements, falseStatements)
        => transConditionalStatement(expression, trueStatements, falseStatements, symbolTable, registers)

      case LoopStatement(condition, statements, symbolTable)
        => transLoopStatement(condition, statements, symbolTable, registers)

      case ScopeStatement(sequence, symbolTable)
        => transScopeStatement(sequence, symbolTable, registers)

      case stat @ ReadStatement(_)
        => transReadStatement(stat, symbolTable, registers)

      case free: FreeStatement
        => transFreeStatement(free, symbolTable, registers)
    }
  }

  def transFreeStatement(free: FreeStatement, symbolTable: SymbolTable, registers: Seq[Register]): Seq[Instruction] = {
<<<<<<< HEAD
    Seq()
=======
    //val instructions = TransExpressions.transExpression(free.expression, symbolTable, registers)

    free match {
      case VariableReference(name, vartype, offset) => {
        CodeSegment()
          .append(LDR(registers.head, RegisterAddress(FP, offset)))
          .append(MOV(R0, registers.head))
          .append(BL(StaticCode.freePairLabel)).instructions
      }
    }
>>>>>>> freeStatement
  }

  def transSkipStatement() : Seq[Instruction] = {
    Seq()
  }

  def transDeclareStatement(dec: DeclareStatement,
                            symbolTable: SymbolTable,
                            registers: Seq[Register]): Seq[Instruction] = {

    val post = Seq(SUB(SP, SP, ImmOperand(dec.vartype.size)))
    val variableRef = dec.newReference
    val assignValue = dec.value

    TransAssignRhs.transAssignRhs(assignValue, symbolTable, registers) ++ Macros.store(variableRef, symbolTable, registers) ++ post
  }

  def transAssignStatement(lhs: AssignTarget, rhs: AssignValue, symbolTable: SymbolTable, registers: Seq[Register]): Seq[Instruction] = {
    val instruction = TransAssignRhs.transAssignRhs(rhs, symbolTable, registers)
    instruction ++ Macros.store(lhs, symbolTable, registers)
  }

  def transExitStatement(exitCode: Expression, symbolTable: SymbolTable, registers: Seq[Register]): Seq[Instruction] = {
    val instruction = TransExpressions.transExpression(exitCode, symbolTable, registers)

    instruction ++ Seq(MOV(R0, registers.head), BL(Label("exit")))
  }

  def transReturnStatement(returnValue: Expression, symbolTable: SymbolTable, registers: Seq[Register]): Seq[Instruction] = {
    val instruction = TransExpressions.transExpression(returnValue, symbolTable, registers)

    instruction ++ Seq(MOV(R0, registers.head))
  }

  def transConditionalStatement(expression: Expression,
                                trueStatements: ScopeStatement,
                                falseStatements: ScopeStatement,
                                parentTable: SymbolTable,
                                registers: Seq[Register]): Seq[Instruction] = {
    val L0 = Label()
    val L1 = Label()

    //stack allocation is not done TODO- experimental
    TransExpressions.transExpression(expression, parentTable, registers) ++
      Seq(CMP(registers.head, ImmOperand(1)), B(L0, EQ)) ++
      transScopeStatement(falseStatements.statements, falseStatements.symbolTable, registers) ++
      Seq(B(L1), DefineLabel(L0)) ++
      transScopeStatement(trueStatements.statements, trueStatements.symbolTable, registers) ++
      Seq(DefineLabel(L1))
  }

  def transLoopStatement(condition: Expression,
                         stmts: Seq[Statement],
                         symbolTable: SymbolTable,
                         registers: Seq[Register]): Seq[Instruction] = {
    val L0 = Label()
    val L1 = Label()

    val (beginFrame, endFrame) = Macros.frame(symbolTable.sizeInBytes)

    beginFrame
        .append(B(L0))
        .append(DefineLabel(L1))
        .extend(transStatementSequence(stmts, symbolTable, registers))
        .append(DefineLabel(L0))
        .extend(TransExpressions.transExpression(condition, symbolTable, registers))
        .append(CMP(registers.head, ImmOperand(1)))
        .append(B(L1, EQ))
        .extend(endFrame).instructions
  }

  def transStatementSequence(seq: Seq[Statement],
                             symbolTable: SymbolTable,
                             registers: Seq[Register]): Seq[Instruction] = {
    val instructions
    = for {
      stmt <- seq
    } yield transStatement(stmt, symbolTable, registers)
    instructions.flatten
  }

  def transScopeStatement(seq: Seq[Statement], symbolTable: SymbolTable, registers: Seq[Register]): Seq[Instruction] = {
    // Make sure the same registers are available after each statement is translated! TODO
    val instructions = seq.map(TransStatements.transStatement(_, symbolTable, registers))
    val (beginFrame, endFrame) = Macros.frame(symbolTable.sizeInBytes)

    CodeSegment()
      .extend(beginFrame)
      .extend(instructions.flatten)
      .extend(endFrame).instructions
  }

  def transReadStatement(read: ReadStatement, symbolTable: SymbolTable, registers: Seq[Register]): Seq[Instruction] = {
    val instructions: CodeSegment = read.target match {
      case vr: VariableReference => CodeSegment().append(ADD(registers.head, FP, ImmOperand(vr.offset)))
      case pe: PairElement       => CodeSegment().extend(TransAssignRhs.getPairElementPointer(pe, symbolTable, registers))
      case ae: ArrayElement      => {
        //TODO: This should already be in the ArrayElement construct instead of identifier
        val vr: VariableReference = symbolTable.lookupDeep(ae.identifier).get
        //TODO: This assumes a single index (not nested arrays)
        CodeSegment().extend(TransExpressions.transExpression(ae.index.head, symbolTable, registers))
          .append(MOV(R0, registers.head))
          .extend(Macros.getArrayElemAddress(vr, symbolTable, registers, read.target.vartype))
      }
    }

    val readLabel: Label = read.target.vartype match {
      case Integer   => StaticCode.readIntLabel
      case Character => StaticCode.readCharLabel
    }

    CodeSegment()
      .extend(instructions)
      .append(MOV(R0, registers.head))
      .append(BL(readLabel))
      .instructions
  }

  def transPrintStatement(print: PrintStatement,
                          symbolTable: SymbolTable,
                          registers: Seq[Register]): Seq[Instruction] = {

    val printLabel: Label = print.expression.vartype match {
      case Integer   => StaticCode.printIntLabel
      case Character => StaticCode.printCharLabel
      case Boolean   => StaticCode.printBoolLabel
      case String    => StaticCode.printFunctionLabel
      case ArrayType(_) => StaticCode.printReferenceFunctionLabel
      case _         => StaticCode.printFunctionLabel
    }

    CodeSegment()
      .extend(TransExpressions.transExpression(print.expression, symbolTable, registers)) // eval expression to print
      .append(MOV(R0, registers.head)) // setup function call
      .append(BL(printLabel)).instructions
  }

  def transPrintLnStatement(print: PrintLnStatement,
                            symbolTable: SymbolTable,
                            registers: Seq[Register]): Seq[Instruction] = {
    val printLabel: Label = print.expression.vartype match {
      case Integer   => StaticCode.printIntLabel
      case Character => StaticCode.printCharLabel
      case Boolean   => StaticCode.printBoolLabel
      case String    => StaticCode.printFunctionLabel
      case ArrayType(_) => StaticCode.printReferenceFunctionLabel
      case _         => StaticCode.printFunctionLabel
    }

    CodeSegment()
      .extend(TransExpressions.transExpression(print.expression, symbolTable, registers))
      .append(MOV(R0, registers.head)) // setup function call
      .append(BL(printLabel))
      .append(BL(StaticCode.printLnFunctionLabel)).instructions

  }
}
