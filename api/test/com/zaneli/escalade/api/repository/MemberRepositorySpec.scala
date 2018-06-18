package com.zaneli.escalade.api.repository

import com.zaneli.escalade.api.entity.{CompanyEntity, MemberEntity}
import db.DBSetup
import org.specs2.mutable.{After, Specification}
import scalikejdbc._
import scalikejdbc.specs2.mutable.AutoRollback
import skinny.orm.exception.OptimisticLockException

class MemberRepositorySpec extends Specification with DBSetup {

  private[this] val repo = new MemberRepository()

  trait AutoRollbackWithFixture extends AutoRollback with After {
    override def fixture(implicit session: DBSession): Unit = {
      sql"""
         |INSERT INTO companies (`id`, `name`) VALUES (1, 'test_company_1'), (2, 'test_company_2')
       """.stripMargin.update.apply()
      sql"""
           |INSERT INTO members (`id`, `name`, `company_id`) VALUES (1, 'test_member_1_1', 1), (2, 'test_member_1_2', 1)
       """.stripMargin.update.apply()
      ()
    }
  }

  sequential

  "MemberRepository.selectById" should {
    "存在するIDを指定" in new AutoRollbackWithFixture {
      repo.selectById(1) must beSome.which { c =>
        c.id must_== 1L
        c.name must_== "test_member_1_1"
        c.version must_== 1L
      }
    }
    "存在しないIDを指定" in new AutoRollbackWithFixture {
      repo.selectById(-1) must beNone
    }
  }

  "MemberRepository.selectByCompanyId" should {
    "存在するCompanyIDを指定" in new AutoRollbackWithFixture {
      repo.selectByCompanyId(1).sortBy(_.id) must_== List(
        MemberEntity("test_member_1_1", CompanyEntity("test_company_1")),
        MemberEntity("test_member_1_2", CompanyEntity("test_company_1"))
      )
    }
    "存在しないCompanyIDを指定" in new AutoRollbackWithFixture {
      repo.selectByCompanyId(-1).sortBy(_.id) must beEmpty
    }
    "存在するが紐づくMemberが存在しないCompanyIDを指定" in new AutoRollbackWithFixture {
      repo.selectByCompanyId(2).sortBy(_.id) must beEmpty
    }
  }

  "MemberRepository.save()" should {
    "新規作成" in new AutoRollbackWithFixture {
      val entity = MemberEntity("inserted_member", CompanyEntity("test_company_1"))
      val result = repo.save(entity, 1)
      result must beAnInstanceOf[InsertSuccess[Long]]

      val InsertSuccess(id: Long) = result
      repo.selectById(id) must beSome(entity)
    }
    "更新" in new AutoRollbackWithFixture {
      repo.selectById(1) must beSome.which { m =>
        val entity = MemberEntity(m.id, "updated_member", CompanyEntity("test_company_1"), m.version)
        repo.save(entity, 1) must_== UpdateSuccess(1)

        repo.selectById(1) must beSome.which { u =>
          u.name must_== entity.name
          u.version must_== m.version + 1
        }
      }
    }
    "楽観ロックエラー" in new AutoRollbackWithFixture {
      repo.selectById(1) must beSome.which { m =>
        val entity = MemberEntity(m.id, "updated_member", CompanyEntity("test_company_1"), m.version + 1)
        val result = repo.save(entity, 1)
        result must beAnInstanceOf[Failure]

        val Failure(t) = result
        t must beAnInstanceOf[OptimisticLockException]

        repo.selectById(1) must beSome(MemberEntity("test_member_1_1", CompanyEntity("test_company_1")))
      }
    }
  }
}
