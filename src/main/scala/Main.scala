object Main extends App {
  println("Hello, World!")
  println(Format("Hello, {{world}}!")(Map(AHeader("world") -> "Philippe")))
}

sealed trait Header { def toString: String }
case class AHeader(override val toString: String) extends Header

sealed trait Transformation {
  def toString: String
  def apply(args: Map[Header, String]): String
}

case class Format(template: String) extends Transformation {
  override def toString: String = template
  override def apply(args: Map[Header, String]): String = {
    args.foldLeft(template) {
      case (str, arg) =>
        val (name, value) = arg
        str.replaceAllLiterally(s"{{$name}}", value)
    }
  }
}

case class Column(header: Header, transformation: Transformation)
