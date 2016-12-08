package wacc.constructs

case class Program(structs: Seq[Struct], functions: Seq[Function], main: ScopeStatement)
