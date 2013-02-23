package models

trait Page[+A] {
  def items: Seq[A]
  def params: PageParams
  def total: Long

  def prev = Option(params.page - 1).filter(_ >= 0).getOrElse(0)
  def next = Option(params.page + 1)
              .filter(_ => (params.offset + size) < total)
              .getOrElse(params.page)

  lazy val size = items.size
}

case class PageImpl[+A](
  items: Seq[A], params: PageParams, total: Long
) extends Page[A]

object Page {
  def empty[A] = PageImpl[A](Nil, PageParams.empty, 0L)
}
