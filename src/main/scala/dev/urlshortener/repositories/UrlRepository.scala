package dev.urlshortener.repositories

import cats.effect.IO
import dev.urlshortener.config.DynamoConfig
import software.amazon.awssdk.services.dynamodb.model._
import java.time.Instant
import scala.jdk.CollectionConverters._
import dev.urlshortener.config.AppConfig

class UrlRepository(config: AppConfig) {
  private val table = config.tableName
  private def toRecord(
      item: java.util.Map[String, AttributeValue]
  ): (String, String, Instant) = (
    item.get("code").s(),
    item.get("url").s(),
    Instant.parse(item.get("expires_at").s())
  )
  def save(code: String, url: String, expiresAt: Instant): IO[Unit] = IO {
    val item = Map(
      "code" -> AttributeValue.builder().s(code).build(),
      "url" -> AttributeValue.builder().s(url).build(),
      "expires_at" -> AttributeValue.builder().s(expiresAt.toString).build()
    )
    val req =
      PutItemRequest.builder().tableName(table).item(item.asJava).build()
    DynamoConfig.client.putItem(req)
  }

  def find(code: String): IO[Option[String]] = IO {
    val key = Map("code" -> AttributeValue.builder().s(code).build())
    val req = GetItemRequest.builder().tableName(table).key(key.asJava).build()
    val result = DynamoConfig.client.getItem(req)

    if (result.hasItem) {
      val item = result.item()
      val expires = Instant.parse(item.get("expires_at").s())
      if (expires.isAfter(Instant.now())) Some(item.get("url").s())
      else None
    } else None
  }

  def findByOriginalUrl(
      originalUrl: String
  ): IO[Option[(String, String, Instant)]] =
    IO.blocking {
      val scan = ScanRequest
        .builder()
        .tableName(table)
        .filterExpression("#u = :u")
        .expressionAttributeNames(
          Map("#u" -> "url").asJava
        )
        .expressionAttributeValues(
          Map(":u" -> AttributeValue.builder().s(originalUrl).build()).asJava
        )
        .build()

      DynamoConfig.client.scan(scan).items().asScala.headOption.map(toRecord)
    }
}
