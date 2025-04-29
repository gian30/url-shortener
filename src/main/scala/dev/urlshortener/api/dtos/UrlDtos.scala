package dev.urlshortener.api.dtos

import io.circe.generic.auto._

case class UrlRequest(url: String)
case class UrlResponse(shortUrl: String)
