package wacc.visitors

import antlr.WACCParser._
import antlr.WACCParserBaseVisitor
import wacc.constructs.{CharLiteral, StringLiteral, _}
import java.lang.Integer.parseInt

/**
  * It only visits expression literals
  * (excludes array literal which is implemented separately)
  */
object  LiteralVisitor extends WACCParserBaseVisitor[Either[CompilationError, Literal]] {


  override def visitDecLiteral(ctx: DecLiteralContext): Either[CompilationError, Literal] = {
    try {
      Right(IntegerLiteral(ctx.getText.toInt))
    } catch {
      case e: NumberFormatException => Left(SyntaxError("integer literal not in range", ctx.start))
    }
  }

  override def visitHexLiteral(ctx: HexLiteralContext): Either[CompilationError, Literal] = {
    try {
      Right(IntegerLiteral(parseInt(ctx.getText.drop(2), 16)))
    } catch {
      case e: NumberFormatException => Left(SyntaxError("integer literal not in range", ctx.start))
    }
  }

  override def visitOctalLiteral(ctx: OctalLiteralContext): Either[CompilationError, Literal] = {
    try {
      if (ctx.getText == "0") {
        Right(IntegerLiteral(0))
      } else {
        Right(IntegerLiteral(parseInt(ctx.getText.drop(1), 8)))
      }
    } catch {
      case e: NumberFormatException => Left(SyntaxError("integer literal not in range", ctx.start))
    }
  }

  override def visitBinaryLiteral(ctx: BinaryLiteralContext): Either[CompilationError, Literal] = {
    try {
      Right(IntegerLiteral(parseInt(ctx.getText.drop(2), 2)))
    } catch {
      case e: NumberFormatException => Left(SyntaxError("integer literal not in range", ctx.start))
    }
  }

  override def visitBoolLiteral(ctx: BoolLiteralContext): Either[CompilationError, BoolLiteral]
    = Right(BoolLiteral(ctx.getText.toBoolean))

  override def visitCharLiteral(ctx: CharLiteralContext): Either[CompilationError, CharLiteral] = {
    val charLiteral = ctx.getText
    val charAt1 = charLiteral.charAt(1) // 0 is a quote
    // Handle escaped character by preserving the backslash so that we can output it in the ASM file
    val charString = if (charAt1 == '\\') "\\" + charLiteral.charAt(2) else charAt1.toString
    Right(CharLiteral(charString))
  }

  override def visitStringLiteral(ctx: StringLiteralContext): Either[CompilationError, StringLiteral]
    = {
    //println("I reached this point having on my back " + ctx.getText + " " + ctx.getText.size)
    Right(StringLiteral(ctx.getText))
  }

  override def visitPairLiteral(ctx: PairLiteralContext): Either[CompilationError, PairLiteral]
    = Right(PairLiteral())
}
