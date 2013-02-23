package models

import play.api.libs.json._

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

  implicit object MessageFormat extends Writes[Message] {
    def writes(m: Message): JsValue = Json.obj(
      "uuid" -> m.getUuid,
      "timestamp" -> m.getTimestamp,
      "iso8601" -> m.iso8601,
      "localtime" -> m.localTime,
      "group" -> m.getGroup,
      "user" -> m.getUser,
      "message" -> m.getMessage
    )
  }
}