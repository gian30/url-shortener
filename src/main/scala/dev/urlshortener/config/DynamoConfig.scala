package dev.urlshortener.config

import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.auth.credentials.{AwsBasicCredentials, StaticCredentialsProvider}
import software.amazon.awssdk.regions.Region

import java.net.URI
import scala.util.Properties

object DynamoConfig {
  private val region    = Properties.envOrElse("AWS_REGION", "us-east-1")
  private val endpoint  = Properties.envOrElse("DYNAMODB_ENDPOINT", "http://localhost:8000")
  private val accessKey = Properties.envOrElse("AWS_ACCESS_KEY_ID", "dummy")
  private val secretKey = Properties.envOrElse("AWS_SECRET_ACCESS_KEY", "dummy")

  private val baseBuilder = DynamoDbClient.builder()
    .region(Region.of(region))
    .credentialsProvider(
      StaticCredentialsProvider.create(
        AwsBasicCredentials.create(accessKey, secretKey)
      )
    )

  private val builder =
    if (endpoint.startsWith("http://localhost"))
      baseBuilder.endpointOverride(URI.create(endpoint))
    else
      baseBuilder

  val client: DynamoDbClient = builder.build()
}
