package com.zaneli.escalade.api.persistence

import scalikejdbc.{ WrappedResultSet, autoConstruct }
import skinny.orm.{ Alias, SkinnyCRUDMapper }
import skinny.orm.feature.OptimisticLockWithVersionFeature
import skinny.orm.feature.associations.BelongsToAssociation

case class Member(
  id: Long,
  name: String,
  companyId: Long,
  lockVersion: Long,
  company: Option[Company] = None)

object Member extends SkinnyCRUDMapper[Member] with OptimisticLockWithVersionFeature[Member] {

  override lazy val tableName: String = "members"

  override lazy val defaultAlias: Alias[Member] = createAlias("mb")

  override def extract(rs: WrappedResultSet, n: scalikejdbc.ResultName[Member]): Member = autoConstruct(rs, n, "company")

  lazy val companyRef: BelongsToAssociation[Member] =
    belongsTo[Company](Company, (m, c) => m.copy(company = c))
      .includes[Company]((es, cs) => es.map { e =>
        cs.find(c => e.companyId == c.id).map(v => e.copy(company = Some(v))).getOrElse(e)
      })
}
