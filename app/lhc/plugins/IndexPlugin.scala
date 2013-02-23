package lhc.plugins

import lhc.config.AppConfig
import lhc.consumers.IrcConsumer
import lhc.indexers.{Indexer, SolrIndexer}
import lhc.util.{DefaultLhcLogger, LhcLogger}
import models.{Group, Message, Page, PageParams}
import play.api.{Application, Plugin}
import org.pircbotx.PircBotX

class IndexPlugin(app: Application) extends Plugin with LhcLogger {
  override val loggerName: String = "index-plugin"

  private var consumer: Option[IrcConsumer] = None
  private var indexer: Option[Indexer] = None

  override def onStart() {
    logger.info("Starting IndexPlugin")
    if (consumer.isDefined)
      consumer.foreach(_.shutdown)
    if (indexer.isDefined)
      indexer.foreach(_.shutdown)
    val idx = new SolrIndexer(AppConfig.solr, app)
    indexer = Some(idx)
    if (AppConfig.irc.enabled) consumer = Some(new IrcConsumer(AppConfig.irc, idx))
  }

  def find(query: String, pageParms: PageParams): Page[Message] = {
    indexer.map(_.find(query,pageParms)).getOrElse(Page.empty[Message])
  }
  def getGroups(): Set[Group] = {
    indexer.map(_.getGroups).getOrElse(Set[Group]())
  }
  def getRecent(rows: Int = 10): Seq[Message] = {
    indexer.map(_.getRecent(rows)).getOrElse(Seq())
  }

  override def onStop() {
    logger.info("Shutting down IndexPlugin")
    consumer.foreach(_.shutdown)
    indexer.foreach(_.shutdown)
  }
}

object IndexPlugin extends DefaultLhcLogger {

  def withPlugin[T](app: Application, default: T)(f: Indexer => T): T = {
    app.plugin[IndexPlugin].flatMap(_.indexer).map(p => f(p)).getOrElse {
      logger.error("IndexPlugin not configured")
      default
    }
  }

  def find(app: Application, query: String, pageParms: PageParams): Page[Message] = {
    withPlugin[Page[Message]](app, Page.empty[Message]) { indexer =>
      indexer.find(query,pageParms)
    }
  }

  def getGroups(app: Application): Set[Group] = {
    withPlugin[Set[Group]](app, Set[Group]()) { indexer =>
      indexer.getGroups
    }
  }

  def getRecent(app: Application, rows: Int = 10): Seq[Message] = {
    withPlugin[Seq[Message]](app, Seq()) { indexer =>
      indexer.getRecent(rows)
    }
  }

}
