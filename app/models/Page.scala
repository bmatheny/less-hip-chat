package models

import play.api.libs.json._
import play.api.libs._

trait Page[+A] {
  def items: Seq[A]
  def params: PageParams
  def total: Long

  def prev = Option(params.page - 1).filter(_ >= 0).getOrElse(0)
  def next = Option(params.page + 1)
              .filter(_ => (params.offset + size) < total)
              .getOrElse(params.page)

  lazy val size = items.size

  def toJson(implicit tjs: Writes[Seq[A]]): JsValue = Json.obj(
    "page" -> Json.obj(
      "prev" -> prev,
      "current" -> params.page,
      "next" -> next,
      "total" -> total
    ),
    "items" -> Json.toJson(items)(tjs)
  )
}

case class PageImpl[+A](
  items: Seq[A], params: PageParams, total: Long
) extends Page[A]

object Page {
  def apply[A](items: Seq[A], params: PageParams, total: Long) =
    PageImpl(items, params, total)

  def empty[A] = PageImpl[A](Nil, PageParams.empty, 0L)
}
