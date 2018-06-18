package com.zaneli.escalade.api.service

import com.zaneli.escalade.api.entity.{HasId, MemberEntity}
import com.zaneli.escalade.api.repository.{CompanyRepository, Failure, InsertSuccess, MemberRepository, UpdateSuccess}
import scalikejdbc.DB

class CompanyMemberService(cr: CompanyRepository, mr: MemberRepository) {

  def save(member: MemberEntity): Either[Throwable, Unit] = {
    DB.localTx { implicit s =>
      cr.save(member.company) match {
        case InsertSuccess(id) =>
          mr.save(member, id)
          Right(())
        case UpdateSuccess(_) =>
          member.company match {
            case c: HasId[Long] =>
              mr.save(member, c.id)
              Right(())
            case _ =>
              Left(new RuntimeException(s"unexpected member entity: $member"))
          }
        case Failure(t) =>
          Left(t)
      }
    }
  }
}
