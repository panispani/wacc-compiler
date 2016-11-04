package experimental

import org.antlr.v4.runtime.{BaseErrorListener, RecognitionException, Recognizer}

class SyntaxErrorListener() extends BaseErrorListener {

  override def syntaxError(recognizer: Recognizer[_, _],
                           offendingSymbol: scala.Any,
                           line: Int,
                           charPositionInLine: Int,
                           msg: String,
                           e: RecognitionException): Unit = {
    println("Syntax error")
    System.exit(100)
  }
}
