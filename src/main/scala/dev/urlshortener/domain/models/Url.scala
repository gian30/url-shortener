package dev.urlshortener.domain.models

import java.time.Instant
import java.time.temporal.ChronoUnit

case class Url(
  code: String,
  originalUrl: String,
  shortUrl: String,
  expiresAt: Instant
) {
  def isExpired: Boolean = expiresAt.isBefore(Instant.now())
  def timeToLive: Long = Instant.now().until(expiresAt, ChronoUnit.SECONDS)
}