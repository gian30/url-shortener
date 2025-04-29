package dev.urlshortener.repositories

import cats.effect.unsafe.implicits.global
import dev.urlshortener.config.AppConfig
import org.scalatest.matchers.should.Matchers
import java.time.Instant
import org.scalatest.flatspec.AnyFlatSpec

class UrlRepositorySpec extends AnyFlatSpec with Matchers {

  val config = AppConfig.load()
  val repo = new UrlRepository(config)

  "UrlRepository" should "fail when inserting a duplicate code" in {
    val code = "dupli000"
    val url = "https://example.com"
    val expiresAt = Instant.now().plusSeconds(3600)
    repo.delete(code).unsafeRunSync()
    repo.save(code, url, expiresAt).unsafeRunSync()
    val result = scala.util.Try(repo.save(code, url, expiresAt).unsafeRunSync())
    result.isFailure shouldBe true
    result.failed.get.getClass.getSimpleName should include(
      "ConditionalCheckFailedException"
    )
    repo.delete(code).unsafeRunSync()
  }

  it should "treat code lookups as case-sensitive" in {
    val codeLower = "casecode"
    val codeUpper = "CASECODE"
    val urlLower = "https://lowercase.com"
    val urlUpper = "https://uppercase.com"
    val expiresAt = Instant.now().plusSeconds(3600)
    repo.delete(codeLower).unsafeRunSync()
    repo.delete(codeUpper).unsafeRunSync()
    repo.save(codeLower, urlLower, expiresAt).unsafeRunSync()
    repo.save(codeUpper, urlUpper, expiresAt).unsafeRunSync()
    val resultLower = repo.find(codeLower).unsafeRunSync()
    val resultUpper = repo.find(codeUpper).unsafeRunSync()
    resultLower shouldEqual Some(urlLower)
    resultUpper shouldEqual Some(urlUpper)
    repo.delete(codeLower).unsafeRunSync()
    repo.delete(codeUpper).unsafeRunSync()
  }
}
