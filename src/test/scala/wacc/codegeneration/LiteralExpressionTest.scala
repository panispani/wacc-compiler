package wacc.codegeneration

import wacc.SymbolTable
import wacc.arm._
import wacc.constructs.{IntegerLiteral, PairLiteral}
/**
  * Created by tt1215 on 18/11/16.
  */
class LiteralExpressionTest extends CodeGenTest {

  "The value of an expression consisting of integer" should "be stored in the first available register" in {
    val availableRegisters = Seq(R4, R5, R6)
    val instructions = TransExpressions.transExpression(IntegerLiteral(1), availableRegisters)

    instructions.size should be (1)
    instructions.head should be (LDR(R4, Const(1)))
  }

  "The value of an expression consisting of pair literal" should "be stored in the first available register" in {
    val availableRegisters = Seq(R4, R5, R6)
    val instructions = TransExpressions.transExpression(PairLiteral(), availableRegisters)

    instructions.size should be (1)
    instructions.head should be (LDR(R4, Const(0)))
  }
}
