package models

trait Group {
  def name: String
  def messageCount: Long
}

case class GroupImpl(name: String, messageCount: Long) extends Group

object Group {
  def apply(name: String, messageCount: Long): Group = GroupImpl(name, messageCount)
  def apply(name: String): Group = GroupImpl(name, 0L)
  def empty: Group = GroupImpl("", 0L)
}
