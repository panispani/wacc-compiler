package experimental

import org.antlr.v4.runtime.{BaseErrorListener, RecognitionException, Recognizer}

class SyntaxErrorListener() extends BaseErrorListener {

  override def syntaxError(recognizer: Recognizer[_, _],
                           offendingSymbol: scala.Any,
                           line: Int,
                           charPositionInLine: Int,
                           msg: String,
                           e: RecognitionException): Unit = {
    //super.syntaxError(recognizer, offendingSymbol, line, charPositionInLine, msg, e)
    println("Syntax error")
    System.exit(-100)
  }
}
