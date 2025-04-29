package dev.urlshortener

import cats.effect._
import org.http4s.ember.server._
import org.http4s.implicits._
import dev.urlshortener.api.routes.HttpRoutesProvider
import dev.urlshortener.config.TableInitializer
import dev.urlshortener.config.AppConfig
import com.comcast.ip4s.Host
import com.comcast.ip4s.Port

object Main extends IOApp {
  val config = AppConfig.load()

  override def run(args: List[String]): IO[ExitCode] =
    TableInitializer.ensureTableExists(config) *>
      EmberServerBuilder
        .default[IO]
        .withHost(Host.fromString("0.0.0.0").get)
        .withPort(Port.fromString("8080").get)
        .withHttpApp(HttpRoutesProvider.routes.orNotFound)
        .build
        .use(_ => IO.never)
        .as(ExitCode.Success)
}
