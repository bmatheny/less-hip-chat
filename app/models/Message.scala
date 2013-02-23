package models

import java.text.SimpleDateFormat
import java.util.{Date, TimeZone}
import org.joda.time.format.ISODateTimeFormat

trait Message {
  def getUuid(): String
  def getTimestamp(): Long
  def getGroup(): String
  def getUser(): Map[String,String]
  def getMessage(): String

  def iso8601(): String = Message.toIso8601(getTimestamp)
  def localTime(): String = Message.toLocalTime(getTimestamp)
}

object Message {
  def apply(
    uuid: String = "", timestamp: Long = 0L, group: String = "",
    user: Map[String, String] = Map.empty, message: String = ""
  ): Message = new MessageImpl(uuid, timestamp, group, user, message)

  private val UTC = TimeZone.getTimeZone("UTC")
  // joda time ISODateTimeFormat parser is thread safe
  private val Iso8601Parser = {
    val parser = ISODateTimeFormat.dateTime
    parser.withZoneUTC()
  }
  def fromIso8601(timestamp: String): Long = {
    Iso8601Parser.parseDateTime(timestamp).getMillis
  }
  def toIso8601(timestamp: Long): String = {
    val fmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    fmt.setTimeZone(UTC)
    fmt.format(new Date(timestamp))
  }
  def toLocalTime(timestamp: Long): String = {
    val fmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    fmt.format(new Date(timestamp))
  }

}


