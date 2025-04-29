import Dependencies._

ThisBuild / scalaVersion := scalaCurrentVersion
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "dev.urlshortener"
ThisBuild / organizationName := "URL Shortener"

lazy val root = (project in file("."))
  .settings(
    name := "url-shortener",
    libraryDependencies ++= Seq(
      catsEffect,
      http4sDsl,
      http4sServer,
      http4sEmberServer,
      http4sCirce,
      circeGeneric,
      circeParser,
      dynamoDbSdk,
      nettyNioClient,
      ip4sCore,
      logback,
      scalaTest,
      mockito
    )
  )
