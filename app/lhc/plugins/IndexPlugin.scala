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
  //private var indexer: Option[SolrIndexer] = None
  var indexer: Option[SolrIndexer] = None

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

import org.apache.solr.client.solrj.SolrQuery
import lhc.util.DefaultLhcLogger
import scala.collection.JavaConverters._
import org.apache.solr.client.solrj.response.FacetField
import scala.util.control.NonFatal
object IndexPlugin extends DefaultLhcLogger {
  def getGroups(app: Application): Set[String] = {
    val id = app.plugin[IndexPlugin].get.indexer.get.solrInstance
    val sq = new SolrQuery().setQuery("*:*").setFacet(true).addFacetField("group")
                  .setRows(0)
    try {
      val rsp = id.query(sq)
      println(rsp.getFacetQuery())
      val field = rsp.getFacetField("group")
      if (field == null) {
        return Set()
      }
      val fields = field.getValues
      if (fields == null || fields.size == 0) {
        return Set()
      }
      fields.asScala.map { f =>
        logger.info("Group %s has %d docs".format(f.getName, f.getCount))
        f.getName
      }.toSet
    } catch {
      case NonFatal(e) =>
        logger.error("Error running query", e)
        throw e
    }
  }

  def query(app: Application) = {
    val id = app.plugin[IndexPlugin].get.indexer.get.solrInstance
    //val sq = new SolrQuery().setQuery("message:hello AND group:#test")
    val sq = new SolrQuery().setQuery("*:*").setFacet(true).addFacetField("group")
                  .setRows(0)
    try {
      val rsp = id.query(sq)
      println(rsp.getFacetFields())
      val results = rsp.getResults()
      val sResults = results.asScala
      logger.info("Found %d results".format(sResults.size))
      sResults
    } catch {
      case NonFatal(e) =>
        logger.error("Error running query", e)
        throw e
    }
  }
}
