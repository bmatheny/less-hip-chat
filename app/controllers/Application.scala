package controllers

import play.api._
import play.api.libs._
import play.api.libs.json._
import play.api.mvc._
import lhc.indexers.Sort
import lhc.messages.Message
import lhc.plugins.IndexPlugin

object Application extends Controller {

  import play.api.Play.current

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

  def search(query: Option[String], size: Option[Int], page: Option[Int], sort: Option[String]) = Action { implicit req =>
    val uquery = query.getOrElse("*:*")
    val usize = size.filter(_ <= 500).getOrElse(10)
    val upage = page.getOrElse(0)
    val usort = sort.map(_.toLowerCase).map { s =>
      if (s == "asc") Sort.Asc else Sort.Desc
    }.getOrElse(Sort.Desc)
    val fResults = IndexPlugin.find(current, uquery, usize, upage, usort)
    val json = Json.toJson(fResults)
    req.queryString.get("callback").flatMap(_.headOption) match {
      case Some(callback) => Ok(Jsonp(callback, json))
      case None => Ok(json)
    }
  }

  def index = Action {
    Ok(views.html.index(IndexPlugin.getGroups(current), Seq()))
  }
  
}
