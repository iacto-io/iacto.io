package io.iacto.transformations

import cats.implicits._

case class Format(template: String) extends Transformation {
  override def toString: String = template
  override def apply(args: Map[io.iacto.Header, String]): String = {
    """\{\{([^{]+)\}\}""".r
      .replaceAllIn(
        template, { m =>
          val headerName :: functionNames =
            m.group(1).split('|').map(_.trim).toList

          val header = args
            .get(io.iacto.Header(headerName))

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
