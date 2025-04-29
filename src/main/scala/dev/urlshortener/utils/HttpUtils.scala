package dev.urlshortener.utils

import cats.effect.IO
import org.http4s._
import org.http4s.headers.Location
import org.typelevel.ci.CIString

object HttpUtils {

  def permanentRedirectWithCache(uri: Uri): IO[Response[IO]] = {
    val location = Location(uri)
    val cacheControl =
      Header.Raw(CIString("Cache-Control"), "public, max-age=31536000")

    IO.pure(
      Response[IO](
        status = Status.PermanentRedirect,
        headers = Headers(location, cacheControl)
      )
    )
  }
}
