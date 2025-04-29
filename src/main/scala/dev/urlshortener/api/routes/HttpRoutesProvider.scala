// src/main/scala/dev/urlshortener/api/routes/HttpRoutesProvider.scala
package dev.urlshortener.api.routes

import cats.effect.IO
import org.http4s.HttpRoutes
import dev.urlshortener.api.controllers.UrlController
import dev.urlshortener.config.AppConfig
import dev.urlshortener.repositories.UrlRepository
import dev.urlshortener.services.UrlShortenerService

object HttpRoutesProvider {
  private val config = AppConfig.load()
  private val repository = new UrlRepository(config)
  private val service = new UrlShortenerService(repository, config)

  val routes: HttpRoutes[IO] =
    UrlController.routes(service)
}
