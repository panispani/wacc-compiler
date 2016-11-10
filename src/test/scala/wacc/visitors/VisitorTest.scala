package wacc.visitors

import org.scalatest.{EitherValues, FlatSpec, Matchers}
import wacc.visitors.TestUtilities.SymbolTableState

trait VisitorTest extends FlatSpec with Matchers with EitherValues with SymbolTableState
