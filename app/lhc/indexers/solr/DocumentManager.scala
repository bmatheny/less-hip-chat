package lhc.indexers.solr

import lhc.messages.{BasicMessage, Message}
import lhc.util.DefaultLhcLogger
import org.apache.solr.common.{SolrDocument, SolrInputDocument}
import scala.collection.JavaConverters._
import java.util.concurrent.BlockingQueue
import java.util.{ArrayList, Collection, Date, UUID}

object DocumentManager extends DefaultLhcLogger {

  def convert(message: Message): SolrInputDocument = {
    val doc = new SolrInputDocument()
    doc.addField("uuid", UUID.randomUUID().toString())
    doc.addField("id", message.getId.toInt)
    doc.addField("timestamp", message.iso8601)
    doc.addField("group", message.getGroup)
    doc.addField("message", message.getMessage)
    message.getUser.foreach { case(key, value) =>
      doc.addField("user_%s_meta_s".format(key), value)
    }
    logger.debug("Created doc for message %s".format(message))
    doc
  }

  def convert(doc: SolrDocument): Message = {
    val userMap = doc.getFieldNames.asScala.filter(_.startsWith("user_")).map { key =>
      val ukey = key.split("_")(1)
      ukey -> doc.getFirstValue(key).asInstanceOf[String]
    }.toMap[String,String]
    BasicMessage.createMessage(
      id = doc.getFieldValue("id").asInstanceOf[Int].toLong,
      timestamp = doc.getFieldValue("timestamp").asInstanceOf[Date].getTime,
      group = doc.getFieldValue("group").asInstanceOf[String],
      message = doc.getFieldValue("message").asInstanceOf[String],
      user = userMap
    )
  }

  type SID = SolrInputDocument
  def drain(queue: BlockingQueue[SID]): Collection[SID] = {
    val docs = new ArrayList[SID]()
    val size = queue.drainTo(docs)
    if (size > 0)
      logger.debug("Drained %d documents from queue".format(size))
    else
      logger.trace("No documents found")
    docs
  }
}
