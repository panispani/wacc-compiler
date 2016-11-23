package wacc

import wacc.TransBinaryOperators._
import wacc.TransUnaryOperators._
import wacc.codegeneration.Weight._
import wacc.codegeneration._
import wacc.arm._
import wacc.constructs._
import wacc.StaticCode._

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
          val evalExpr = transExpression(e1, symbolTable, reg1 +: reg2 +: regs) ++
            transExpression(e2, symbolTable, reg2 +: regs)
          evalExpr ++ transBinaryOperator(reg1, binOp, reg2)
        } else {
          // e2 first
          val evalExpr = transExpression(e2, symbolTable, reg2 +: reg1 +: regs) ++
            transExpression(e1, symbolTable, reg1 +: regs)
          evalExpr ++ transBinaryOperator(reg1, binOp, reg2)
        }

      case UnaryOperatorExpr(op, e) => {
        transExpression(e, symbolTable, reg1 +: reg2 +: regs) ++ transUnaryOperator(reg1, op)
      }

      case ArrayElement(identifier, index, elemtype) => {
        val variableReference = symbolTable.lookupDeep(identifier).get

        val instructions = Seq(
          ADD(reg1, FP, ImmOperand(variableReference.offset)),
          LDR(reg1, RegisterAddress(reg1, 0)),
          MOV(R0, reg2),
          MOV(R1, reg1),
          BL(checkArrayBoundsLabel),
          ADD(reg1, reg1, ImmOperand(4)),
          ADD(reg1, reg1, reg2),
          MOV(regs.head, ImmOperand(elemtype.size)),
          MUL(reg1, reg1, regs.head),
          LDR(reg1, RegisterAddress(reg1, 0))
        )

        transExpression(index.head, symbolTable, reg2 +: regs) ++ instructions

//        ADD r4, sp, #0
//        33		LDR r5, =77    //Up to expression
//        37		LDR r4, [r4]
//        38		MOV r0, r5
//        39		MOV r1, r4
//        40		BL p_check_array_bounds
//        41		ADD r4, r4, #4
//        42		ADD r4, r4, r5, LSL #2
//        43		LDRSB r4, [r4]
      }

      case VariableReference(name, _, offset) => Seq(LDR(reg1, RegisterAddress(FP, offset)))

      case IntegerLiteral(value) => Seq(MOV(reg1, ImmOperand(value)))

      case BoolLiteral(value)    => Seq(MOV(reg1, ImmOperand(if (value) 1 else 0)))

      case CharLiteral(value)    => Seq(MOV(reg1, CharOperand(value)))

      case StringLiteral(value)  => Seq(MOV(reg1, LabelAddress(DefineStringLabel(value).label)))

      case PairLiteral()         => Seq(MOV(reg1, ImmOperand(0)))
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
    expr match {
      case IntegerLiteral(value) => Seq(MOV(reg1, ImmOperand(value)))
      case BoolLiteral(value) => Seq(MOV(reg1, ImmOperand(if (value) 1 else 0)))
      case CharLiteral(value) => Seq(MOV(reg1, CharOperand(value)))
      case StringLiteral(value) => Seq(MOV(reg1, LabelAddress(DefineStringLabel(value).label)))
      case default => {
        val reg2 = getAnotherRegister(reg1)
        Seq(PUSH(Seq(reg2))) ++
          transExpressionReg(expr, reg1, reg2, symbolTable, regs) ++
          Seq(POP(Seq(reg2)))
      }
    }
  }

}