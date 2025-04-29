package dev.urlshortener.repositories

import cats.effect.unsafe.implicits.global
import dev.urlshortener.config.AppConfig
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import java.time.Instant

class UrlRepositorySpec extends AnyFunSuite with Matchers {

  val config = AppConfig.load()
  val repo = new UrlRepository(config)

  test("save should fail when inserting a duplicate code") {
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
}
