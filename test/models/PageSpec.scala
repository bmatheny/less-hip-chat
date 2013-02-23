package models

import org.specs2.mutable._

class PageSpec extends Specification {
  val allItems = (1 to 51).toSeq
  val pages = Map(
    0 -> allItems.slice(0, 10),
    1 -> allItems.slice(10, 20),
    2 -> allItems.slice(20, 30),
    3 -> allItems.slice(30, 40),
    4 -> allItems.slice(40, 50),
    5 -> allItems.slice(50, 51)
  )

  def page(n: Int): Page[Int] =
    Page[Int](pages(n), PageParams(n, 10, SortDirection.Desc), allItems.size)

  "Page" should {
    "support page.prev" in {
      page(0).prev mustEqual 0
      page(1).prev mustEqual 0
      page(2).prev mustEqual 1
      page(3).prev mustEqual 2
      page(4).prev mustEqual 3
      page(5).prev mustEqual 4
    }
    "support page.next" in {
      page(0).next mustEqual 1
      page(1).next mustEqual 2
      page(2).next mustEqual 3
      page(3).next mustEqual 4
      page(4).next mustEqual 5
      page(5).next mustEqual 5
    }
    "support page.size" in {
      page(0).size mustEqual 10
      page(5).size mustEqual 1
    }
    "support page.total" in {
      page(0).total mustEqual allItems.size
      page(5).total mustEqual allItems.size
    }
  }
}
