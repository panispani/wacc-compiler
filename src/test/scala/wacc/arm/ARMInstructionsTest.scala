package wacc.arm

import wacc.codegeneration.CodeGenTest

class ARMInstructionsTest extends CodeGenTest {

  "ADD" should "be translated correctly" in {
    ADD(R0, R1, ImmOperand(1)).toString shouldBe "ADD R0, R1, #1"
  }

  it should "be translated with conditionals" in {
    ADD(R0, R1, ImmOperand(1), GT).toString shouldBe "ADDGT R0, R1, #1"
  }

  "PUSH" should "be translated correctly" in {
    PUSH(Seq(R0, R1, R2)).toString shouldBe "PUSH {R0, R1, R2}"
  }

  "MUL" should "be translated correctly" in {
    MUL(R1, R2, R3).toString shouldBe "MUL R1, R2, R3"
  }

  "RETURN" should "be translated correctly" in {
    RETURN.toString shouldBe "POP {PC}"
  }

  "NEW_STACK_FRAME" should "be translated correctly" in {
    NEW_STACK_FRAME.toString shouldBe "PUSH {LR}"
  }
}
