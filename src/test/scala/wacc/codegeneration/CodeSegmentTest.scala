package wacc.codegeneration

import wacc.arm._

class CodeSegmentTest extends CodeGenTest {

  "A code segment" should "be initialised empty" in {
     CodeSegment().instructions shouldBe empty
  }

  it should "build a code segment when appending instructions" in {
    val instruction = MOV(R0, R1)
    CodeSegment(instruction).instructions should contain (instruction)
  }

  it should "build a code segment when chaining append and extend" in {
    val instruction = MOV(R0, R1)


    CodeSegment(instruction).extend(MOV(R1, R2), MOV(R2, R3))
      .instructions should be (instruction +: Seq(MOV(R1, R2), MOV(R2, R3)))
  }

  it should "accept custom consumers" in {

    var registers : Seq[Register] = Seq()

    def registerAccesses(codeSegment: CodeSegment): Unit = {
      registers = codeSegment.instructions map {
        case MOV(r, _, _) => r
      }
    }
    CodeSegment(MOV(R1, R2), MOV(R2, R3)).release()(registerAccesses)
    registers should be (Seq(R1, R2))
  }
}
