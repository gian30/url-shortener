package dev.urlshortener.api.controllers

import cats.effect.IO
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.circe._
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s.headers.Location

import dev.urlshortener.api.dtos.{UrlRequest, UrlResponse}
import dev.urlshortener.services.UrlShortenerService
import dev.urlshortener.utils.HttpUtils.permanentRedirectWithCache

object UrlController {

  implicit val urlRequestDecoder: EntityDecoder[IO, UrlRequest] =
    jsonOf[IO, UrlRequest]

  def routes(service: UrlShortenerService): HttpRoutes[IO] =
    HttpRoutes.of[IO] {

      case req @ POST -> Root / "shorten" =>
        for {
          input <- req.as[UrlRequest]
          url <- service.shorten(input.url)
          res <- Ok(UrlResponse(url.shortUrl).asJson)
        } yield res

      case GET -> Root / code =>
        service.resolve(code).flatMap {
          case Some(originalUrl) =>
            Uri.fromString(originalUrl) match {
              case Right(uri) => permanentRedirectWithCache(uri)
              case Left(_)    => BadRequest("Invalid redirect URI")
            }
          case None =>
            NotFound("URL not found or expired")
        }
    }
}
