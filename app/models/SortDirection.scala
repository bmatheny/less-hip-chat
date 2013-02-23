package models

sealed trait SortDirection {
  def name: String

  override def toString: String = s"SortDirection.$name"

  def unary_~ : SortDirection = this match {
    case SortDirection.Desc => SortDirection.Asc
    case SortDirection.Asc => SortDirection.Desc
  }
}

object SortDirection {
  case object Asc extends SortDirection {
    val name = "ASC"
  }
  case object Desc extends SortDirection {
    val name = "DESC"
  }

  val directions = Asc :: Desc :: Nil

  def withName(direction: String): Option[SortDirection] =
    directions.find(_.name == direction.toUpperCase)
}
