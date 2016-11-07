package experimental

import org.scalatest.{FlatSpec, Matchers}

class ArrayLiteralTest extends FlatSpec with Matchers {

 "An array literal" should "pass the semantic check when empty" in {
   val arrayLiteral = ArrayLiteral(Seq())
   arrayLiteral.semanticCheck should be (true)
 }

  it should "pass semantic check when elements have the same type" in {
    val arrayLiteral = ArrayLiteral(Seq(IntegerLiteral(1), IntegerLiteral(2), IntegerLiteral(3)))
    arrayLiteral.semanticCheck should be (true)
  }

  it should "fail semantic check when elements have different types" in {
    val arrayLiteral = ArrayLiteral(Seq(IntegerLiteral(1), BoolLiteral(true), IntegerLiteral(3)))
    arrayLiteral.semanticCheck should be (false)
  }

  it should "respect the element type in it's value type" in {
    val arrayLiteral = ArrayLiteral(Seq(IntegerLiteral(1), IntegerLiteral(3)))
    arrayLiteral.valueType should be (Array(Integer))
  }
}
