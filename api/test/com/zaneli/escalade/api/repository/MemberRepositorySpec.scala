package com.zaneli.escalade.api.repository

import com.zaneli.escalade.api.entity.{CompanyEntity, CompanyId, MemberEntity, MemberId}
import com.zaneli.escalade.api.repository.Result.{InsertSuccess, UpdateSuccess}
import db.DBSetup
import org.specs2.mutable.{After, Specification}
import scalikejdbc._
import scalikejdbc.specs2.mutable.AutoRollback
import skinny.orm.exception.OptimisticLockException

class MemberRepositorySpec extends Specification with DBSetup {

  private[this] val repo = new MemberRepository()

  trait AutoRollbackWithFixture extends AutoRollback with After {
    override def fixture(implicit session: DBSession): Unit = DBSetup.fixture
  }

  sequential

  "MemberRepository.selectById" should {
    "存在するIDを指定" in new AutoRollbackWithFixture {
      val id = MemberId(1L)
      repo.findById(id) must beSome.which { c =>
        c.id must_== id
        c.name must_== "test_member_1_1"
        c.version must_== 1L
      }
    }
    "存在しないIDを指定" in new AutoRollbackWithFixture {
      repo.findById(MemberId(-1L)) must beNone
    }
  }

  "MemberRepository.selectByCompanyId" should {
    "存在するCompanyIDを指定" in new AutoRollbackWithFixture {
      repo.findByCompanyId(CompanyId(1L)).sortBy(_.id) must_== List(
        MemberEntity("test_member_1_1", CompanyEntity("test_company_1")),
        MemberEntity("test_member_1_2", CompanyEntity("test_company_1"))
      )
    }
    "存在しないCompanyIDを指定" in new AutoRollbackWithFixture {
      repo.findByCompanyId(CompanyId(-1L)).sortBy(_.id) must beEmpty
    }
    "存在するが紐づくMemberが存在しないCompanyIDを指定" in new AutoRollbackWithFixture {
      repo.findByCompanyId(CompanyId(2L)).sortBy(_.id) must beEmpty
    }
  }

  "MemberRepository.save()" should {
    "新規作成" in new AutoRollbackWithFixture {
      val entity = MemberEntity("inserted_member", CompanyEntity("test_company_1"))
      repo.save(entity, CompanyId(1L)) must beRight.which {
        case InsertSuccess(id) => repo.findById(id) must beSome(entity)
        case _ => ko
      }
    }
    "更新" in new AutoRollbackWithFixture {
      repo.findById(MemberId(1L)) must beSome.which { m =>
        val entity = MemberEntity(m.id, "updated_member", CompanyEntity("test_company_1"), m.version)
        repo.save(entity, CompanyId(1L)) must beRight(UpdateSuccess(1))

        repo.findById(entity.id) must beSome.which { u =>
          u.name must_== entity.name
          u.version must_== m.version + 1
        }
      }
    }
    "楽観ロックエラー" in new AutoRollbackWithFixture {
      repo.findById(MemberId(1L)) must beSome.which { m =>
        val entity = MemberEntity(m.id, "updated_member", CompanyEntity("test_company_1"), m.version + 1)
        repo.save(entity, CompanyId(1L)) must beLeft.which(_ must beAnInstanceOf[OptimisticLockException])

        repo.findById(entity.id) must beSome(MemberEntity("test_member_1_1", CompanyEntity("test_company_1")))
      }
    }
  }
}
