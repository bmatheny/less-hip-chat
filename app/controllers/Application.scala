package controllers

import play.api._
import play.api.mvc._
import lhc.plugins.IndexPlugin

object Application extends Controller {

  import play.api.Play.current

  def index = Action {
    Ok(views.html.index(IndexPlugin.getGroups(current), IndexPlugin.query(current)))
  }
  
}
