package models

class MessageImpl(
  uuid: String, timestamp: Long, group: String, user: Map[String,String], message: String
) extends Message {
  override def getUuid() = uuid
  override def getTimestamp() = timestamp
  override def getGroup() = group
  override def getUser() = user
  override def getMessage() = message
}
