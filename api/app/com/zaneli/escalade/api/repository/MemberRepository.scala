package com.zaneli.escalade.api.repository

import com.zaneli.escalade.api.entity.{CompanyEntity, HasId, HasVersion, MemberEntity}
import com.zaneli.escalade.api.persistence.Member
import scalikejdbc._

import scala.util.control.NonFatal

class MemberRepository {

  def save(entity: MemberEntity, companyId: Long)(implicit s: DBSession): Result[Long] = {
    val column = Member.column
    try {
      entity match {
        case m: HasId[Long] with HasVersion =>
          val count = Member.updateByIdAndVersion(m.id, m.version)
            .withNamedValues(column.name -> m.name, column.companyId -> companyId)
          UpdateSuccess(count)
        case m =>
          val id = Member.createWithNamedValues(
            column.name -> m.name,
            column.companyId -> companyId
          )
          InsertSuccess(id)
      }
    } catch {
      case NonFatal(e) => Failure(e)
    }
  }

  def selectById(id: Long)(implicit s: DBSession): Option[MemberEntity with HasId[Long] with HasVersion] = {
    Member.includes(Member.companyRef).findById(id).flatMap { m =>
      m.company.map { c =>
        val company = CompanyEntity.apply(c.id, c.name, c.lockVersion)
        MemberEntity(m.id, m.name, company, m.lockVersion)
      }
    }
  }

  def selectByCompanyId(companyId: Long)(implicit s: DBSession): List[MemberEntity with HasId[Long] with HasVersion] = {
    val column = Member.column
    Member.includes(Member.companyRef).findAllBy(sqls.eq(column.companyId, companyId)).flatMap { m =>
      m.company.map { c =>
        val company = CompanyEntity.apply(c.id, c.name, c.lockVersion)
        MemberEntity(m.id, m.name, company, m.lockVersion)
      }
    }
  }
}
