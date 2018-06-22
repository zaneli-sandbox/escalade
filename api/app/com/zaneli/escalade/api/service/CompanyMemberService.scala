package com.zaneli.escalade.api.service

import com.zaneli.escalade.api.entity.{CompanyId, HasId, MemberEntity}
import com.zaneli.escalade.api.repository.Result.{InsertSuccess, UpdateSuccess}
import com.zaneli.escalade.api.repository.{CompanyRepository, MemberRepository}
import scalikejdbc.DB

class CompanyMemberService(cr: CompanyRepository, mr: MemberRepository) {

  def save(member: MemberEntity): Either[Throwable, Unit] = {
    DB.localTx { implicit s =>
      cr.save(member.company).flatMap {
        case InsertSuccess(id) =>
          mr.save(member, id)
        case UpdateSuccess(_) =>
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
