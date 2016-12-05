package wacc

import org.antlr.v4.runtime.{BaseErrorListener, RecognitionException, Recognizer}

class ISyntaxErrorListener() extends BaseErrorListener {

  override def syntaxError(recognizer: Recognizer[_, _],
                           offendingSymbol: scala.Any,
                           line: Int,
                           charPositionInLine: Int,
                           msg: String,
                           e: RecognitionException): Unit = {
    System.err.println("Syntax error")
  }
}