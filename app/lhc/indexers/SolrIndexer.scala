package lhc.indexers

import lhc.config.SolrConfig
import lhc.indexers.solr._
import lhc.messages.Message
import lhc.util.DefaultLhcLogger
import play.api.Application
import play.api.libs.concurrent.Akka
import play.api.libs.concurrent.Execution.Implicits._
import org.apache.solr.common.SolrInputDocument
import scala.concurrent.duration.Duration
import scala.util.control.NonFatal
import java.util.concurrent.{ArrayBlockingQueue, TimeUnit}

class SolrIndexer(val cfg: SolrConfig, val app: Application) extends Indexer with DefaultLhcLogger {

//  protected val solrInstance = InstanceManager.get(cfg)
  val solrInstance = InstanceManager.get(cfg)
  protected val queue = new ArrayBlockingQueue[SolrInputDocument](10000)

  Akka.system(app).scheduler.schedule(
    SolrIndexer.TEN_SECONDS,
    SolrIndexer.TEN_SECONDS,
    new Runnable() {
      override def run() {
        logger.debug("Running document drain")
        drainQueue()
      }
    }
  )

  override def index(message: Message) {
    val doc = DocumentManager.convert(message)
    if (!queue.offer(doc)) {
      logger.warn("Failed to add message to queue")
    } else {
      logger.debug("Added message to queue")
    }
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

  override def shutdown() {
    logger.info("Shutting down solr indexer")
    drainQueue()
    InstanceManager.shutdown(solrInstance)
  }

}

object SolrIndexer {
  val TEN_SECONDS = Duration.create(10, TimeUnit.SECONDS)
}
