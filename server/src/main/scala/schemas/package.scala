package com.github.plippe.funnel.server.schemas

import cats.effect.Sync
import io.circe.generic.auto._
import org.http4s.circe.jsonOf

object Schemas {
  def apply[F[_]: Sync] = {
    implicit val r = jsonOf[F, Form]

    Routes[F](
      () => IndexHandler[F],
      CreateHandler[F],
      ShowHandler[F],
      UpdateHandler[F],
      DestroyHandler[F],
    )
  }
}
