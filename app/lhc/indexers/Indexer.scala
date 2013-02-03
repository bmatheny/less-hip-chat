package lhc.indexers

import lhc.messages.Message

trait Indexer {
  def index(message: Message)
  def shutdown() {
  }
}
