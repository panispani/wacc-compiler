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

      case stat @ ReadStatement(_) => transReadStatement(stat, registers)
    }
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

    val bytesAllocatedByFunction = symbolTable.deepSize()

    // TODO: use proper stuff
    instruction ++ Seq(
      MOV(R0, registers.head),
      ADD(SP, SP, ImmOperand(bytesAllocatedByFunction)),
      POP(Seq(FP)),
      POP(Seq(PC)))
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

  def transReadStatement(read: ReadStatement, registers: Seq[Register]): Seq[Instruction] = {
    val target: Integer = read.target match {
      case vr: VariableReference => vr.offset
      //TODO: pair arrayelem
    }

    val printLabel: Label = read.target.vartype match {
      case Integer   => StaticCode.readIntLabel
      case Character => StaticCode.readCharLabel
    }

    CodeSegment()
      .append(ADD(R0, FP, ImmOperand(target)))     // r0 = address of target
      .append(BL(printLabel))                     // reads input into desired variable
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
