package com.github.plippe.funnel.server.http4s

import cats.implicits._
import cats.effect.Sync
import io.circe.{Encoder, Json}
import io.circe.syntax._
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl.io._

object CrudRoutes {

  private def response[F[_]: Sync, T](f: F[T], status: Status)(
      implicit encoder: Encoder[T],
      entityEncoder: EntityEncoder[F, Json]) =
    f.flatMap { result =>
      Response()
        .withStatus(status)
        .withEntity(result.asJson)
        .pure[F]
    }

  def readAllResponse[F[_]: Sync, Model](f: () => F[List[Model]])(
      implicit encoder: Encoder[Model]) =
    response(f(), Status.Ok)

  def createResponse[F[_]: Sync, Model, Form](f: Form => F[Model],
                                              req: Request[F])(
      implicit entityDecoder: EntityDecoder[F, Form],
      encoder: Encoder[Model]) =
    req
      .as[Form]
      .flatMap(form => response(f(form), Status.Created))

  def readOneResponse[F[_]: Sync, Model](f: String => F[Model], id: String)(
      implicit encoder: Encoder[Model]) =
    response(f(id), Status.Ok)

  def updateResponse[F[_]: Sync, Model, Form](f: (String, Form) => F[Model],
                                              req: Request[F],
                                              id: String)(
      implicit entityDecoder: EntityDecoder[F, Form],
      encoder: Encoder[Model]) =
    req
      .as[Form]
      .flatMap(form => response(f(id, form), Status.Ok))

  def deleteResponse[F[_]: Sync](f: String => F[Unit], id: String) =
    response(f(id), Status.NoContent)

  def apply[F[_]: Sync, Model, Form](readAll: () => F[List[Model]],
                                     create: Form => F[Model],
                                     readOne: String => F[Model],
                                     update: (String, Form) => F[Model],
                                     delete: String => F[Unit])(
      implicit entityDecoder: EntityDecoder[F, Form],
      encoder: Encoder[Model]) =
    HttpRoutes.of[F] {
      case GET -> Root              => readAllResponse(readAll)
      case req @ POST -> Root       => createResponse(create, req)
      case GET -> Root / id         => readOneResponse(readOne, id)
      case req @ PATCH -> Root / id => updateResponse(update, req, id)
      case req @ PUT -> Root / id   => updateResponse(update, req, id)
      case DELETE -> Root / id      => deleteResponse(delete, id)
    }

}
