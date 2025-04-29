package dev.urlshortener.services

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.mockito.Mockito._
import org.mockito.ArgumentMatchers._
import org.scalatestplus.mockito.MockitoSugar

import dev.urlshortener.repositories.UrlRepository
import dev.urlshortener.domain.models.Url
import dev.urlshortener.config.AppConfig

import cats.effect.IO
import cats.effect.unsafe.implicits.global

import java.time.Instant

class UrlShortenerServiceSpec
    extends AnyFlatSpec
    with Matchers
    with MockitoSugar {

  val testConfig = AppConfig.load()

  "UrlShortenerService" should "generate a new short URL if not already present" in {
    val mockRepo = mock[UrlRepository]
    val originalUrl = "https://www.google.com"
    val expiresAt =
      Instant.now().plusSeconds(testConfig.defaultExpiryDays * 86400)
    when(mockRepo.findByOriginalUrl(originalUrl)).thenReturn(IO.pure(None))
    when(mockRepo.find(any[String])).thenReturn(IO.pure(None))
    when(mockRepo.save(any[String], any[String], any[Instant]))
      .thenReturn(IO.unit)

    val service = new UrlShortenerService(mockRepo, testConfig)
    val result = service.shorten(originalUrl).unsafeRunSync()
    result.originalUrl shouldEqual originalUrl
    result.shortUrl should startWith(testConfig.baseUrl)
  }

  it should "return the same short URL for the same long URL if already present" in {
    val mockRepo = mock[UrlRepository]
    val originalUrl = "https://www.google.com/test"
    val code = "abc123"
    val expiresAt = Instant.now().plusSeconds(86400)
    when(mockRepo.findByOriginalUrl(originalUrl))
      .thenReturn(IO.pure(Some((code, originalUrl, expiresAt))))
    val service = new UrlShortenerService(mockRepo, testConfig)
    val result = service.shorten(originalUrl).unsafeRunSync()
    result.code shouldEqual code
    result.originalUrl shouldEqual originalUrl
    result.shortUrl shouldEqual s"${testConfig.baseUrl}$code"
  }

  it should "return None when resolving a non-existent short code" in {
    val mockRepo = mock[UrlRepository]
    val code = "notfound"
    when(mockRepo.find(code)).thenReturn(IO.pure(None))
    val service = new UrlShortenerService(mockRepo, testConfig)
    val result = service.resolve(code).unsafeRunSync()
    result shouldBe None
  }

  it should "return the original URL when resolving a valid short code" in {
    val mockRepo = mock[UrlRepository]
    val code = "xyz789"
    val originalUrl = "https://www.google.com/original"
    when(mockRepo.find(code)).thenReturn(IO.pure(Some(originalUrl)))
    val service = new UrlShortenerService(mockRepo, testConfig)
    val result = service.resolve(code).unsafeRunSync()
    result shouldEqual Some(originalUrl)
  }

  it should "reject empty URL" in {
    val mockRepo = mock[UrlRepository]
    val service = new UrlShortenerService(mockRepo, testConfig)
    an[IllegalArgumentException] should be thrownBy {
      service.shorten("").unsafeRunSync()
    }
  }

  it should "reject invalid URL format" in {
    val mockRepo = mock[UrlRepository]
    val service = new UrlShortenerService(mockRepo, testConfig)
    an[IllegalArgumentException] should be thrownBy {
      service.shorten("not-a-url").unsafeRunSync()
    }
  }

  it should "retry on code collision and generate a unique short code" in {
    val mockRepo = mock[UrlRepository]
    val originalUrl = "https://example.com"
    val existingCode = "abc123"
    val newCode = "xyz456"
    when(mockRepo.findByOriginalUrl(originalUrl))
      .thenReturn(IO.pure(None))
    when(mockRepo.find(existingCode))
      .thenReturn(IO.pure(Some("https://another.com")))
    when(mockRepo.find(newCode)).thenReturn(IO.pure(None))
    when(mockRepo.save(any[String], any[String], any[Instant]))
      .thenReturn(IO.unit)
    val codeAttempts = Iterator(existingCode, newCode)
    val service =
      new UrlShortenerService(mockRepo, testConfig, () => codeAttempts.next())
    val result = service.shorten(originalUrl).unsafeRunSync()
    result.code shouldEqual newCode
  }
}
