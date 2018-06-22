package com.zaneli.escalade.api.persistence

import com.zaneli.escalade.api.entity.Price
import scalikejdbc._
import skinny.orm.{Alias, SkinnyCRUDMapper}
import skinny.orm.feature.{OptimisticLockWithVersionFeature, SoftDeleteWithBooleanFeature}

case class Item(
  id: Long,
  name: String,
  price: Price,
  lockVersion: Long,
  isDeleted: Boolean
)

object Item extends SkinnyCRUDMapper[Item] with OptimisticLockWithVersionFeature[Item] with SoftDeleteWithBooleanFeature[Item] {

  override lazy val tableName: String = "items"

  override lazy val defaultAlias: Alias[Item] = createAlias("it")

  override def extract(rs: WrappedResultSet, n: scalikejdbc.ResultName[Item]): Item = autoConstruct(rs, n)
}
