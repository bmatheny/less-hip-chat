package lhc.messages

abstract class BasicMessage(
  id: Long, timestamp: Long, group: String, user: Map[String,String], message: String
) extends Message {
  override def getId() = id
  override def getTimestamp() = timestamp
  override def getGroup() = group
  override def getUser() = user
  override def getMessage() = message
}

object BasicMessage {
  def createMessage(
    id: Long = 0, timestamp: Long = 0, group: String = "", user: Map[String,String] = Map(),
    message: String = ""
  ): Message = {
    new BasicMessage(id, timestamp, group, user, message) {}
  }
}
