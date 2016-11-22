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
        => transAssignStatement(lhs, rhs, registers)

      case ExitStatement(exitCode: Expression)
        => transExitStatement(exitCode, registers)

      case ReturnStatement(returnValue: Expression)
        => transReturnStatement(returnValue, registers)

      case SkipStatement()
        => transSkipStatement();

      case PrintStatement(expression)
        => transExpression(expression, registers) :+ BL(Label("p_print_string"))

      case PrintLnStatement(expression)
        => transExpression(expression, registers) :+ BL(Label("p_print_ln"))

      case ConditionalStatement(expression, trueStatements, falseStatements, symbolTable)
        => transConditionalStatement(expression, trueStatements, falseStatements, symbolTable, registers)

      case LoopStatement(condition, statements, symbolTable)
        => transLoopStatement(condition, statements, symbolTable, registers)

      case ScopeStatement(sequence, symbolTable)
        => transScopeStatement(sequence, symbolTable, registers)
    }
  }

  def transSkipStatement() : Seq[Instruction] = {
    Seq()
  }

  def transArrayLiteral(literal: ArrayLiteral, registers: Seq[Register]): Seq[Instruction] = {
    var offset = 4
    var instructions: Seq[Instruction] = Seq()

    for (elem <- literal.elements) {
      instructions ++= transExpression(elem, registers.tail) :+ STR(registers(1), RegisterAddress(registers.head, offset))
      offset += literal.vartype.elemtype.size
    }

    instructions
  }

  def transDeclareStatement(vartype: Type,
                            variableRef: VariableReference,
                            assignValue: AssignValue,
                            symbolTable: SymbolTable,
                            registers: Seq[Register]): Seq[Instruction] = {
    // TODO: See what this should do
    //    VarLog.add(identifier, vartype)

    assignValue match {
      case VariableReferenceExpression(name, _) =>
        // It is safe to .get the option (semantic check)
        val reference = symbolTable.lookupDeep(name).get
        Seq(
          LDR(registers.head, RegisterAddress(SP, reference.offset)),
          STR(R4, RegisterAddress(SP, variableRef.offset))
        )
      case default => transDeclareStatementWithLiteralRhs(vartype, variableRef, assignValue, registers)
    }
  }

  private def transDeclareStatementWithLiteralRhs(vartype: Type, variableRef: VariableReference, assignValue: AssignValue, registers: Seq[Register]): Seq[Instruction] = {
    val instructions = vartype match {
      case Integer => transAssignRhs(assignValue, registers) ++ Seq(STR(registers.head, RegisterAddress(SP, variableRef.offset)))
      case Boolean | Character => transAssignRhs(assignValue, registers) ++ Seq(STRB(registers.head, RegisterAddress(SP, variableRef.offset)))
      case ArrayType(elemsType) => assignValue match {
        case literal @ ArrayLiteral(elements) => {
          val arraySize = 4 + elements.size * elemsType.size
          Seq(
            LDR(R0, Const(arraySize)),
            BL(Label("malloc")),
            MOV(registers.head, R0),
            LDR(registers(1), Const(elements.size)),
            STR(registers(1), RegisterAddress(registers.head, 0))
          ) ++ transArrayLiteral(literal, registers) :+ STR(registers.head, RegisterAddress(SP, 0))
        }
        case default => println("not impelemented"); Seq()
      }
      case PairType(firstType, secondType) => assignValue match {
        case PairConstructor(firstExp, secondExp) => {
          Seq(
            LDR(R0, Const(firstType.size + secondType.size)),      //Load the size of the pair (always 8) in R0
            BL(Label("malloc")),
            MOV(registers.head, R0)
          ) ++ transExpression(firstExp, registers.tail) ++
            Seq (
              LDR(R0, Const(firstType.size)),
              BL(Label("malloc")),
              STR(registers(1), RegisterAddress(R0, 0)),  //Store the value for the first element in its memory
              STR(R0, RegisterAddress(registers(0), 0)) //Put address of first element in memory of pair
            ) ++ transExpression(secondExp, registers.tail) ++
              Seq(
                LDR(R0, Const(secondType.size)),
                BL(Label("malloc")),
                STR(registers(1), RegisterAddress(R0, 0)),  //Store the value for the second element in its memory
                STR(R0, RegisterAddress(registers(0), firstType.size)),   //Put address of second element in memory of pair with offset
                STR(registers.head, RegisterAddress(SP, 0))
              )
        }


      }
      case default => println("not impelemented"); Seq()
    }

    instructions
  }

  def transAssignStatement(lhs: AssignTarget, rhs: AssignValue, registers: Seq[Register]): Seq[Instruction] = {
    val instruction = transAssignRhs(rhs, registers)

    println("assign " + rhs + " to " + lhs)
    instruction
  }

  def transExitStatement(exitCode: Expression, registers: Seq[Register]): Seq[Instruction] = {
    val instruction = transExpression(exitCode, registers)

    instruction ++ Seq(MOV(R0, registers.head), BL(Label("exit")))
  }

  def transReturnStatement(returnValue: Expression, registers: Seq[Register]): Seq[Instruction] = {
    val instruction = transExpression(returnValue, registers)

    instruction ++ Seq(MOV(R0, registers.head))
  }

  def transConditionalStatement(expression: Expression,
                                trueStatements: Seq[Statement],
                                falseStatements: Seq[Statement],
                                symbolTable: SymbolTable,
                                registers: Seq[Register]): Seq[Instruction] = {
    val L0 = Label()
    val L1 = Label()

    transExpression(expression, registers) ++
      Seq(CMP(registers.head, ImmOperand(0)), B(L0, EQ)) ++
      transStatementSequence(falseStatements, symbolTable, registers) ++
      Seq(B(L1), DefineLabel(L0)) ++
      transStatementSequence(trueStatements, symbolTable, registers) ++
      Seq(DefineLabel(L1))
  }

  def transLoopStatement(condition: Expression,
                         stmts: Seq[Statement],
                         symbolTable: SymbolTable,
                         registers: Seq[Register]): Seq[Instruction] = {
    val L0 = Label()
    val L1 = Label()

    Seq(B(L0), DefineLabel(L1), SUB(SP, SP, ImmOperand(symbolTable.sizeInBytes))) ++
    transStatementSequence(stmts, symbolTable, registers) ++
    Seq(ADD(SP, SP, ImmOperand(symbolTable.sizeInBytes)), DefineLabel(L0)) ++
    transExpression(condition, registers) ++
    Seq(CMP(registers.head, ImmOperand(1)), B(L1, EQ))
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

    new CodeSegment()
      .append(SUB(SP, SP, ImmOperand(symbolTable.sizeInBytes)))
      .extend(instructions.flatten)
      .append(ADD(SP, SP, ImmOperand(symbolTable.sizeInBytes))).instructions
  }

  def transReadStatement(read: ReadStatement, registers: Seq[Register]): CodeSegment = {
    val target: Integer = read.target match {
      case vr: VariableReference => vr.offset
    }

    new CodeSegment()
      .append(ADD(R0, SP, ImmOperand(target)))         // r0 = address of target
      .append(BL(Label(StaticCode.readFunctionLabel))) // reads input into desired variable
  }
}
