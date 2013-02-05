package lhc.indexers.solr

import lhc.messages.Message
import lhc.util.DefaultLhcLogger
import org.apache.solr.common.SolrInputDocument
import java.text.SimpleDateFormat
import java.util.concurrent.BlockingQueue
import java.util.{ArrayList, Collection, Date, UUID}

object DocumentManager extends DefaultLhcLogger {

  def convert(message: Message): SolrInputDocument = {
    val doc = new SolrInputDocument()
    doc.addField("uuid", UUID.randomUUID().toString())
    doc.addField("id", message.getId.toInt)
    doc.addField("timestamp", mkIso8601(message.getTimestamp))
    doc.addField("group", message.getGroup)
    doc.addField("message", message.getMessage)
    message.getUser.foreach { case(key, value) =>
      doc.addField("user_%s_meta_s".format(key), value)
    }
    logger.debug("Created doc for message %s".format(message))
    doc
  }

  type SID = SolrInputDocument
  def drain(queue: BlockingQueue[SID]): Collection[SID] = {
    val docs = new ArrayList[SID]()
    val size = queue.drainTo(docs)
    logger.debug("Drained %d documents from queue".format(size))
    docs
  }

  def mkIso8601(timestamp: Long): String = {
    val fmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    fmt.format(new Date(timestamp))
  }
}
