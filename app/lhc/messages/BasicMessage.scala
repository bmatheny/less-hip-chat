package lhc.messages

import models.Message

abstract class BasicMessage(
  uuid: String, timestamp: Long, group: String, user: Map[String,String], message: String
) extends Message {
  override def getUuid() = uuid
  override def getTimestamp() = timestamp
  override def getGroup() = group
  override def getUser() = user
  override def getMessage() = message
}

object BasicMessage {
  def createMessage(
    uuid: String = "", timestamp: Long = 0, group: String = "", user: Map[String,String] = Map(),
    message: String = ""
  ): Message = {
    new BasicMessage(uuid, timestamp, group, user, message) {}
  }
}
