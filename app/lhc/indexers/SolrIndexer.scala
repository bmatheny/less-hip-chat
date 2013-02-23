package lhc.indexers

import lhc.config.SolrConfig
import lhc.indexers.solr._
import lhc.util.DefaultLhcLogger
import models.{Group, Message, SortDirection}
import play.api.Application
import play.api.libs.concurrent.Akka
import play.api.libs.concurrent.Execution.Implicits._
import scala.concurrent.duration._
import scala.language.postfixOps // for seconds
import scala.util.control.NonFatal
import java.util.concurrent.{ArrayBlockingQueue, TimeUnit}
import org.apache.solr.common.{SolrDocumentList, SolrInputDocument}
import org.apache.solr.client.solrj.SolrQuery
import org.apache.solr.client.solrj.response.QueryResponse
import scala.collection.JavaConverters._

class SolrIndexer(val cfg: SolrConfig, val app: Application) extends Indexer with DefaultLhcLogger {

  protected val solrInstance = InstanceManager.get(cfg)
  protected val queue = new ArrayBlockingQueue[SolrInputDocument](10000)

  Akka.system(app).scheduler.schedule(
    10 seconds,
    10 seconds,
    new Runnable() {
      override def run() {
        drainQueue()
      }
    }
  )

  override def find(
    query: String, rows: Int = 10, start: Int = 10, sort: SortDirection = SortDirection.Desc
  ): Seq[Message] = {
    val sq = SolrIndexer.findQuery(query, rows, start, sort)
    withQueryResponse(sq) { response =>
      response.getResults.asScala.map { res =>
        DocumentManager.convert(res)
      }
    }.getOrElse(Seq())
  }

  override def getGroups(): Set[Group] = {
    val sq = SolrIndexer.groupsQuery()
    withQueryResponse(sq) { rsp =>
      Option(rsp.getFacetField("group")).map(_.getValues)
        .filter(l => l != null && l.size > 0) // getValues returns null on no results
        .map { fields =>
          fields.asScala.map { f =>
            Group(f.getName, f.getCount)
          }.toSet[Group]
        }.getOrElse(Set[Group]())
    }.getOrElse(Set[Group]())
  }

  override def getRecent(rows: Int = 10): Seq[Message] = {
    val query = SolrIndexer.recentQuery(rows)
    withQueryResponse(query) { response =>
      response.getResults.asScala.map { res =>
        DocumentManager.convert(res)
      }
    }.getOrElse(Seq())
  }

  override def index(message: Message) {
    val doc = DocumentManager.convert(message)
    if (!queue.offer(doc)) {
      logger.warn("Failed to add message to queue")
    } else {
      logger.debug("Added message to queue")
    }
  }

  override def shutdown() {
    logger.info("Shutting down solr indexer")
    drainQueue()
    InstanceManager.shutdown(solrInstance)
  }

  protected def drainQueue() {
    val docs = DocumentManager.drain(queue)
    val dsize = docs.size
    if (dsize < 1) return
    logger.info("Indexing %d documents".format(dsize))
    try {
      solrInstance.add(docs)
      solrInstance.commit
    } catch {
      case NonFatal(e) =>
        logger.error("Exception draining queue, lost %d messages".format(dsize), e)
    }
  }

  protected def withQueryResponse[T](query: SolrQuery)(f: QueryResponse => T): Option[T] = {
    try {
      Option(f(solrInstance.query(query)))
    } catch {
      case NonFatal(e) =>
        logger.error("Error running query", e)
        None
    }
  }

}

object SolrIndexer {

  def findQuery(q: String, rows: Int, start: Int, sort: SortDirection): SolrQuery = {
    val ssort = sort match {
      case SortDirection.Desc => SolrQuery.ORDER.desc
      case SortDirection.Asc  => SolrQuery.ORDER.asc
    }
    val query = if (q.contains(":")) q else "message:%s".format(q)
    new SolrQuery()
          .setQuery(query)
          .addSortField("timestamp", ssort)
          .setRows(rows)
          .setStart(start)
  }

  def groupsQuery(): SolrQuery = {
    new SolrQuery()
          .setQuery("*:*")
          .setFacet(true)
          .addFacetField("group")
          .setRows(0)
  }

  def recentQuery(rows: Int): SolrQuery = {
    new SolrQuery()
          .setQuery("*:*")
          .addSortField("timestamp", SolrQuery.ORDER.desc)
          .setRows(rows)
  }

  def resultsToMessages(res: SolrDocumentList): Seq[Message] = {
    Seq()
  }
}
