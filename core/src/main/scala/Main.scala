package io.iacto.core

import cats.implicits._
import cats.effect._
import fs2._
import java.nio.file.Paths
import scala.concurrent.ExecutionContext

object Main extends IOApp {
  def run(args: List[String]): IO[ExitCode] = {
    println("Hello, World!")
    println(
      Formula("Hello, {{world | lower | capitalize}}!")(
        Map(Header("world") -> "philippe")))

    val schema = Schema(
      List(
        Column(Header("first"), Formula("{{first_name}}")),
        Column(Header("last"), Formula("{{last_name}}")),
        Column(Header("email"),
               Formula("{{first_name}}.{{last_name}}@company.com")),
      ))

    val file = Paths.get("core/src/main/resources/mockaroo.small.csv")
    val format = Format.csv[IO]

    io.file
      .readAll[IO](file, ExecutionContext.global, 4096)
      .through(text.utf8Decode)
      .through(text.lines)
      .through(format.read)
      .map(schema.apply)
      .through(format.write)
      .map(println)
      .compile
      .drain
      .as(ExitCode.Success)
  }
}
