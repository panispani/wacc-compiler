package wacc.visitors

import org.scalatest.{EitherValues, FlatSpec, Matchers}
import wacc.{FunctionReference, SymbolTable}
import wacc.constructs.{ExitStatement, Function, Integer, IntegerLiteral, Param, ReturnStatement, SemanticError}

/**
  * Created by ema on 09/11/2016.
  */
class FunctionVisitorTest extends FlatSpec
  with Matchers
  with EitherValues {

  "Visiting a function" should "create function construct" in {
    val parser = TestUtilities.setupParser("int f() is return 3 end")
    val result = TestUtilities.buildSubProgram(parser.function, FunctionVisitor)

    result.right.value should be (Function("f", Seq(), Integer, Seq(ReturnStatement(IntegerLiteral(3)))))
  }

  it should "add the function reference to the symbol table" in {
    val parser = TestUtilities.setupParser("int f() is return 3 end")
    val result = TestUtilities.buildSubProgram(parser.function, FunctionVisitor)

    SymbolTable.globalTable.lookup("f") should contain (FunctionReference(Integer, Seq()))
  }

}
