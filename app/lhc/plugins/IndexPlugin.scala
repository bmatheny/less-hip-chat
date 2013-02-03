package lhc.plugins

import lhc.config.AppConfig
import lhc.consumers.IrcConsumer
import lhc.indexers.SolrIndexer
import lhc.util.LhcLogger
import play.api.{Application, Plugin}
import org.pircbotx.PircBotX

class IndexPlugin(app: Application) extends Plugin with LhcLogger {
  override val loggerName: String = "index-plugin"

  private var consumer: Option[IrcConsumer] = None
  private var indexer: Option[SolrIndexer] = None

  override def onStart() {
    logger.info("Starting IndexPlugin")
    if (consumer.isDefined)
      consumer.foreach(_.shutdown)
    if (indexer.isDefined)
      indexer.foreach(_.shutdown)
    val idx = new SolrIndexer(AppConfig.solr, app)
    indexer = Some(idx)
    consumer = Some(new IrcConsumer(AppConfig.irc, idx))
  }

  override def onStop() {
    logger.info("Shutting down IndexPlugin")
    consumer.foreach(_.shutdown)
    indexer.foreach(_.shutdown)
  }
}
