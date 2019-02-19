import cats.implicits._

object Main extends App {
  println("Hello, World!")
  println(
    Format("Hello, {{world | lower | capitalize}}!")(
      Map(Header.AHeader("world") -> "philippe")))
}

sealed trait Header { def toString: String }
object Header {
  case class AHeader(override val toString: String) extends Header

  def apply(toString: String) = AHeader(toString)
}

sealed trait Transformation {
  def toString: String
  def apply(args: Map[Header, String]): String
}

case class Format(template: String) extends Transformation {
  override def toString: String = template
  override def apply(args: Map[Header, String]): String = {
    """\{\{([^{]+)\}\}""".r
      .replaceAllIn(
        template, { m =>
          val headerName :: functionNames =
            m.group(1).split('|').map(_.trim).toList

          val header = args
            .get(Header(headerName))

          val function = functionNames
            .traverse(FormatFunction.fromString)
            .map { functions =>
              val empty: String => String = identity
              functions
                .map(_.format)
                .foldLeft(empty)(_.andThen(_))
            }

          (header, function).tupled.fold(m.toString) {
            case (header, function) => function(header)
          }
        }
      )
  }
}

sealed trait FormatFunction {
  def toString: String
  def format: String => String
}

object FormatFunction {
  case object Lower extends FormatFunction {
    override val toString: String = "lower"
    override val format = _.toLowerCase
  }

  case object Upper extends FormatFunction {
    override val toString: String = "upper"
    override val format = _.toUpperCase
  }

  case object Capitalize extends FormatFunction {
    override val toString: String = "capitalize"
    override val format = _.capitalize
  }

  def all = Iterable(Lower, Upper, Capitalize)
  def fromString(str: String): Option[FormatFunction] =
    all.find(_.toString.toLowerCase === str.toLowerCase)
}

case class Column(header: Header, transformation: Transformation)
