package wacc.codegeneration

import wacc.arm._
import wacc.constructs.ForLoopStatement
import wacc.visitors.StatementVisitor
import wacc.{SymbolTable, TestUtilities}

class ConditionalStatementTest extends CodeGenTest {

  ignore should "check condition and provide alternative branches" in {
    val parser = TestUtilities.setupParser("if (1 == 1) then exit 5 else skip fi")
    val program = TestUtilities.buildSubProgram(parser.statement, StatementVisitor)
    val availableRegisters = Seq(R0, R1, R2, R3, R4, R5, R6, R7, R8)

    val instructions = TransStatements.transStatement(program.right.get, availableRegisters)
    println(instructions)
    instructions.head shouldBe MOV(R1,ImmOperand(1),ALWAYS)
    instructions(1) shouldBe MOV(R0,ImmOperand(1),ALWAYS)
    instructions(2) shouldBe CMP(R0,R1)
    instructions(3) shouldBe MOV(R0,ImmOperand(1),EQ)
    instructions(4) shouldBe MOV(R0,ImmOperand(0),NE)
    instructions(5) shouldBe CMP(R0,ImmOperand(0))
    instructions(6) shouldBe B(Label("L0"),EQ)
    instructions(7) shouldBe B(Label("L1"),ALWAYS)
    instructions(8) shouldBe DefineLabel(Label("L0"))
    // dont care about branch instructions since they are up to transStatement
    instructions.last shouldBe DefineLabel(Label("L1"))
  }

  "if simple" should "branch to the end of instructions" in {
    val parser = TestUtilities.setupParser("if true then int i = 0 fi")
    val program = TestUtilities.buildSubProgram(parser.statement, StatementVisitor)
    val availableRegisters = Seq(R0, R1, R2, R3, R4, R5, R6, R7, R8)

    val instructions = TransStatements.transStatement(program.right.get, availableRegisters)

    val labels = instructions.find({
      case B(_, _) => true
      case _ => false
    })

    labels.size shouldBe 1
    labels.head should be (B(Label("L0"), NE))
    instructions.last should be (DefineLabel(Label("L0")))

  }


  "if else" should "branch twice" in {
    val parser = TestUtilities.setupParser("if true then int i = 0 else skip fi")
    val program = TestUtilities.buildSubProgram(parser.statement, StatementVisitor)
    val availableRegisters = Seq(R0, R1, R2, R3, R4, R5, R6, R7, R8)

    val instructions = TransStatements.transStatement(program.right.get, availableRegisters)

    val labels = instructions.filter({
      case B(_, _) => true
      case _ => false
    })

    val defines = instructions.filter({
      case DefineLabel(_) => true
      case _ => false
    })

    labels.size shouldBe 2
    labels.head should be (B(Label("L0"), EQ))
    labels(1) should be (B(Label("L1"), EQ))
    defines.head should be (DefineLabel(Label("L0")))
    instructions.last should be (DefineLabel(Label("L1")))

  }

  "if recursive" should "branch recursively" in {
    val parser = TestUtilities.setupParser("if true then int i = 0 else if true then skip else int j = 2 fi")
    val program = TestUtilities.buildSubProgram(parser.statement, StatementVisitor)
    val availableRegisters = Seq(R0, R1, R2, R3, R4, R5, R6, R7, R8)

    val instructions = TransStatements.transStatement(program.right.get, availableRegisters)

    val labels = instructions.filter({
      case B(_, _) => true
      case _ => false
    })

    val defines = instructions.filter({
      case DefineLabel(_) => true
      case _ => false
    })

    labels.size shouldBe 4
    labels.head should be (B(Label("L0"), EQ))
    labels(1) should be (B(Label("L2"), EQ))
    labels(2) should be (B(Label("L3"), EQ))
    labels(3) should be (B(Label("L1"), EQ))
    defines.head should be (DefineLabel(Label("L2")))
    defines(1) should be (DefineLabel(Label("L3")))
    defines(2) should be (DefineLabel(Label("L0")))
    instructions.last should be (DefineLabel(Label("L1")))
  }
}
