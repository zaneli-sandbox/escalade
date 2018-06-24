package com.zaneli.escalade.api.service

import com.zaneli.escalade.api.entity._
import com.zaneli.escalade.api.repository.Result
import com.zaneli.escalade.api.repository.{ CompanyRepository, MemberRepository }
import javax.inject.Inject
import scalikejdbc.DB

class CompanyMemberService @Inject() (cr: CompanyRepository, mr: MemberRepository) {

  def findCompanyById(id: CompanyId): Option[CompanyEntity with HasId[CompanyId] with HasVersion] = {
    DB.readOnly { implicit s =>
      cr.findById(id)
    }
  }

  def findCompanies(): List[CompanyEntity with HasId[CompanyId] with HasVersion] = {
    DB.readOnly { implicit s =>
      cr.findAll()
    }
  }

  def save(company: CompanyEntity): Either[Throwable, Result[CompanyId]] = {
    DB.localTx { implicit s =>
      cr.save(company)
    }
  }

  def save(member: MemberEntity): Either[Throwable, Unit] = {
    DB.localTx { implicit s =>
      cr.save(member.company).flatMap {
        case Result.InsertSuccess(id) =>
          mr.save(member, id)
        case Result.UpdateSuccess(_) =>
          member.company match {
            case c: HasId[CompanyId] =>
              mr.save(member, c.id)
            case _ =>
              Left(new RuntimeException(s"unexpected member entity: $member"))
          }
      }.map(_ => ())
    }
  }
}
