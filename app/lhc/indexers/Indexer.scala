package lhc.indexers

import models.{Group, Message}

trait Indexer {
  def find(query: String, rows: Int = 10, start: Int = 10, sort: Sort = Sort.Desc): Seq[Message]
  def getGroups(): Set[Group]
  def getRecent(rows: Int = 10): Seq[Message]
  def index(message: Message)
  def shutdown() {
  }
}
