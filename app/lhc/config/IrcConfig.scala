package lhc.config

import models.IrcChannel
import play.api.Configuration

import scala.collection.JavaConverters._

case class IrcConfig(override val underlying: Configuration) extends ConfigAccessor {
  override val namespace = Some("irc")

  lazy val channels: List[IrcChannel] = getObject("channels").map { obj =>
    obj.keySet.asScala.map { name =>
      val cfg = getObject("channels.%s".format(name))
                  .map(c => Configuration(c.toConfig))
                  .getOrElse(Configuration.empty)
      IrcChannel(name, cfg)
    }.toList
  }.getOrElse(List())
  val colors = getBoolean("colors", true)
  val debug = getBoolean("debug", false)
  val enabled = getBoolean("enabled", false)
  def host = getString("host").getOrElse {
    throw new IllegalArgumentException("host not defined for irc configuration")
  }
  val insecure = getBoolean("insecure", true)
  def nickname = getString("nickname").getOrElse {
    throw new IllegalArgumentException("nickname not defined for irc configuration")
  }
  val password = getString("password")
  val port = getInt("port").getOrElse {
    if (ssl) {
      6697
    } else {
      6667
    }
  }
  def realname = getString("realname").getOrElse(nickname)
  val ssl = getBoolean("ssl", false)
  def username = getString("username").getOrElse(nickname)

  override protected[config] def validate() {
    if (enabled) {
      require(nickname.size > 0, "empty irc.nickname specified")
      require(channels.size > 0, "no irc.channels configured")
      require(host.size > 0, "empty host.size specified")
      require(port > 0 && port < (1 << 16), "irc.port must be > 0 and < 2^16")
      logger.info("irc enabled, %d channels configured".format(channels.size))
    } else {
      logger.info("irc not enabled, %d channels configured".format(channels.size))
    }
  }
}
