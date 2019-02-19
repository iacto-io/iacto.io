package io.iacto

sealed trait Header { def toString: String }
object Header {
  case class AHeader(override val toString: String) extends Header

  def apply(toString: String) = AHeader(toString)
}
