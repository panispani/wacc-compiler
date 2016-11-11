package wacc

import wacc.constructs.{ArrayType, AssignValue, ErasedPair, NullType, PairType, Type}

package object visitor {

  def sequence[A, B](s: Seq[Either[A, B]]): Either[A, Seq[B]] =
    s.foldRight(Right(Nil): Either[A, List[B]]) {
      (e, acc) => for (xs <- acc.right; x <- e.right) yield x :: xs
    }

  def compatibleTypes(lhs: AssignValue, rhs: AssignValue): Boolean = {
    rhs.vartype match {
      case NullType            => lhs.vartype.isInstanceOf[PairType] || lhs.vartype.isInstanceOf[ErasedPair]
      case ArrayType(NullType) => lhs.vartype.isInstanceOf[ArrayType]
      case PairType(x, y)      => {
        lhs.vartype match {
          case PairType(a, b) =>
            val lhsFstType = new AssignValue {override val vartype: Type = a}
            val rhsFstType = new AssignValue {override val vartype: Type = x}

            val lhsSndType = new AssignValue {override val vartype: Type = b}
            val rhsSndType = new AssignValue {override val vartype: Type = y}

            compatibleTypes(lhsFstType, rhsFstType) && compatibleTypes(lhsSndType, rhsSndType)
          case default        => lhs.vartype == rhs.vartype
        }
      }
      case default             => lhs.vartype == rhs.vartype
    }
  }
}
