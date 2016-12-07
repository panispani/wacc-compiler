package wacc.constructs

import wacc.{SymbolTable, VariableReference}
import wacc.arm._
import wacc.codegeneration._

trait Expression extends AssignValue with AssignTarget {

  override def transAssignRhs(registers: Seq[Register]): CodeSegment = {
    registers match {
      case (r1::r2::regs) => transExpressionReg(r1, r2, regs)
      case (r1::regs)     => transExpressionAcc(r1, regs)
    }
  }

  // Register machine approach
  private def transExpressionReg(reg1: Register, reg2: Register, regs: Seq[Register]): CodeSegment = {
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

    this match {
      case e @ BinaryOperatorExpr(e1, binOp, e2) =>
        if (weight(e1) > weight(e2)) {
          e1.transAssignRhs(reg1 +: reg2 +: regs)    // e1 first
            .extend(e2.transAssignRhs(reg2 +: regs))
            .extend(binOp.translate(e.varType, reg1, reg2))
        } else {
          e2.transAssignRhs(reg2 +: reg1 +: regs)    // e2 first
            .extend(e1.transAssignRhs(reg1 +: regs))
            .extend(binOp.translate(e.varType, reg1, reg2))
        }

      case UnaryOperatorExpr(op, e) =>
        e.transAssignRhs(reg1 +: reg2 +: regs)
         .extend(op.translate(reg1))

      case ArrayElement(variableReference, index, elemType) => {

        val load = elemType match {
          case Character | Boolean => LDRB(reg1, RegisterAddress(reg1, 0))
          case default             => LDR(reg1, RegisterAddress(reg1, 0))
        }

        CodeSegment(ADD(reg1, FP, ImmOperand(variableReference.offset)))
          .extend(Macros.getNestedElementAddress(index, reg1 +: reg2 +: regs, elemType.size))
          .extend(load)
      }

      case sm @ StructMember(struct, membersName) => {
        struct.varType match {
          case StructType(structId, _) => {

            val load = sm.varType match {
              case Character | Boolean => LDRB(reg1, RegisterAddress(reg1, 0))
              case default             => LDR(reg1, RegisterAddress(reg1, 0))
            }

            //TODO: handle memberName not existing in frontend

            val membersOffset =

            CodeSegment(ADD(reg1, FP, ImmOperand(struct.offset)))
              .extend(Macros.getNestedStructMemberAddress())
              .extend(load)
          }
        }
      }

      case VariableReference(name, varType, offset) => CodeSegment(Macros.load(reg1, offset, varType))
      case IntegerLiteral(value) => CodeSegment(LDR(reg1, Const(value)))
      case BoolLiteral(value)    => CodeSegment(LDR(reg1, Const(if (value) 1 else 0)))
      case CharLiteral(value)    => CodeSegment(MOV(reg1, CharOperand(value)))
      case StringLiteral(value)  => CodeSegment(LDR(reg1, LabelAddress(DefineStringLabel(value).label)))
      case PairLiteral()         => CodeSegment(LDR(reg1, Const(0)))
    }
  }

  // Accumulator machine approach
  private def getAnotherRegister(reg: Register) = {
    if (reg == R5)
      R4
    else
      R5
  }

  private def transExpressionAcc(reg1: Register, regs: Seq[Register]): CodeSegment = {
    this match {
      case IntegerLiteral(value) => CodeSegment(LDR(reg1, Const(value)))
      case BoolLiteral(value)    => CodeSegment(MOV(reg1, ImmOperand(if (value) 1 else 0)))
      case CharLiteral(value)    => CodeSegment(MOV(reg1, CharOperand(value)))
      case StringLiteral(value)  => CodeSegment(MOV(reg1, LabelAddress(DefineStringLabel(value).label)))
      case default => {
        val reg2 = getAnotherRegister(reg1)

        CodeSegment()
          .extend(Seq(PUSH(Seq(reg2))))
          .extend(transExpressionReg(reg1, reg2, regs))
          .extend(POP(Seq(reg2)))
      }
    }
  }
}