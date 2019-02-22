package io.iacto.core

import cats.implicits._
import fs2._

case class Format[F[_]](delimiter: String) {
  def read: Pipe[F, String, Map[Header, String]] = { in =>
    val rows = in
      .filter(_.trim.nonEmpty)
      .map(_.split(delimiter))

    val headers = rows.head.map(_.map(Header.apply))
    val body = rows.drop(1)

    headers.repeat
      .zip(body)
      .map { case (k, v) => k.zip(v).toMap }
  }

  def write: Pipe[F, Map[Header, String], String] = { in =>
    val headers = in.head.map(_.keys)
    val body = in.map(_.values)

    (headers ++ body).map(_.mkString(delimiter))
  }
}

object Format {
  def csv[F[_]] = Format[F](",")
}
