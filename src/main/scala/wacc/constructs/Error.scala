package wacc.constructs

/**
  * Created by panayiotis on 08/11/16.
  */
case class Error(errorType: String, errorMessage: String) {
  println(errorType + ": " + errorMessage)
  val errorCode = getErrorCode(errorType)
  if (errorCode != 0) {
    System.exit(errorCode)
  }

  def getErrorCode(errorType: String): Int = errorType match {
    case "Semantic"  => 200
    case "Syntactic" => 100
    case _           => 0
  }
}
