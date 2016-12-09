package wacc.constructs

case class Program(structs: Seq[StructType], functions: Seq[Function], main: ScopeStatement)
