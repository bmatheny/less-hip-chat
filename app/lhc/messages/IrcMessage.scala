package lhc.messages

import org.pircbotx.Colors

case class IrcMessage(
  id: Long, timestamp: Long, group: String, user: Map[String,String], msg: String
) extends BasicMessage(id, timestamp, group, user, msg) {
  override def getMessage(): String = Colors.removeFormattingAndColors(msg)
}
