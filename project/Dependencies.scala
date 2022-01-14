import sbt._

object Dependencies {
  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.0.5"
  lazy val utilCore  = "com.twitter" %% "util-core" % "21.11.0"
  lazy val akka      = "com.typesafe.akka" %% "akka-http" % "10.1.5"
}

object Version {
  lazy val AkkaVersion = "2.6.8"
  lazy val AkkaHttpVersion = "10.2.7"
}

object V {
  val depProject ="master"
  // Other library versions
}

object Projects {
  lazy val depProject = RootProject(uri("https://github.com/33cn/chain33-sdk-java.git#%s".format(V.depProject)))
}
