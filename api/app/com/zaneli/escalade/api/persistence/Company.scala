package com.zaneli.escalade.api.persistence

import scalikejdbc.{ WrappedResultSet, autoConstruct }
import skinny.orm.{ Alias, SkinnyCRUDMapper }
import skinny.orm.feature.OptimisticLockWithVersionFeature

case class Company(
  id: Long,
  name: String,
  lockVersion: Long)

object Company extends SkinnyCRUDMapper[Company] with OptimisticLockWithVersionFeature[Company] {

  override lazy val tableName: String = "companies"

  override lazy val defaultAlias: Alias[Company] = createAlias("cp")

  override def extract(rs: WrappedResultSet, n: scalikejdbc.ResultName[Company]): Company = autoConstruct(rs, n)
}
