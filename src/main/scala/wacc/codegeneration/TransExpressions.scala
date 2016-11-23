package wacc

import wacc.TransBinaryOperators._
import wacc.codegeneration.Weight._
import wacc.codegeneration._
import wacc.arm._
import wacc.constructs._

package object TransExpressions {
  def transExpression(expr: Expression, symbolTable: SymbolTable, registers: Seq[Register]): Seq[Instruction] = {
    registers match {
      case (r1::r2::regs) => transExpressionReg(expr, r1, r2, symbolTable, regs)
      case (r1::regs) => transExpressionAcc(expr, r1, symbolTable: SymbolTable, regs)
    }
  }

  // Register machine approach
  private def transExpressionReg(expr: Expression, reg1: Register, reg2: Register, symbolTable: SymbolTable, regs: Seq[Register]): Seq[Instruction] = {
    expr match {
      case BinaryOperatorExpr(e1, binOp, e2) =>
        if (weight(e1) > weight(e2)) {
          // e1 first
          val evalExpr = transExpression(e1, symbolTable, reg1+:reg2+:regs) ++
            transExpression(e2, symbolTable, reg2+:regs)
          evalExpr ++ transBinaryOperator(reg1, binOp, reg2)
        } else {
          // e2 first
          val evalExpr = transExpression(e2, symbolTable, reg2+:reg1+:regs) ++
            transExpression(e1, symbolTable, reg1+:regs)
          evalExpr ++ transBinaryOperator(reg1, binOp, reg2)
        }
      case VariableReferenceExpression(name, _) => {
        // It is safe to .get the option (semantic check)
        val reference = symbolTable.lookupDeep(name).get
        Seq(LDR(reg1, RegisterAddress(FP, reference.offset)))
      }
      case IntegerLiteral(value) => Seq(MOV(reg1, ImmOperand(value)))
      case BoolLiteral(value)    => Seq(MOV(reg1, ImmOperand(if (value) 1 else 0)))
      case CharLiteral(value)    => Seq(MOV(reg1, CharOperand(value)))
      case StringLiteral(value)  => Seq(MOV(reg1, LabelAddress(DefineStringLabel(value).label)))
    }
  }

  // Accumulator machine approach
  private def getAnotherRegister(reg: Register) = {
    if (reg == R0)
      R1
    else
      R0
  }

  private def transExpressionAcc(expr: Expression, reg1: Register, symbolTable: SymbolTable, regs: Seq[Register]): Seq[Instruction] = {
    val reg2 = getAnotherRegister(reg1)
    Seq(PUSH(Seq(reg2))) ++
      transExpressionReg(expr, reg1, reg2, symbolTable, regs) ++
      Seq(POP(Seq(reg2)))
  }
}