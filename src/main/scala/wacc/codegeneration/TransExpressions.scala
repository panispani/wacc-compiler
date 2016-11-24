package wacc.codegeneration

import wacc.{SymbolTable, VariableReference}
import wacc.arm._
import wacc.constructs._

object TransExpressions {
  def transExpression(expr: Expression, symbolTable: SymbolTable, registers: Seq[Register]): Seq[Instruction] = {
    registers match {
      case (r1::r2::regs) => transExpressionReg(expr, r1, r2, symbolTable, regs)
      case (r1::regs) => transExpressionAcc(expr, r1, symbolTable: SymbolTable, regs)
    }
  }

  // Register machine approach
  private def transExpressionReg(expr: Expression, reg1: Register, reg2: Register, symbolTable: SymbolTable, regs: Seq[Register]): Seq[Instruction] = {
    def weight(e: Expression): Integer = {
      e match {
        case BinaryOperatorExpr(e1, binOp, e2) => {
          val e1Weight = weight(e1)
          val e2Weight = weight(e2)
          val cost1 = math.max(e1Weight, e2Weight + 1)
          val cost2 = math.max(e1Weight + 1, e2Weight)
          math.min(cost1, cost2)
        }
        case default => 1
      }
      0
    }

    expr match {
      case BinaryOperatorExpr(e1, binOp, e2) =>
        if (weight(e1) > weight(e2)) {
          // e1 first
          val evalExpr = transExpression(e1, symbolTable, reg1 +: reg2 +: regs) ++
            transExpression(e2, symbolTable, reg2 +: regs)
          evalExpr ++ TransBinaryOperators.transBinaryOperator(reg1, binOp, reg2)
        } else {
          // e2 first
          val evalExpr = TransExpressions.transExpression(e2, symbolTable, reg2 +: reg1 +: regs) ++
            TransExpressions.transExpression(e1, symbolTable, reg1 +: regs)
          evalExpr ++ TransBinaryOperators.transBinaryOperator(reg1, binOp, reg2)
        }

      case UnaryOperatorExpr(op, e) =>
        transExpression(e, symbolTable, reg1 +: reg2 +: regs) ++ op.translate(reg1).instructions

      case ArrayElement(identifier, index, elemtype) => {
        val variableReference = symbolTable.lookupDeep(identifier).get
        val check = Macros.checkArrayBounds(variableReference, index.head, symbolTable, reg1 +: reg2 +: regs).instructions
        //reg1 is now going to contain the value of the index expression
        check ++ Macros.getArrayElemAddress(variableReference, symbolTable, reg1 +: reg2 +: regs, elemtype).instructions ++ Seq(
          LDR(reg1, RegisterAddress(reg1, 0))
        )
      }

      case VariableReference(name, vartype, offset) => Seq(Macros.load(reg1, offset, vartype))
      case IntegerLiteral(value) => Seq(MOV(reg1, ImmOperand(value)))
      case BoolLiteral(value)    => Seq(MOV(reg1, ImmOperand(if (value) 1 else 0)))
      case CharLiteral(value)    => Seq(MOV(reg1, CharOperand(value)))
      case StringLiteral(value)  => Seq(LDR(reg1, LabelAddress(DefineStringLabel(value).label)))
      case PairLiteral()         => Seq(MOV(reg1, ImmOperand(0)))
    }
  }

  // Accumulator machine approach
  private def getAnotherRegister(reg: Register) = {
    if (reg == R5)
      R4
    else
      R5
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