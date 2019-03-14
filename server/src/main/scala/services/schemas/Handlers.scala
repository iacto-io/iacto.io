package com.github.plippe.funnel.server.services.schemas

import cats.implicits._
import cats.effect.Sync

object IndexHandler {
  def apply[F[_]: Sync](): F[List[Model]] =
    List(
      Model("1", "index", 1),
      Model("2", "index", 2),
      Model("3", "index", 3)
    ).pure[F]
}

object CreateHandler {
  def apply[F[_]: Sync](form: Form): F[Model] = {
    locally(form)
    Model("4", "create", 4).pure[F]
  }
}

object ShowHandler {
  def apply[F[_]: Sync](id: String): F[Model] =
    Model(id, "show", 5).pure[F]
}

object UpdateHandler {
  def apply[F[_]: Sync](id: String, form: Form): F[Model] = {
    locally(form)
    Model(id, "update", 5).pure[F]
  }
}

object DestroyHandler {
  def apply[F[_]: Sync](id: String): F[Unit] = {
    locally(id)
    ().pure[F]
  }
}
