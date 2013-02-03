package lhc.messages

trait Message {
  def getId(): Long
  def getTimestamp(): Long
  def getGroup(): String
  def getUser(): Map[String,String]
  def getMessage(): String
}
