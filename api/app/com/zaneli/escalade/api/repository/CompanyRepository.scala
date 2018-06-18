package com.zaneli.escalade.api.repository

import com.zaneli.escalade.api.entity.{CompanyEntity, HasId, HasVersion}
import com.zaneli.escalade.api.persistence.Company
import scalikejdbc.DBSession

class CompanyRepository {

  def save(entity: CompanyEntity)(implicit s: DBSession): Result[Long] = {
    val column = Company.column
    insertOrOptimisticLockUpdate(entity) {
      Company.createWithNamedValues(column.name -> entity.name)
    } { e =>
      Company.updateByIdAndVersion(e.id, e.version).withNamedValues(column.name -> entity.name)
    }
  }

  def selectById(id: Long)(implicit s: DBSession): Option[CompanyEntity with HasId[Long] with HasVersion] = {
    Company.findById(id).map(c => CompanyEntity(c.id, c.name, c.lockVersion))
  }
}
