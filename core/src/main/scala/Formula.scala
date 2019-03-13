package com.github.plippe.funnel.core

import cats.implicits._

case class Formula(formula: String) {
  def apply(args: Map[Header, String]): String = {
    """\{\{([^{]+)\}\}""".r
      .replaceAllIn(
        formula, { m =>
          val headerName :: functionNames =
            m.group(1).split('|').map(_.trim).toList

          val header = args
            .get(Header(headerName))

          val function = functionNames
            .traverse(FormulaFunction.fromString)
            .map { functions =>
              val empty: String => String = identity
              functions
                .map(_.apply)
                .foldLeft(empty)(_.andThen(_))
            }

          (header, function).tupled.fold(m.toString) {
            case (header, function) => function(header)
          }
        }
      )
  }
}

sealed trait FormulaFunction {
  def toString: String
  def apply: String => String
}

object FormulaFunction {
  case object Lower extends FormulaFunction {
    override val toString: String = "lower"
    override val apply = _.toLowerCase
  }

  case object Upper extends FormulaFunction {
    override val toString: String = "upper"
    override val apply = _.toUpperCase
  }

  case object Capitalize extends FormulaFunction {
    override val toString: String = "capitalize"
    override val apply = _.capitalize
  }

  def all = Iterable(Lower, Upper, Capitalize)
  def fromString(str: String): Option[FormulaFunction] =
    all.find(_.toString.toLowerCase === str.toLowerCase)
}
