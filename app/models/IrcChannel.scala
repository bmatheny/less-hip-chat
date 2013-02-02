package models

import play.api.{Configuration, PlayException}
import scala.concurrent.duration.Duration
import scala.util.control.NonFatal

case class IrcChannel(name: String, password: Option[String], expireAfter: Option[Duration])

object IrcChannel {
  def apply(key: String, cfg: Configuration): IrcChannel = {
    val name = mkChannel(cfg.getString("name").getOrElse(key))
    val password = cfg.getString("password")
    val expireAfter = cfg.getString("expireAfter").map(mkDuration(_))
    new IrcChannel(name, password, expireAfter)
  }

  def mkChannel(chan: String): String = if (chan.startsWith("#")) {
    chan
  } else {
    "#%s".format(chan)
  }

  def mkDuration(dur: String): Duration = try {
    Duration.create(dur)
  } catch {
    case NonFatal(e) =>
      throw new PlayException("Invalid Duration", "Duration %s can not be parsed".format(dur), e)
  }
}
