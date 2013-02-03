package lhc.messages

case class IrcMessage(
  id: Long, timestamp: Long, channel: String, user: Map[String,String], msg: String
) extends Message {
  override def getId() = id
  override def getTimestamp() = timestamp
  override def getGroup() = channel
  override def getUser() = user
  override def getMessage() = msg
}
