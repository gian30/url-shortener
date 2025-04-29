import sbt._

object Dependencies {
  val scalaCurrentVersion = "2.13.16"
  val catsEffect   = "org.typelevel" %% "cats-effect" % "3.5.2"
  val http4sVersion = "0.23.24"
  val http4sDsl         = "org.http4s" %% "http4s-dsl" % http4sVersion
  val http4sServer      = "org.http4s" %% "http4s-server" % http4sVersion
  val http4sEmberServer = "org.http4s" %% "http4s-ember-server" % http4sVersion
  val http4sCirce       = "org.http4s" %% "http4s-circe" % http4sVersion
  val circeVersion   = "0.14.6"
  val circeGeneric   = "io.circe" %% "circe-generic" % circeVersion
  val circeParser    = "io.circe" %% "circe-parser" % circeVersion
  val dynamoDbSdk      = "software.amazon.awssdk" % "dynamodb" % "2.20.0"
  val nettyNioClient   = "software.amazon.awssdk" % "netty-nio-client" % "2.20.34"
  val ip4sCore         = "com.comcast" %% "ip4s-core" % "3.3.0"
  val logback          = "ch.qos.logback" % "logback-classic" % "1.4.14"
  val scalaTest        = "org.scalatest" %% "scalatest" % "3.2.18" % Test
  val mockito          = "org.scalatestplus" %% "mockito-4-11" % "3.2.18.0" % Test
}
