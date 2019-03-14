package com.github.plippe.funnel.server.services.schemas

import cats.effect.Sync
import com.github.plippe.funnel.server.http4s.CrudRoutes
import io.circe.Encoder
import org.http4s.EntityDecoder

object Routes {
  def apply[F[_]: Sync](readAll: () => F[List[Model]],
                        create: Form => F[Model],
                        readOne: String => F[Model],
                        update: (String, Form) => F[Model],
                        delete: String => F[Unit])(
      implicit d: EntityDecoder[F, Form],
      e: Encoder[Model]) =
    CrudRoutes[F, Model, Form](readAll, create, readOne, update, delete)
}
