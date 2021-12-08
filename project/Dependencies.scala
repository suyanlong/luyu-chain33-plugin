import sbt._

object Dependencies {
  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.0.5"
  lazy val utilCore  = "com.twitter" %% "util-core" % "21.11.0"
  lazy val akka      = "com.typesafe.akka" %% "akka-http" % "10.1.5"
}
