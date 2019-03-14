package com.github.plippe.funnel.server

import cats.effect._
import cats.implicits._
import io.circe.generic.auto._
import org.http4s.circe.jsonOf
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.server.blaze._

object Main extends IOApp {

  val schemas = {
    implicit val r = jsonOf[IO, services.schemas.Form]

    services.schemas.Routes[IO](
      () => services.schemas.IndexHandler[IO],
      services.schemas.CreateHandler[IO],
      services.schemas.ShowHandler[IO],
      services.schemas.UpdateHandler[IO],
      services.schemas.DestroyHandler[IO],
    )
  }

  val httpApp = Router(
    "/schemas" -> schemas
  ).orNotFound

  def run(args: List[String]): IO[ExitCode] =
    BlazeServerBuilder[IO]
      .bindHttp(8080, "localhost")
      .withHttpApp(httpApp)
      .resource
      .use(_ => IO.never)
      .as(ExitCode.Success)
}
