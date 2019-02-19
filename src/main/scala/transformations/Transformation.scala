package io.iacto.transformations

trait Transformation {
  def toString: String
  def apply(args: Map[io.iacto.Header, String]): String
}
