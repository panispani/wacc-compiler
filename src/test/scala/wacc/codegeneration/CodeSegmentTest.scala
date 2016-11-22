package wacc.codegeneration

class CodeSegmentTest extends CodeGenTest {

  "A code segment" should "be initialised empty" in {
     new CodeSegment().instructions shouldBe empty
  }

  it should "build a code segment when appending instructions" in {
    val instruction = MOV(R0, R1)
    new CodeSegment().append(instruction).instructions should contain (instruction)
  }

  it should "build a code segment when chaining append and extend" in {
    val instruction = MOV(R0, R1)
    val instructions = Seq(
      MOV(R1, R2),
      MOV(R2, R3)
    )

    new CodeSegment()
      .append(instruction)
      .extend(instructions)
      .instructions should be (instruction +: instructions)
  }

  it should "accept custom consumers" in {
    val instructions = Seq(
      MOV(R1, R2),
      MOV(R2, R3)
    )

    var registers : Seq[Register] = Seq()

    def registerAccesses(codeSegment: CodeSegment): Unit = {
      registers = codeSegment.instructions map {
        case MOV(r, _, _) => r
      }
    }
    new CodeSegment().extend(instructions).release()(registerAccesses)
    registers should be (Seq(R1, R2))
  }
}
