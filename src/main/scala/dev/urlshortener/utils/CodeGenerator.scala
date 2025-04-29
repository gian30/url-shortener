package dev.urlshortener.utils

import scala.util.Random

object CodeGenerator {
  private val alphabet =
    "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"

  def generate(length: Int): String =
    Random.alphanumeric.filter(alphabet.contains(_)).take(length).mkString
}
