package com.zaneli.escalade.api.repository

import com.zaneli.escalade.api.entity.CompanyEntity
import db.DBSetup
import org.specs2.mutable.{After, Specification}
import scalikejdbc._
import scalikejdbc.specs2.mutable.AutoRollback
import skinny.orm.exception.OptimisticLockException

class CompanyRepositorySpec extends Specification with DBSetup {

  private[this] val repo = new CompanyRepository()

  trait AutoRollbackWithFixture extends AutoRollback with After {
    override def fixture(implicit session: DBSession): Unit = DBSetup.fixture
  }

  sequential

  "CompanyRepository.selectById" should {
    "存在するIDを指定" in new AutoRollbackWithFixture {
      repo.selectById(1L) must beSome.which { c =>
        c.id must_== 1L
        c.name must_== "test_company_1"
        c.version must_== 1L
      }
    }
    "存在しないIDを指定" in new AutoRollbackWithFixture {
      repo.selectById(-1L) must beNone
    }
  }

  "CompanyRepository.save()" should {
    "新規作成" in new AutoRollbackWithFixture {
      val entity = CompanyEntity("inserted_company")
      val result = repo.save(entity)
      result must beAnInstanceOf[InsertSuccess[Long]]

      val InsertSuccess(id) = result
      repo.selectById(id) must beSome(entity)
    }
    "更新" in new AutoRollbackWithFixture {
      repo.selectById(1L) must beSome.which { c =>
        val entity = CompanyEntity(c.id, "updated_company", c.version)
        repo.save(entity) must_== UpdateSuccess(1)

        repo.selectById(entity.id) must beSome.which { u =>
          u.name must_== entity.name
          u.version must_== c.version + 1
        }
      }
    }
    "楽観ロックエラー" in new AutoRollbackWithFixture {
      repo.selectById(1L) must beSome.which { c =>
        val entity = CompanyEntity(c.id, "updated_company", c.version + 1L)
        val result = repo.save(entity)
        result must beAnInstanceOf[Failure]

        val Failure(t) = result
        t must beAnInstanceOf[OptimisticLockException]

        repo.selectById(entity.id) must beSome(CompanyEntity("test_company_1"))
      }
    }
  }
}
