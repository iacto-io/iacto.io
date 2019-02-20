package io.iacto

object Main extends App {
  println("Hello, World!")
  println(
    Formula("Hello, {{world | lower | capitalize}}!")(
      Map(Header("world") -> "philippe")))
}
