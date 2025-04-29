package dev.urlshortener.config

import cats.effect.IO
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model._
import scala.jdk.CollectionConverters._
import cats.syntax.all._

object TableInitializer {

  def ensureTableExists(config: AppConfig): IO[Unit] = {
    for {
      client <- IO(DynamoConfig.client)
      existing <- IO(client.listTables().tableNames().asScala.toList)
      tableName = config.tableName
      _ <-
        if (!existing.contains(tableName)) {
          IO {
            val request = CreateTableRequest
              .builder()
              .tableName(tableName)
              .keySchema(
                KeySchemaElement
                  .builder()
                  .attributeName("code")
                  .keyType(KeyType.HASH)
                  .build()
              )
              .attributeDefinitions(
                AttributeDefinition
                  .builder()
                  .attributeName("code")
                  .attributeType(ScalarAttributeType.S)
                  .build()
              )
              .provisionedThroughput(
                ProvisionedThroughput
                  .builder()
                  .readCapacityUnits(5)
                  .writeCapacityUnits(5)
                  .build()
              )
              .build()

            client.createTable(request)
          }.attempt.flatMap {
            case Right(_) => IO.println(s"Table '$tableName' created successfully")
            case Left(e: Throwable) =>
              IO.println(s"Error creating table '$tableName': ${e.getMessage}")
          }
        } else {
          IO.println(s"Table '$tableName' already exists")
        }
    } yield ()
  }
}
