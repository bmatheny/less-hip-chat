package lhc.indexers

sealed trait Sort

object Sort {
  object Desc extends Sort
  object Asc extends Sort
}
