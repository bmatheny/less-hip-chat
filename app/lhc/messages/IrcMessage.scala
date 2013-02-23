package lhc.messages

import models.MessageImpl
import org.pircbotx.Colors

case class IrcMessage(
  uuid: String, timestamp: Long, group: String, user: Map[String,String], msg: String
) extends MessageImpl(uuid, timestamp, group, user, msg) {
  override def getMessage(): String = Colors.removeFormattingAndColors(msg)
}
