package wacc

import wacc.constructs.{ArrayType, AnyType, PairType, Type}

package object visitor {

  def sequence[A, B](s: Seq[Either[A, B]]): Either[A, Seq[B]] =
    s.foldRight(Right(Nil): Either[A, List[B]]) {
      (e, acc) => for (xs <- acc.right; x <- e.right) yield x :: xs
    }

  def compatibleTypes(a: Type, b: Type): Boolean = {
    b == AnyType || a == AnyType || (b match {
      case ArrayType(AnyType) => a.isInstanceOf[ArrayType]
      case ArrayType(PairType(x1, y1)) => a match {
        case ArrayType(PairType(x2, y2)) => compatibleTypes(x1, x2) && compatibleTypes(y1, y2)
        case default => false
      }
      case PairType(aFst, aSnd) => a match {
        case PairType(bFst, bSnd) => compatibleTypes(aFst, bFst) && compatibleTypes(aSnd, bSnd)
        case default => a == b
      }
      case default => a == b
    })
  }
}
