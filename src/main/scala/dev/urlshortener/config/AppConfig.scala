package dev.urlshortener.config

case class AppConfig(
    baseUrl: String,
    defaultExpiryDays: Int,
    tableName: String,
    host: String,
    port: Int
)

object AppConfig {
  import scala.util.Properties.envOrElse

  def load(): AppConfig = AppConfig(
    baseUrl = envOrElse("BASE_URL", "http://localhost:8080/"),
    defaultExpiryDays = envOrElse("DEFAULT_EXPIRY_DAYS", "30").toInt,
    tableName = envOrElse("TABLE_NAME", "short_links"),
    host = envOrElse("HTTP_HOST", "0.0.0.0"),
    port = envOrElse("HTTP_PORT", "8080").toInt
  )
}
