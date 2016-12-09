package wacc

import wacc.constructs.{AnyType, ArrayType, PairType, StructType, Type}

package object visitors {

  def sequenceOrLast[A, B](s: Seq[Either[A, B]]): Either[A, Seq[B]] =
    s.foldRight(Right(Nil): Either[A, List[B]]) {
      (e, acc) => for (xs <- acc.right; x <- e.right) yield x :: xs
    }

  def sequenceOrAll[A, B](s: Seq[Either[A, B]]): Either[Seq[A], Seq[B]] =
    s.partition(_.isLeft) match {
      case (Nil, xs) => Right(for(Right(x) <- xs) yield x)
      case (es, _) => Left(for(Left(e) <- es) yield e)
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
      case StructType(_, bs, _) => a match {
        case StructType(_, as, _) => (as zip bs).forall(p => compatibleTypes(p._1.varType, p._2.varType))
        case default => false
      }

      case default => a == b
    })
  }
}
