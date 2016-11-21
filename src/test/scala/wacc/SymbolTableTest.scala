package wacc

import org.scalatest.{BeforeAndAfterEach, FlatSpec, Matchers}
import wacc.TestUtilities.SymbolTableState
import wacc.constructs.{Boolean, Integer}

class SymbolTableTest extends FlatSpec
  with Matchers
  with SymbolTableState
  with BeforeAndAfterEach {

  override def beforeEach(): Unit = {
    // Seed the global table with some variables
    SymbolTable.currentTable.addLocalVariable("x", Boolean) // 0
    SymbolTable.currentTable.addLocalVariable("y", Integer) // 1
    SymbolTable.currentTable.addLocalVariable("z", Integer) // 5
  }

  "A parent lookup" should "have the correct offset" in {
    SymbolTable.openScope()

    SymbolTable.currentTable.addLocalVariable("a", Integer) // 0

    val xLookup = SymbolTable.currentTable.lookupDeep("x")
    xLookup.get.offset should be (-9)

    val yLookup = SymbolTable.currentTable.lookupDeep("y")
    yLookup.get.offset should be (-8)

    SymbolTable.closeScope()
  }

  it should "preserve the type and identifier of the looked up variable" in {
    SymbolTable.openScope()

    SymbolTable.currentTable.lookupDeep("x").get.name should be ("x")
    SymbolTable.currentTable.lookupDeep("x").get.vartype should be (Boolean)

    SymbolTable.currentTable.lookupDeep("y").get.name should be ("y")
    SymbolTable.currentTable.lookupDeep("y").get.vartype should be (Integer)
  }

  "A deeper lookup" should "have the correct offset" in {
    SymbolTable.openScope()

    SymbolTable.currentTable.addLocalVariable("a", Integer) // 0
    SymbolTable.currentTable.addLocalVariable("b", Integer) // 4

    SymbolTable.openScope()

    SymbolTable.currentTable.lookupDeep("x").get.offset should be (-8 + (-9))

    SymbolTable.closeScope()

    SymbolTable.closeScope()
  }
}
