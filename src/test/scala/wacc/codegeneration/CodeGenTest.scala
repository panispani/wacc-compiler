package wacc.codegeneration

import org.scalatest.{EitherValues, FlatSpec, Matchers}
import wacc.TestUtilities.{LabelCreationState, SymbolTableState}

trait CodeGenTest extends FlatSpec with Matchers with EitherValues with SymbolTableState with LabelCreationState
