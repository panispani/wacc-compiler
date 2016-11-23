package wacc

import wacc.TransAssigns._
import wacc.TransExpressions._
import wacc.codegeneration._
import wacc.arm._
import wacc.constructs._

/**
  * Created by panayiotis on 16/11/16.
  */
package object TransStatements {

  def transStatement(statement: Statement, symbolTable: SymbolTable, registers: Seq[Register]): Seq[Instruction] = {
    statement match {
      case DeclareStatement(vartype: Type, variable: VariableReference, value: AssignValue)
        => transDeclareStatement(vartype, variable, value, symbolTable, registers)

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

      case ConditionalStatement(expression, trueStatements, falseStatements, symbolTable)
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

  def transArrayLiteral(literal: ArrayLiteral, symbolTable: SymbolTable, registers: Seq[Register]): Seq[Instruction] = {
    var offset = 4
    var instructions: Seq[Instruction] = Seq()

    for (elem <- literal.elements) {
      instructions ++= transExpression(elem, symbolTable, registers.tail) :+ STR(registers(1), RegisterAddress(registers.head, offset))
      offset += literal.vartype.elemtype.size
    }

    instructions
  }

  def transDeclareStatement(vartype: Type,
                            variableRef: VariableReference,
                            assignValue: AssignValue,
                            symbolTable: SymbolTable,
                            registers: Seq[Register]): Seq[Instruction] = {

    assignValue match {
      case VariableReference(name, _, offset) =>
        Seq(
          LDR(registers.head, RegisterAddress(FP, offset)),
          STR(R4, RegisterAddress(FP, variableRef.offset))
        )
      case default => transDeclareStatementWithLiteralRhs(vartype, variableRef, assignValue, symbolTable, registers)
    }
  }

  private def transDeclareStatementWithLiteralRhs(vartype: Type, variableRef: VariableReference, assignValue: AssignValue, symbolTable: SymbolTable, registers: Seq[Register]): Seq[Instruction] = {
    val instructions = vartype match {
      case Integer => transAssignRhs(assignValue, symbolTable, registers) ++ Seq(STR(registers.head, RegisterAddress(FP, variableRef.offset)))
      case Boolean | Character => transAssignRhs(assignValue, symbolTable, registers) ++ Seq(STRB(registers.head, RegisterAddress(FP, variableRef.offset)))
      case ArrayType(elemsType) => assignValue match {
        case literal @ ArrayLiteral(elements) => {
          val arraySize = 4 + elements.size * elemsType.size
          Seq(
            LDR(R0, Const(arraySize)),
            BL(Label("malloc")),
            MOV(registers.head, R0),
            LDR(registers(1), Const(elements.size)),
            STR(registers(1), RegisterAddress(registers.head, 0))
          ) ++ transArrayLiteral(literal, symbolTable, registers) :+ STR(registers.head, RegisterAddress(FP, variableRef.offset))
        }
        case default => println("not impelemented"); Seq()
      }
      case PairType(firstType, secondType) => assignValue match {
        case PairConstructor(firstExp, secondExp) => {
          Seq(
            LDR(R0, Const(firstType.size + secondType.size)),      //Load the size of the pair in R0
            BL(Label("malloc")),
            MOV(registers.head, R0)
          ) ++ transExpression(firstExp, symbolTable, registers.tail) ++
            Seq (
              LDR(R0, Const(firstType.size)),
              BL(Label("malloc")),
              STR(registers(1), RegisterAddress(R0, 0)),  //Store the value for the first element in its memory
              STR(R0, RegisterAddress(registers(0), 0)) //Put address of first element in memory of pair
            ) ++ transExpression(secondExp, symbolTable, registers.tail) ++
              Seq(
                LDR(R0, Const(secondType.size)),
                BL(Label("malloc")),
                STR(registers(1), RegisterAddress(R0, 0)),  //Store the value for the second element in its memory
                STR(R0, RegisterAddress(registers(0), firstType.size)),   //Put address of second element in memory of pair with offset
                STR(registers.head, RegisterAddress(FP, variableRef.offset))
              )
        }
        case PairLiteral() => transAssignRhs(assignValue, symbolTable, registers) ++ Seq(STR(registers.head, RegisterAddress(FP, variableRef.offset)))


      }
      case default => println("not impelemented"); Seq()
    }

    instructions
  }

  def transAssignStatement(lhs: AssignTarget, rhs: AssignValue, symbolTable: SymbolTable, registers: Seq[Register]): Seq[Instruction] = {
    val instruction = transAssignRhs(rhs, symbolTable, registers)

    println("assign " + rhs + " to " + lhs)
    instruction
  }

  def transExitStatement(exitCode: Expression, symbolTable: SymbolTable, registers: Seq[Register]): Seq[Instruction] = {
    val instruction = transExpression(exitCode, symbolTable, registers)

    instruction ++ Seq(MOV(R0, registers.head), BL(Label("exit")))
  }

  def transReturnStatement(returnValue: Expression, symbolTable: SymbolTable, registers: Seq[Register]): Seq[Instruction] = {
    val instruction = transExpression(returnValue, symbolTable, registers)

    instruction ++ Seq(MOV(R0, registers.head))
  }

  def transConditionalStatement(expression: Expression,
                                trueStatements: Seq[Statement],
                                falseStatements: Seq[Statement],
                                symbolTable: SymbolTable,
                                registers: Seq[Register]): Seq[Instruction] = {
    val L0 = Label()
    val L1 = Label()

    //stack allocation is not done TODO- experimental
    transExpression(expression, symbolTable, registers) ++
      Seq(CMP(registers.head, ImmOperand(0)), B(L0, EQ)) ++
      transScopeStatement(falseStatements, symbolTable, registers) ++
      Seq(B(L1), DefineLabel(L0)) ++
      transScopeStatement(trueStatements, symbolTable, registers) ++
      Seq(DefineLabel(L1))
  }

  def transLoopStatement(condition: Expression,
                         stmts: Seq[Statement],
                         symbolTable: SymbolTable,
                         registers: Seq[Register]): Seq[Instruction] = {
    val L0 = Label()
    val L1 = Label()

    val (beginFrame, endFrame) = Macros.frame(symbolTable.sizeInBytes)

    new CodeSegment()
        .append(B(L0))
        .append(DefineLabel(L1))
        .extend(beginFrame)
        .extend(transStatementSequence(stmts, symbolTable, registers))
        .extend(endFrame)
        .append(DefineLabel(L0))
        .extend(transExpression(condition, symbolTable, registers))
        .append(CMP(registers.head, ImmOperand(1)))
        .append(B(L1, EQ)).instructions
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
    val instructions = seq.map(transStatement(_, symbolTable, registers))
    val (beginFrame, endFrame) = Macros.frame(symbolTable.sizeInBytes)

    new CodeSegment()
      .extend(beginFrame)
      .extend(instructions.flatten)
      .extend(endFrame).instructions
  }

  def transReadStatement(read: ReadStatement, registers: Seq[Register]): Seq[Instruction] = {
    val target: Integer = read.target match {
      case vr: VariableReference => vr.offset
    }

    new CodeSegment()
      .append(ADD(R0, FP, ImmOperand(target)))         // r0 = address of target
      .append(BL(StaticCode.readFunctionLabel)) // reads input into desired variable
      .instructions
  }

  def transPrintStatement(print: PrintStatement,
                          symbolTable: SymbolTable,
                          registers: Seq[Register]): Seq[Instruction] = {

    new CodeSegment()
      .extend(transExpression(print.expression, symbolTable, registers)) // eval expression to print
        .append(MOV(R0, registers.head)) // setup function call
      .append(BL(StaticCode.printFunctionLabel)).instructions
  }

  def transPrintLnStatement(print: PrintLnStatement,
                            symbolTable: SymbolTable,
                            registers: Seq[Register]): Seq[Instruction] = {
    new CodeSegment()
      .extend(transExpression(print.expression, symbolTable, registers))
      .append(MOV(R0, registers.head)) // setup function call
      .append(BL(StaticCode.printFunctionLabel))
      .append(BL(StaticCode.printLnFunctionLabel)).instructions

  }
}
