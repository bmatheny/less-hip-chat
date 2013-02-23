package models

import org.specs2.mutable._

class SortDirectionSpec extends Specification {
  "SortDirection" should {
    "convert 'asc' to Asc" in {
      SortDirection.withName("asc") must beSome(SortDirection.Asc)
    }
    "convert 'ASC' to Asc" in {
      SortDirection.withName("ASC") must beSome(SortDirection.Asc)
    }
    "convert 'desc' to Desc" in {
      SortDirection.withName("desc") must beSome(SortDirection.Desc)
    }
    "convert 'DESC' to Desc" in {
      SortDirection.withName("DESC") must beSome(SortDirection.Desc)
    }
    "convert 'fizz' to None" in {
      SortDirection.withName("fizz") must beNone
    }
  }
}
