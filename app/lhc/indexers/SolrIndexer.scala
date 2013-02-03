package lhc.indexers

import lhc.config.SolrConfig
import lhc.messages.Message
import play.api.Application

class SolrIndexer(val cfg: SolrConfig, val app: Application) extends Indexer {

  override def index(message: Message) {
  }

  override def shutdown() {
    // TODO shutdown solr connection
  }
}
