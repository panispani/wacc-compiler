package wacc.visitors

import wacc.{SymbolTable, TestUtilities}
import wacc.arm._
import wacc.constructs.{ScopeStatement, _}

class IfStatementTest extends VisitorTest {
  "If" should "throw a semantic error if the expression given is not a bool " in {
    val input =
      "begin " +
        "  pair(int, int) p = newpair(1,2); " +
        "  if (p) then " +
        "    skip " +
        "  else " +
        "    skip " +
        "  fi " +
        "end"

    val program = TestUtilities.buildProgram(input)

    program.left.value should (be (a[SemanticError]) or be (a[List[_]]))
  }

  "Simple If" should "be a ConditionalSimpleStatement" in {
    val parser = TestUtilities.setupParser("if true then skip fi")
    val result = TestUtilities.buildSubProgram(parser.conditionalStatement, ConditionalVisitor)
    val registers = Seq(R0, R1, R2, R3, R4, R5, R6, R7, R8)


    result.right.value should be (ConditionalSimpleStatement(
                                    BoolLiteral(true),
                                    ScopeStatement(List(SkipStatement()),
                                                   SymbolTable(Some(SymbolTable(None))))))
  }

  "Else If" should "be a ConditionalElseStatement" in {
    val parser = TestUtilities.setupParser("if true then skip else skip fi")
    val result = TestUtilities.buildSubProgram(parser.conditionalStatement, ConditionalVisitor)
    val registers = Seq(R0, R1, R2, R3, R4, R5, R6, R7, R8)
    println(result.right.value)

    result.right.value should be (ConditionalElseStatement(
                                    BoolLiteral(true),
                                    ScopeStatement(List(SkipStatement()),SymbolTable(Some(SymbolTable(None)))),
                                    ScopeStatement(List(SkipStatement()),SymbolTable(Some(SymbolTable(None))))))
  }

  "Recursive If" should "be a ConditionalRecursiveStatement" in {
    val parser = TestUtilities.setupParser("if true then skip else if false then skip else skip fi")
    val result = TestUtilities.buildSubProgram(parser.conditionalStatement, ConditionalVisitor)

    result.right.value should be (ConditionalRecursiveStatement(
                                    BoolLiteral(true),
                                    ScopeStatement(List(SkipStatement()),SymbolTable(Some(SymbolTable(None)))),
                                    ConditionalElseStatement(
                                        BoolLiteral(false),
                                        ScopeStatement(List(SkipStatement()),SymbolTable(Some(SymbolTable(None)))),
                                        ScopeStatement(List(SkipStatement()),SymbolTable(Some(SymbolTable(None)))))))
  }

}


