package models

import play.api.libs.json._

trait Group {
  def name: String
  def messageCount: Long
}

case class BasicGroup(name: String, messageCount: Long) extends Group

object Group {
  implicit object GroupFormat extends Writes[Group] {
    def writes(g: Group): JsValue = Json.obj(
      "name" -> g.name,
      "messageCount" -> g.messageCount
    )
  }
}
