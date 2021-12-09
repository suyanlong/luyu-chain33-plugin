import sbt._

object Dependencies {
  val AkkaVersion = "2.6.8"
  val AkkaHttpVersion = "10.2.7"
  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.0.5"
  lazy val utilCore  = "com.twitter" %% "util-core" % "21.11.0"
  lazy val akka      = "com.typesafe.akka" %% "akka-http" % "10.1.5"
//  lazy val chain33_sdk_java = RootProject(uri("https://github.com/33cn/chain33-sdk-java.git#1.0.15"))
}
