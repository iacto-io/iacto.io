package io.iacto

import io.iacto.transformations._

object Main extends App {
  println("Hello, World!")
  println(
    Format("Hello, {{world | lower | capitalize}}!")(
      Map(Header.AHeader("world") -> "philippe")))
}
