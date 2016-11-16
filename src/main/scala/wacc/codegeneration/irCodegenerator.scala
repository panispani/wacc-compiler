package wacc.codegeneration

import wacc.constructs._
import wacc.codegeneration.Weight.weight

/**
  * Created by panayiotis on 15/11/16.
  */
//accumulator register approach
class irCodegenerator {
  val allRegisters = Seq(R1, R2, R3, R4, R5, R6, R7, R8, R9, R10, R11, R12)

  def codegen(program: Program): Seq[Instruction] = {
    transNext(program, allRegisters)
  }


  def transNext(a: Any, registers: Seq[Register]): Seq[Instruction] = {
    a match {
      case Program(functions, stmt)
        => transProgram(functions, stmt, registers)
      case Function(ident, params, vartype, stmt)
        => transFunction(ident, params, vartype, stmt, registers)
      case DeclareStatement(vartype, identifier, value)
        => transDeclareStatement(vartype, identifier, value, registers)
      case stmt:Statement
        => transStatement(stmt, registers)
      case default
        => println("can we even reach this point"); Seq[Instruction]()
    }
  }

  def transProgram(functions: Seq[Function], stmts: Seq[Statement], registers: Seq[Register]): Seq[Instruction] = {
    val instruction = Seq[Instruction]()
    val functionInstructions =
      for {
      f <- functions
    } yield transNext(f, registers)

    val mainInstructions =
      for {
        stmt <- stmts
      } yield transNext(stmt, registers)

    // add labels later
    functionInstructions.flatten ++ mainInstructions.flatten
  }

  def transFunction(ident: String, params: Seq[Param], vartype: Type, stmt: Seq[Statement], registers: Seq[Register]): Seq[Instruction] = {
    val instruction = Seq[Instruction]()
    instruction
  }

  def transStatement(stmt: Statement, registers: Seq[Register]) = {
    stmt match {
      case DeclareStatement(vartype: Type, identifier: String, value: AssignValue)
      => transDeclareStatement(vartype, identifier, value, registers)
      case AssignStatement(lhs: AssignTarget, rhs: AssignValue)
      => transAssignStatement(lhs, rhs, registers)
      case ExitStatement(exitCode: Expression)
      => transExitStatement(exitCode, registers)
      case ReturnStatement(returnValue: Expression)
      => transReturnStatement(returnValue, registers)
    }
  }

  def transDeclareStatement(vartype: Type, identifier: String, value: AssignValue, registers: Seq[Register]): Seq[Instruction] = {
    println("declare " + identifier + " to be " + value + "(" + vartype + ")" )
    val bytes = vartype match {
      case PrimitiveType("int") => 4
      case PrimitiveType("bool") => 1
      case PrimitiveType("char") => 1
      case PrimitiveType("string") => 4 //keep on heap, this is a pointer
      case ArrayType(elemtype: Type) => 4 //keep on heap
      case PairType(ftype, sType) => 4 //keep on heap
    }
    val store =  vartype match {
      case PrimitiveType("int") => Seq(STR(registers.head, SP, 0))
      case PrimitiveType("bool") => Seq(STR(registers.head, SP, 0))
      case PrimitiveType("char") => Seq(STRB(registers.head, SP, 0))
    }
    //result on first register in list
    Seq(SUB2(SP, SP, bytes)) ++ transAssignValue(value, registers) ++ store
  }

  def transAssignValue(value: AssignValue, registers: Seq[Register]): Seq[Instruction] = {
    value match {
      case e: Expression => transExpression(e, registers)
      case default  => println("unimplemented"); Seq()
    }
  }

  def transAssignStatement(lhs: AssignTarget, rhs: AssignValue, registers: Seq[Register]): Seq[Instruction] = {
    val instruction = Seq[Instruction]()
    println("assign " + rhs + " to " + lhs)
    instruction
  }

  def transExitStatement(exitCode: Expression, registers: Seq[Register]): Seq[Instruction] = {
    val instruction = Seq()//transExpression(exitCode, registers)

    instruction ++ Seq(MOV(R0, registers.head), BL(Label("exit")))
  }

  def transReturnStatement(returnValue: Expression, registers: Seq[Register]): Seq[Instruction] = {
    val instruction = transExpression(returnValue, registers)

    instruction ++ Seq(MOV(R0, registers.head), BL(Label("exit")))
  }

  //expressions
  def transExpression(expr: Expression, registers: Seq[Register]): Seq[Instruction] = {
    expr match {
      case BinaryOperatorExpr(e1, bOp, e2) => {
        if (weight(e1) > weight(e2)) {
          // e1 first
        } else {
          // e2 first
        }
        Seq()
      }
      case IntegerLiteral(value) => Seq(MOVS(registers.head, value))
      case BoolLiteral(value) => val v = if (value) 1 else 0;
                                 Seq(MOVS(registers.head, v))
      case CharLiteral(value) => Seq(MOVCH(registers.head, value))
    }
  }


}
