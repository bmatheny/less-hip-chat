package lhc.indexers

import models.{Group, Message, Page, PageParams}

trait Indexer {
  def find(query: String, pageParms: PageParams): Page[Message]
  def getGroups(): Set[Group]
  def getRecent(rows: Int = 10): Seq[Message]
  def index(message: Message)
  def shutdown() {
  }
}
