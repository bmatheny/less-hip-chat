package controllers

import models.{Page, PageParams, SortDirection}
import lhc.plugins.IndexPlugin

import play.api._
import play.api.libs._
import play.api.libs.json._
import play.api.mvc._

object Application extends Controller {

  import play.api.Play.current
  import formats._

  def groups = Action { implicit req =>
    val json = Json.toJson(IndexPlugin.getGroups(current))
    req.queryString.get("callback").flatMap(_.headOption) match {
      case Some(callback) => Ok(Jsonp(callback, json))
      case None => Ok(json)
    }
  }

  def index = Action {
    Ok(views.html.index(IndexPlugin.getGroups(current), Seq()))
  }
 
  def search(query: Option[String], size: Option[Int], page: Option[Int], sort: Option[String]) =
    Action { implicit req =>
      val params = PageParams(
        page.filter(_ >= 0).getOrElse(0),
        size.filter(i => i > 0 && i <= 500).getOrElse(10),
        sort.flatMap(SortDirection.withName(_)).getOrElse(SortDirection.Desc)
      )
      val uquery = query.getOrElse("*:*")
      val results = IndexPlugin.find(current, query.getOrElse("*:*"), params)
      val json = Json.toJson(results.toJson)
      req.queryString.get("callback").flatMap(_.headOption) match {
        case Some(callback) => Ok(Jsonp(callback, json))
        case None => Ok(json)
      }
    }

}
