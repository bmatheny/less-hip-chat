package lhc.config

import models.IrcChannel
import play.api.Configuration

import scala.collection.JavaConverters._

case class IrcConfig(override val underlying: Configuration) extends ConfigAccessor {
  override val namespace = Some("irc")
  val enabled = getBoolean("enabled", false)
  lazy val channels: List[IrcChannel] = getObject("channels").map { obj =>
    obj.keySet.asScala.map { name =>
      val cfg = getObject("channels.%s".format(name))
                  .map(c => Configuration(c.toConfig))
                  .getOrElse(Configuration.empty)
      IrcChannel(name, cfg)
    }.toList
  }.getOrElse(List())

  override protected[config] def validate() {
    if (enabled) {
      require(channels.size > 0, "no irc.channels configured")
      logger.info("irc enabled, %d channels configured".format(channels.size))
    } else {
      logger.info("irc not enabled, %d channels configured".format(channels.size))
    }
  }
}
