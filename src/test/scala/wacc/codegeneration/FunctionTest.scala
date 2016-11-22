package wacc.codegeneration

import wacc.{SymbolTable, TestUtilities}
import wacc.TransStatements._
import wacc.arm._
import wacc.visitors.{ProgramVisitor, StatementVisitor}

class FunctionTest extends CodeGenTest {

  it should "define a function and local variables relative to function SP" in {
    val parser = TestUtilities.setupParser("begin int foo() is int a = 1; int x = 32; return 1 end skip end")
    val program = TestUtilities.buildSubProgram(parser.program, ProgramVisitor).right.get
    val instructions = TestUtilities.translateWithoutSections(program.functions)

    println(instructions)
    instructions.head shouldBe DefineLabel(Label("foo"))
    instructions(1) shouldBe PUSH(Seq(LR))
    // dont care about body instructions since they are up to transStatement
    instructions(2) shouldBe SUB(SP, SP, ImmOperand(8))
    instructions(3) shouldBe MOV(R4,ImmOperand(1),ALWAYS)
    instructions(4) shouldBe STR(R4, RegisterAddress(SP, 0))
    instructions(5) shouldBe MOV(R4,ImmOperand(32),ALWAYS)
    instructions(6) shouldBe STR(R4, RegisterAddress(SP, 4))
    instructions(7) shouldBe MOV(R4,ImmOperand(1),ALWAYS)
    instructions(8) shouldBe MOV(R0, R4, ALWAYS)
    instructions(9) shouldBe ADD(SP, SP, ImmOperand(8))
    instructions(10) shouldBe POP(Seq(PC))
  }

  it should "define a function with arguments and be able to reference them" in {
    val parser = TestUtilities.setupParser("begin int goo(int a, int b) is bool c = true; return a end skip end")
    val program = TestUtilities.buildSubProgram(parser.program, ProgramVisitor).right.get
    val instructions = TestUtilities.translateWithoutSections(program)

    println(instructions)
    instructions.head shouldBe DefineLabel(Label("goo"))
    instructions(1) shouldBe PUSH(Seq(LR))
    // dont care about body instructions since they are up to transStatement
    instructions(2) shouldBe SUB(SP, SP, ImmOperand(1))
    instructions(3) shouldBe MOV(R4,ImmOperand(1),ALWAYS)
    instructions(4) shouldBe STRB(R4, RegisterAddress(SP, 0))
    instructions(5) shouldBe LDR(R4, RegisterAddress(SP, 5))
    instructions(6) shouldBe MOV(R0, R4)
    instructions(7) shouldBe ADD(SP, SP, ImmOperand(1))
    //instructions(8) shouldBe POP(Seq(PC)) is instead MOV(PC,LR,ALWAYS())
  }

}
