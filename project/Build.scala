import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "less-hip-chat"
  val appVersion      = "1.0-SNAPSHOT"

  val camelVersion = "2.10.3"

  val appDependencies = Seq(
    "org.pircbotx" % "pircbotx" % "1.8"
  )


  val main = play.Project(appName, appVersion, appDependencies).settings(
    resolvers += "Sonatype OSS" at "http://oss.sonatype.org/content/public"
  )

}
