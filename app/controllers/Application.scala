package controllers

import play.api._
import play.api.mvc._
import lhc.indexers.Sort
import lhc.plugins.IndexPlugin

object Application extends Controller {

  import play.api.Play.current

  def index(query: Option[String], size: Option[Int], page: Option[Int], sort: Option[String]) = Action {
    val uquery = query.getOrElse("*:*")
    val usize = size.filter(_ <= 500).getOrElse(10)
    val upage = page.getOrElse(0)
    val usort = sort.map(_.toLowerCase).map { s =>
      if (s == "asc") Sort.Asc else Sort.Desc
    }.getOrElse(Sort.Desc)
    val fResults = IndexPlugin.find(current, uquery, usize, upage, usort)
    Ok(views.html.index(IndexPlugin.getGroups(current), fResults))
  }
  
}
