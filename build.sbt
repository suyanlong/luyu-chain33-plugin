import Dependencies._
import Version._
import sbt.Keys._

ThisBuild / scalaVersion     := "2.12.8"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.chain33"
ThisBuild / organizationName := "luyu-chain33"

resolvers += "Sonatype Releases" at "https://oss.sonatype.org/content/repositories/releases/"
resolvers += "Sonatype s01 Releases" at "https://s01.oss.sonatype.org/content/repositories/snapshots/"
resolvers += "aliyun public Releases"   at "https://maven.aliyun.com/nexus/content/groups/public/"
resolvers += "aliyun jcenter Releases"   at "https://maven.aliyun.com/nexus/content/repositories/jcenter"
resolvers += Resolver.url("bintray-sbt-plugins", url("https://dl.bintray.com/sbt/sbt-plugin-releases"))(Resolver.ivyStylePatterns)

lazy val root = (project in file("."))
  .settings(
    name := "luyu-chain33",
    libraryDependencies ++= Seq(
      scalaTest % Test,
      utilCore,
      "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
      "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
      "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,

      "link.luyu" % "luyu-cross-chain-protocol" % "1.0.0",
      "link.luyu" % "luyu-java-sdk" % "1.0.0",
      "link.luyu" % "luyu-toolkit" % "1.0.0"
    ))