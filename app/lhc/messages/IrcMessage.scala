package lhc.messages

import org.pircbotx.Colors

case class IrcMessage(
  uuid: String, timestamp: Long, group: String, user: Map[String,String], msg: String
) extends BasicMessage(uuid, timestamp, group, user, msg) {
  override def getMessage(): String = Colors.removeFormattingAndColors(msg)
}
