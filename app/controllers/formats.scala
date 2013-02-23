package controllers

import models._

import play.api.libs.json._

object formats {
  implicit object GroupFormat extends Format[Group] {
    def reads(js: JsValue): JsResult[Group] = JsSuccess(GroupImpl(
      (js \ "name").as[String],
      (js \ "messageCount").as[Long]
    ))
    def writes(g: Group): JsValue = Json.obj(
      "name" -> g.name,
      "messageCount" -> g.messageCount
    )
  }

  implicit object MessageFormat extends Format[Message] {
    def reads(json: JsValue): JsResult[Message] = JsSuccess(new MessageImpl(
      (json \ "uuid").as[String],
      (json \ "timestamp").as[Long],
      (json \ "group").as[String],
      (json \ "user").asOpt[Map[String,String]].getOrElse(Map.empty),
      (json \ "message").as[String]
    ))

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
