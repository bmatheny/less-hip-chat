package lhc.consumers

import lhc.config.{AppConfig, IrcConfig}
import lhc.indexers.Indexer
import lhc.messages.IrcMessage
import lhc.util.LhcLogger

import java.util.UUID
import scala.util.control.NonFatal

import org.pircbotx.{Colors, PircBotX, UtilSSLSocketFactory}
import org.pircbotx.hooks.ListenerAdapter
import org.pircbotx.hooks.events._

class IrcConsumer(val cfg: IrcConfig, val indexer: Indexer) extends ListenerAdapter[PircBotX] with LhcLogger {

  override val loggerName = "irc"

  private var bot = IrcConsumer.createBot(this)
  @volatile private var inShutdown = false

  /* Join channels when we connect to the server */
  override def onConnect(event: ConnectEvent[PircBotX]) {
    loggerName.synchronized {
      bot = event.getBot
    }
    logger.info("onConnect called for %s:%d".format(bot.getServer, bot.getPort))
    cfg.channels.foreach { channel =>
      logger.info("Joining channel %s, password %s".format(
        channel.name, channel.password.map(_ => "*****").getOrElse("unspecified")
      ))
      if (channel.password.isDefined) safely(_.joinChannel(channel.name, channel.password.get))
      else safely(_.joinChannel(channel.name))
    }
  }

  /* Reconnect to the server if we are disconnected */
  override def onDisconnect(event: DisconnectEvent[PircBotX]) {
    if (inShutdown) {
      logger.info("Shutting down, disconnect event received")
      return
    }
    logger.warn("Disconnected from %s:%d, attempting reconnect".format(
      event.getBot.getServer, event.getBot.getPort)
    )
    Thread.sleep(5 * 1000)
    Range(1, 15).find { attempt =>
      if (event.getBot.isConnected) {
        true
      } else {
        tryReconnect(attempt, event)
      }
    }
  }

  /* Handle seeing a message in a channel */
  override def onMessage(event: MessageEvent[PircBotX]) {
    val channel = event.getChannel
    val user = event.getUser
    val msg = event.getMessage
    val userMap = Map(
      "login" -> user.getLogin, "nick" -> user.getNick, "realName" -> user.getRealName
    )
    val uuid = UUID.randomUUID().toString()
    val ircMessage = IrcMessage(uuid, event.getTimestamp, channel.getName, userMap, msg)
    logger.debug("Indexing message: %s".format(ircMessage))
    indexer.index(ircMessage)
  }

  /* Handle a private message */
  override def onPrivateMessage(event: PrivateMessageEvent[PircBotX]) {
    val username = event.getUser.getNick()
    val msg = event.getMessage
    logger.info("Received private message from %s: %s".format(username, msg))
    event.respond("AH AH AH. You didn't say the magic word.")
  }

  def shutdown() {
    inShutdown = true
    // FIXME this throws an exception for some reason :/
    safely(_.shutdown(true))
  }

  protected def tryReconnect(attempt: Int, event: DisconnectEvent[PircBotX]): Boolean = {
    try {
      event.getBot.reconnect()
      logger.info("Successfully reconnected on attempt %d".format(attempt))
      true
    } catch {
      case NonFatal(e) =>
        logger.warn("Failed to reconnect on attempt %d, retrying after %s".format(
          attempt, e.getMessage
        ))
        Thread.sleep(10 * 1000)
        false
    }
  }
  protected def safely[T](f: PircBotX => T): Option[T] = {
    try {
      Option(f(bot))
    } catch {
      case NonFatal(e) =>
        logger.warn("IRC operation failed: %s".format(e.getMessage), e)
        None
    }
  }
}

object IrcConsumer {
  val BOT_VERSION = "less-hip-chat 0.1"
  def createBot(i: IrcConsumer) = {
    val bot = configureBot(i)
    val cfg = i.cfg
    val trustManager = new UtilSSLSocketFactory().trustAllCertificates()
    if (cfg.password.isDefined && cfg.ssl) {
      bot.connect(cfg.host, cfg.port, cfg.password.get, trustManager)
    } else if (cfg.ssl) {
      bot.connect(cfg.host, cfg.port, trustManager)
    } else if (cfg.password.isDefined) {
      bot.connect(cfg.host, cfg.port, cfg.password.get)
    } else {
      bot.connect(cfg.host, cfg.port)
    }
    bot
  }
  def configureBot(i: IrcConsumer) = {
    val bot = new PircBotX
    val cfg = i.cfg
    bot.getListenerManager.addListener(i)
    bot.setName(cfg.nickname)
    bot.setLogin(cfg.username)
    bot.setAutoNickChange(true);
    bot.setVersion(BOT_VERSION)
    bot.setVerbose(cfg.debug)
    bot
  }
}
