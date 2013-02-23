package models

/**
 * The parameters of a page are generally described by the page number (0-N), size of the page (>0),
 * and the sort direction. The offset into the result set can be calculated as page*size. That is,
 * if you request page 2 with size 10, the offset should be 20.
 */
trait PageParams {
  def page: Int
  def size: Int
  def sort: SortDirection
  def sortBy: Option[String]

  def offset: Int = page * size

  def validate() {
    require(page >= 0, "page must be >= 0")
    require(size > 0, "size must be > 0")
  }
}

case class PageParamsImpl(
  page: Int, size: Int, sort: SortDirection, sortBy: Option[String]
) extends PageParams

object PageParams {
  def apply(page: Int, size: Int, sort: String, sortBy: Option[String]): PageParams =
    PageParamsImpl(page, size, SortDirection.withName(sort).getOrElse(SortDirection.Desc), sortBy)

  def empty = PageParamsImpl(0, 0, SortDirection.Desc, None)
}
