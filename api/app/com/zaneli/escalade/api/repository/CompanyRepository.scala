package com.zaneli.escalade.api.repository

import com.zaneli.escalade.api.entity.{CompanyEntity, CompanyId, HasId, HasVersion}
import com.zaneli.escalade.api.persistence.Company
import com.zaneli.escalade.generator._
import scalikejdbc.DBSession

class CompanyRepository extends InsertOrOptimisticLockUpdate[CompanyEntity, CompanyId] {

  def save(entity: CompanyEntity)(implicit s: DBSession): Either[Throwable, Result[CompanyId]] = {
    val nvs = autoNamedValues(entity, Company.column)
    insertOrUpdate(entity) { _ =>
      CompanyId(Company.createWithNamedValues(nvs: _*))
    } { e =>
      Company.updateByIdAndVersion(e.id.value, e.version).withNamedValues(nvs: _*)
    }
  }

  def selectById(id: CompanyId)(implicit s: DBSession): Option[CompanyEntity with HasId[CompanyId] with HasVersion] = {
    Company.findById(id.value).map(c => CompanyEntity(CompanyId(c.id), c.name, c.lockVersion))
  }
}
