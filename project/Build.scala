import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "less-hip-chat"
  val appVersion      = "1.0-SNAPSHOT"

  val camelVersion = "2.10.3"

  val appDependencies = Seq(
    "org.apache.camel" % "camel-solr" % camelVersion,
    "org.apache.camel" % "camel-irc"  % camelVersion
  )


  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here      
  )

}
