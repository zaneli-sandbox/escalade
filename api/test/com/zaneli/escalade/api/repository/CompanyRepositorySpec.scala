package com.zaneli.escalade.api.repository

import com.zaneli.escalade.api.entity.{CompanyEntity, CompanyId}
import com.zaneli.escalade.api.repository.Result.{InsertSuccess, UpdateSuccess}
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
      val id = CompanyId(1L)
      repo.findById(id) must beSome.which { c =>
        c.id must_== id
        c.name must_== "test_company_1"
        c.version must_== 1L
      }
    }
    "存在しないIDを指定" in new AutoRollbackWithFixture {
      repo.findById(CompanyId(-1L)) must beNone
    }
  }

  "CompanyRepository.save()" should {
    "新規作成" in new AutoRollbackWithFixture {
      val entity = CompanyEntity("inserted_company")
      repo.save(entity) must beRight.which {
        case InsertSuccess(id) => repo.findById(id) must beSome(entity)
        case _ => ko
      }
    }
    "更新" in new AutoRollbackWithFixture {
      repo.findById(CompanyId(1L)) must beSome.which { c =>
        val entity = CompanyEntity(c.id, "updated_company", c.version)
        repo.save(entity) must beRight(UpdateSuccess(1))

        repo.findById(entity.id) must beSome.which { u =>
          u.name must_== entity.name
          u.version must_== c.version + 1
        }
      }
    }
    "楽観ロックエラー" in new AutoRollbackWithFixture {
      repo.findById(CompanyId(1L)) must beSome.which { c =>
        val entity = CompanyEntity(c.id, "updated_company", c.version + 1L)
        repo.save(entity) must beLeft.which(_ must beAnInstanceOf[OptimisticLockException])

        repo.findById(entity.id) must beSome(CompanyEntity("test_company_1"))
      }
    }
  }
}
