import Dependencies._

ThisBuild / scalaVersion := "2.13.16"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "dev.urlshortener"
ThisBuild / organizationName := "URL Shortener"

lazy val root = (project in file("."))
  .settings(
    name := "url-shortener",
    libraryDependencies ++= Seq(
      "org.http4s" %% "http4s-dsl" % "0.23.24",
      "org.http4s" %% "http4s-ember-server" % "0.23.24",
      "org.http4s" %% "http4s-circe" % "0.23.24",
      "io.circe" %% "circe-generic" % "0.14.6",
      "io.circe" %% "circe-parser" % "0.14.6",
      "org.typelevel" %% "cats-effect" % "3.5.2",
      "software.amazon.awssdk" % "dynamodb" % "2.20.0",
      "software.amazon.awssdk" % "netty-nio-client" % "2.20.34",
      "com.comcast" %% "ip4s-core" % "3.3.0",
      "org.http4s" %% "http4s-server" % "0.23.24",
      "ch.qos.logback" % "logback-classic" % "1.4.14",
      "org.scalatest" %% "scalatest" % "3.2.18" % Test,
      "org.scalatestplus" %% "mockito-4-11" % "3.2.18.0" % Test
    )
  )
