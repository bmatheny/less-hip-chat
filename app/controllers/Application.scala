package controllers

import models.SortDirection
import lhc.plugins.IndexPlugin

import play.api._
import play.api.libs._
import play.api.libs.json._
import play.api.mvc._

object Application extends Controller {

  import play.api.Play.current
  import formats._

  def groups = Action {
    Ok(Json.toJson(IndexPlugin.getGroups(current)))
  }

  def index = Action {
    Ok(views.html.index(IndexPlugin.getGroups(current), Seq()))
  }
 
  def search(query: Option[String], size: Option[Int], page: Option[Int], sort: Option[String]) = Action { implicit req =>
    val uquery = query.getOrElse("*:*")
    val usize = size.filter(i => i > 0 && i <= 500).getOrElse(10)
    val upage = page.filter(_ >= 0).getOrElse(0)
    val usort = sort.flatMap(SortDirection.withName(_)).getOrElse(SortDirection.Desc)
    val fResults = IndexPlugin.find(current, uquery, usize, upage, usort)
    val json = Json.toJson(fResults)
    req.queryString.get("callback").flatMap(_.headOption) match {
      case Some(callback) => Ok(Jsonp(callback, json))
      case None => Ok(json)
    }
  }

}
