import Dependencies._

ThisBuild / scalaVersion     := "2.12.8"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.chain33"
ThisBuild / organizationName := "luju-chain33"

val AkkaVersion = "2.6.8"
val AkkaHttpVersion = "10.2.7"

lazy val root = (project in file("."))
  .settings(
  // set the name of the project
    name := "luyu-chain33",
    // set the main Scala source directory to be <base>/src
//    Compile / scalaSource := baseDirectory.value / "src",

    // set the Scala test source directory to be <base>/test
//    Test / scalaSource := baseDirectory.value / "test",

    // add a test dependency on ScalaCheck
//    libraryDependencies += scalacheck % Test,
    libraryDependencies ++= Seq(
      scalaTest % Test,
      utilCore,
      "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
      "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
      "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion
    ))