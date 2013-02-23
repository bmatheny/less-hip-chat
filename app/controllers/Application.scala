package controllers

import models.{Page, PageParams, SortDirection}
import lhc.plugins.IndexPlugin

import play.api._
import play.api.libs._
import play.api.libs.json._
import play.api.mvc._

import scala.concurrent._
import ExecutionContext.Implicits.global

object Application extends Controller {

  import play.api.Play.current
  import formats._

  def groups = Action { implicit req =>
    val futureResult = future {
      Json.toJson(IndexPlugin.getGroups(current))
    }
    Async {
      futureResult.map { json =>
        req.queryString.get("callback").flatMap(_.headOption) match {
          case Some(callback) => Ok(Jsonp(callback, json))
          case None => Ok(json)
        }
      }
    }
  }

  def index = Action {
    val groups = IndexPlugin.getGroups(current).toSeq.sortWith((a, b) =>
      a.name.toUpperCase < b.name.toUpperCase
    )
    Ok(views.html.index(groups, Seq()))
  }
 
  def search(query: Option[String], size: Option[Int], page: Option[Int], sort: Option[String]) =
    Action { implicit req =>
      val params = PageParams(
        page.filter(_ >= 0).getOrElse(0),
        size.filter(i => i > 0 && i <= 500).getOrElse(10),
        sort.flatMap(SortDirection.withName(_)).getOrElse(SortDirection.Desc)
      )
      val uquery = query.getOrElse("*:*")
      val futureResult = future {
        IndexPlugin.find(current, query.getOrElse("*:*"), params)
      }
      Async {
        futureResult.map { r =>
          val json = Json.toJson(r.toJson)
          req.queryString.get("callback").flatMap(_.headOption) match {
            case Some(callback) => Ok(Jsonp(callback, json))
            case None => Ok(json)
          }
        }
      }
    }

}
