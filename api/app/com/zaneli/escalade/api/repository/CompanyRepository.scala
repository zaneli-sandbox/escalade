package com.zaneli.escalade.api.repository

import com.zaneli.escalade.api.entity.{CompanyEntity, HasId, HasVersion}
import com.zaneli.escalade.api.persistence.Company
import scalikejdbc.DBSession

import scala.util.control.NonFatal

class CompanyRepository {

  def save(entity: CompanyEntity)(implicit s: DBSession): Result[Long] = {
    val column = Company.column
    try {
      entity match {
        case c: HasId[Long] with HasVersion =>
          val count = Company.updateByIdAndVersion(c.id, c.version)
            .withNamedValues(column.name -> c.name)
          UpdateSuccess(count)
        case c =>
          val id = Company.createWithNamedValues(
            column.name -> c.name
          )
          InsertSuccess(id)
      }
    } catch {
      case NonFatal(e) => Failure(e)
    }
  }

  def selectById(id: Long)(implicit s: DBSession): Option[CompanyEntity with HasId[Long] with HasVersion] = {
    Company.findById(id).map(c => CompanyEntity(c.id, c.name, c.lockVersion))
  }
}
