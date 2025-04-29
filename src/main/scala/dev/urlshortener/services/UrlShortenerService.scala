package dev.urlshortener.services

import cats.effect.IO
import dev.urlshortener.domain.models.Url
import dev.urlshortener.utils.CodeGenerator
import dev.urlshortener.repositories.UrlRepository

import java.time.Instant
import java.time.temporal.ChronoUnit
import dev.urlshortener.config.AppConfig

class UrlShortenerService(
    repository: UrlRepository,
    config: AppConfig,
    codeGen: () => String = () => CodeGenerator.generate(8)
) {

  private val defaultExpiryDays = config.defaultExpiryDays
  private val baseUrl = config.baseUrl

  def shorten(originalUrl: String): IO[Url] = {
    if (originalUrl == null || originalUrl.trim.isEmpty) {
      IO.raiseError(new IllegalArgumentException("URL cannot be empty"))
    } else if (!originalUrl.matches("^https?://.+")) {
      IO.raiseError(new IllegalArgumentException("Invalid URL format"))
    } else {
      repository.findByOriginalUrl(originalUrl).flatMap {
        case Some((code, url, expiresAt)) =>
          IO.pure(Url(code, url, s"$baseUrl$code", expiresAt))

        case None =>
          for {
            code <- generateUniqueCode()
            expiresAt = Instant.now().plus(defaultExpiryDays, ChronoUnit.DAYS)
            shortUrl = s"$baseUrl$code"
            url = Url(code, originalUrl, shortUrl, expiresAt)
            _ <- repository.save(url.code, url.originalUrl, url.expiresAt)
          } yield url
      }
    }
  }

  def resolve(code: String): IO[Option[String]] =
    repository.find(code)

  private def generateUniqueCode(retries: Int = 5): IO[String] = {
    def tryGenerate(attempt: Int): IO[String] = {
      val code = codeGen()
      repository.find(code).flatMap {
        case Some(_) if attempt < retries =>
          tryGenerate(attempt + 1)
        case Some(_) =>
          IO.raiseError(
            new RuntimeException("Too many collisions, please retry")
          )
        case None =>
          IO.pure(code)
      }
    }
    tryGenerate(0)
  }

}
