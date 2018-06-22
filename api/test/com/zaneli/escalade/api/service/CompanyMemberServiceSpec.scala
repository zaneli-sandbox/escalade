package com.zaneli.escalade.api.service

import com.zaneli.escalade.api.entity.{CompanyEntity, CompanyId, MemberEntity, MemberId}
import com.zaneli.escalade.api.repository.Result.{InsertSuccess, UpdateSuccess}
import com.zaneli.escalade.api.repository.{CompanyRepository, MemberRepository}
import db.DBSetup
import org.scalamock.specs2.MockContext
import org.specs2.mutable.Specification
import scalikejdbc.DBSession
import skinny.orm.exception.OptimisticLockException

class CompanyMemberServiceSpec extends Specification with DBSetup with MockContext {

  sequential

  "OrderService.save()" should {
    "Companyを新規作成した場合、新規採番したCompanyIDでMemberをsaveする" in new MockContext {
      val companyRepository = mock[CompanyRepository]
      val memberRepository = mock[MemberRepository]

      val companyId = CompanyId(123)
      val member = MemberEntity("test_member", CompanyEntity("test_company"))

      val service = new CompanyMemberService(companyRepository, memberRepository)

      (companyRepository.save(_: CompanyEntity)(_: DBSession))
        .expects(member.company, *)
        .returns(Right(InsertSuccess(companyId)))

      (memberRepository.save(_: MemberEntity, _: CompanyId)(_: DBSession))
        .expects(member, companyId, *)
        .returns(Right(InsertSuccess(MemberId(234))))

      service.save(member) must beRight
    }
    "Companyを更新した場合、元のCompanyIDでMemberをsaveする" in new MockContext {
      val companyRepository = mock[CompanyRepository]
      val memberRepository = mock[MemberRepository]

      val companyId = CompanyId(345)
      val member = MemberEntity("test_member", CompanyEntity(companyId, "test_company", 1L))

      val service = new CompanyMemberService(companyRepository, memberRepository)

      (companyRepository.save(_: CompanyEntity)(_: DBSession))
        .expects(member.company, *)
        .returns(Right(UpdateSuccess(1)))

      (memberRepository.save(_: MemberEntity, _: CompanyId)(_: DBSession))
        .expects(member, companyId, *)
        .returns(Right(InsertSuccess(MemberId(234))))

      service.save(member) must beRight
    }
    "Companyのsaveに失敗した場合、Memberのsaveを実行しない" in new MockContext {
      val companyRepository = mock[CompanyRepository]
      val memberRepository = mock[MemberRepository]

      val companyId = CompanyId(456)
      val member = MemberEntity("test_member", CompanyEntity("test_company"))

      val service = new CompanyMemberService(companyRepository, memberRepository)

      val expectedException = OptimisticLockException("test")

      (companyRepository.save(_: CompanyEntity)(_: DBSession))
        .expects(member.company, *)
        .returns(Left(expectedException))
      (memberRepository.save(_: MemberEntity, _: CompanyId)(_: DBSession))
        .expects(member, companyId, *)
        .never()

      service.save(member) must beLeft(expectedException)
    }
  }
}
