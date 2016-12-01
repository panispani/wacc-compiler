package wacc

import org.scalatest.{BeforeAndAfterEach, FlatSpec, Matchers}
import wacc.TestUtilities.SymbolTableState
import wacc.constructs.{Boolean, Integer}

class SymbolTableTest extends FlatSpec
  with Matchers
  with SymbolTableState
  with BeforeAndAfterEach {

  override def beforeEach(): Unit = {
    // Seed the table with the example below
    SymbolTable().addLocalVariable("x", Integer)
      SymbolTable.openScope()
      SymbolTable().addLocalVariable("y", Boolean)
      SymbolTable().addLocalVariable("z", Integer)
        SymbolTable.openScope()
        SymbolTable().addLocalVariable("a", Integer)
  }

  /** Symbol table                 ARM11 Stack (offsets relative to SP at time of adding)
    *                              On entering a scope the order is: PUSH FP, FP = SP, SP -= size of this scope
    *  ---
    * |x:-4|                              store each local variable      STR Ri, [FP, #offset]
    *  --- size=4                         access any reachable variable  LDR Ri, [FP, #offset]
    *                              |x:-4|
    *   parent of                  | FP |
    *       ---                    |y:-1|
    *      |y:-1|                  |z:-5|
    *      |z:-5|                  | FP | <- The value of FP is the address above it
    *       --- size=5             |a:-4|
    *        parent of |a:-4|
    *
    * lookup(x).offset = size(GP) + off(x) + size(P) + 2 * size(FP) = 4 - 4 + 5 + 8 = 13
    * lookup(y).offset = size(P) + off(y) + size(FP) = 5 - 1 + 4 = 8
    * lookup(z).offset = size(P) + off(z) + size(FP) = 4
    * lookup(a).offset = -4
    * */

  "A parent lookup" should "have the correct offset" in {
    SymbolTable().lookupDeep("y").get.offset should be (8)
  }

  it should "preserve the type and identifier of the looked up variable" in {
    SymbolTable().lookupDeep("x").get.name should be ("x")
    SymbolTable().lookupDeep("x").get.varType should be (Integer)

    SymbolTable().lookupDeep("y").get.name should be ("y")
    SymbolTable().lookupDeep("y").get.varType should be (Boolean)
  }

  "A deeper lookup" should "have the correct offset" in {
    SymbolTable().lookupDeep("x").get.offset should be (13)
  }
}
