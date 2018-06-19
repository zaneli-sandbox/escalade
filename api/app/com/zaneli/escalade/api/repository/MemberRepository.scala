package com.zaneli.escalade.api.repository

import com.zaneli.escalade.api.entity._
import com.zaneli.escalade.api.persistence.Member
import scalikejdbc._

class MemberRepository extends InsertOrOptimisticLockUpdate[MemberEntity, MemberId] {

  def save(entity: MemberEntity, companyId: CompanyId)(implicit s: DBSession): Either[Throwable, Result[MemberId]] = {
    val column = Member.column
    insertOrUpdate(entity) { e =>
      val id = Member.createWithNamedValues(
        column.name -> e.name,
        column.companyId -> companyId
      )
      MemberId(id)
    } { e =>
      Member.updateByIdAndVersion(e.id.value, e.version)
        .withNamedValues(column.name -> entity.name, column.companyId -> companyId)
    }
  }

  def selectById(id: MemberId)(implicit s: DBSession): Option[MemberEntity with HasId[MemberId] with HasVersion] = {
    Member.includes(Member.companyRef).findById(id.value).flatMap { m =>
      m.company.map { c =>
        val company = CompanyEntity.apply(CompanyId(c.id), c.name, c.lockVersion)
        MemberEntity(MemberId(m.id), m.name, company, m.lockVersion)
      }
    }
  }

  def selectByCompanyId(companyId: CompanyId)(implicit s: DBSession): List[MemberEntity with HasId[MemberId] with HasVersion] = {
    val column = Member.column
    Member.includes(Member.companyRef).findAllBy(sqls.eq(column.companyId, companyId)).flatMap { m =>
      m.company.map { c =>
        val company = CompanyEntity.apply(CompanyId(c.id), c.name, c.lockVersion)
        MemberEntity(MemberId(m.id), m.name, company, m.lockVersion)
      }
    }
  }
}
