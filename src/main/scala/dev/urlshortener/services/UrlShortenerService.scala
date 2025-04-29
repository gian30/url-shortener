package dev.urlshortener.services

import cats.effect.IO
import dev.urlshortener.domain.model.Url
import dev.urlshortener.utils.CodeGenerator
import dev.urlshortener.repositories.UrlRepository

import java.time.Instant
import java.time.temporal.ChronoUnit
import dev.urlshortener.config.AppConfig

class UrlShortenerService(
  repository: UrlRepository,
  config: AppConfig
) {

  private val defaultExpiryDays = config.defaultExpiryDays
  private val baseUrl = config.baseUrl

  def shorten(originalUrl: String): IO[Url] =
    repository.findByOriginalUrl(originalUrl).flatMap {
      case Some((code, url, expiresAt)) =>
        IO.pure(Url(code, url, s"$baseUrl$code", expiresAt))
      case None =>
        val code = CodeGenerator.generate(8)
        val expiresAt = Instant.now().plus(defaultExpiryDays, ChronoUnit.DAYS)
        val url = Url(code, originalUrl, s"$baseUrl$code", expiresAt)
        repository.save(url.code, url.originalUrl, url.expiresAt).map(_ => url)
    }

  def resolve(code: String): IO[Option[String]] =
    repository.find(code)
}
