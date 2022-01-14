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

assemblyMergeStrategy in assembly := {
  case PathList("javax", "servlet", xs @ _*) => MergeStrategy.last
  case PathList("javax", "activation", xs @ _*) => MergeStrategy.last
  case PathList("org", "apache", xs @ _*) => MergeStrategy.last
  case PathList("com", "google", xs @ _*) => MergeStrategy.last
  case PathList("io", "netty", xs @ _*) => MergeStrategy.last
  case PathList("com", "esotericsoftware", xs @ _*) => MergeStrategy.last
  case PathList("com", "codahale", xs @ _*) => MergeStrategy.last
  case PathList("com", "yammer", xs @ _*) => MergeStrategy.last
  case "about.html" => MergeStrategy.rename
  case "META-INF/ECLIPSEF.RSA" => MergeStrategy.last
  case "META-INF/mailcap" => MergeStrategy.last
  case "META-INF/org/apache/logging/log4j/core/config/plugins/Log4j2Plugins.dat" => MergeStrategy.last
  case "META-INF/spring-configuration-metadata.json" => MergeStrategy.last
  case "META-INF/mimetypes.default" => MergeStrategy.last
  case "plugin.properties" => MergeStrategy.last
  case "module-info.class" => MergeStrategy.last
  case "META-INF/io.netty.versions.properties" => MergeStrategy.last
  case "META-INF/additional-spring-configuration-metadata.json" => MergeStrategy.last
  case "META-INF/spring.factories" => MergeStrategy.last
  case "google/protobuf/descriptor.proto" => MergeStrategy.last
  case "google/protobuf/compiler/plugin.proto" => MergeStrategy.last
  case "google/protobuf/field_mask.proto" => MergeStrategy.last
  case "google/protobuf/any.proto" => MergeStrategy.last
  case "google/protobuf/duration.proto" => MergeStrategy.last
  case "google/protobuf/struct.proto" => MergeStrategy.last
  case "google/protobuf/timestamp.proto" => MergeStrategy.last
  case "google/protobuf/type.proto" => MergeStrategy.last
  case "google/protobuf/wrappers.proto" => MergeStrategy.last
  case "META-INF/web-fragment.xml" => MergeStrategy.last
  case "log4j.properties" => MergeStrategy.last
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}

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
      "link.luyu" % "luyu-toolkit" % "1.0.0",
      "com.citahub.cita" % "core" % "20.2.0",
      "org.apache.commons" % "commons-lang3" % "3.11",
      "org.apache.commons" % "commons-collections4" % "4.4",
      "org.reflections" % "reflections" % "0.9.11",
      "com.alibaba" % "fastjson" % "1.2.47"
    ))
