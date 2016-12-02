package wacc.codegeneration

import wacc.SymbolTable
import wacc.arm._
import wacc.constructs.{IntegerLiteral, PairLiteral}

class LiteralExpressionTest extends CodeGenTest {

  "The value of an expression consisting of integer" should "be stored in the first available register" in {
    val availableRegisters = Seq(R4, R5, R6)
    val instructions       = IntegerLiteral(1).transAssignRhs(availableRegisters).instructions

    instructions.size should be (1)
    instructions.head should be (LDR(R4, Const(1)))
  }

  "The value of an expression consisting of pair literal" should "be stored in the first available register" in {
    val availableRegisters = Seq(R4, R5, R6)
    val instructions       = PairLiteral().transAssignRhs(availableRegisters).instructions

    instructions.size should be (1)
    instructions.head should be (LDR(R4, Const(0)))
  }
}
