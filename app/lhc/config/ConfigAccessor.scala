package lhc.config

import lhc.util.LhcLogger

import play.api.Configuration
import com.typesafe.config._
import java.net.URL

trait ConfigAccessor extends LhcLogger {
  protected val underlying: Configuration
  val namespace: Option[String]

  override val loggerName: String = "configuration"

  def getBoolean(path: String): Option[Boolean] = underlying.getBoolean(mkPath(path))
  def getBoolean(path: String, default: Boolean): Boolean =
    getBoolean(path).getOrElse(default)

  def getObject(path: String): Option[ConfigObject] = underlying.getObject(mkPath(path))

  def getObjectList(path: String): Option[java.util.List[_ <: ConfigObject]] =
    underlying.getObjectList(mkPath(path))

  def getString(path: String): Option[String] = underlying.getString(mkPath(path))
  def getUrl(path: String): Option[URL] = getString(path).map(u => new URL(u))

  protected[config] def validate() {
  }

  protected def mkPath(path: String): String = namespace match {
    case None => path
    case Some(ns) => "%s.%s".format(ns, path)
  }
}
