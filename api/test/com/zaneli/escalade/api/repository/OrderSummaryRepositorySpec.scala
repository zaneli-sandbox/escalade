package com.zaneli.escalade.api.repository

import java.time.LocalDateTime

import com.zaneli.escalade.api.entity._
import com.zaneli.escalade.api.repository.Result.{InsertSuccess, UpdateSuccess}
import db.DBSetup
import org.specs2.mutable.{After, Specification}
import scalaz.syntax.std.option._
import scalikejdbc._
import scalikejdbc.specs2.mutable.AutoRollback
import skinny.orm.exception.OptimisticLockException

class OrderSummaryRepositorySpec extends Specification with DBSetup {

  private[this] val repo = new OrderSummaryRepository()

  trait AutoRollbackWithFixture extends AutoRollback with After {
    override def fixture(implicit session: DBSession): Unit = DBSetup.fixture
  }

  sequential

  "OrderSummaryRepository.selectBySummaryId" should {
    "存在するIDを指定" in new AutoRollbackWithFixture {
      val id = OrderSummaryId(1L)
      repo.selectById(id) must beSome.which { s =>
        s.id must_== id
        s.orderer must_== MemberId(1L)
        s.status must_== OrderStatus.Unsettled
        s.settledDate must beNone
        s.details.sortBy(_.itemId) must_== Seq(
          OrderDetailEntity(id, ItemId(1L), 10, Rate(10), Price(900)),
          OrderDetailEntity(id, ItemId(2L), 5, Rate(3), Price(727.5))
        )
      }
    }
    "存在しないIDを指定" in new AutoRollbackWithFixture {
      repo.selectById(OrderSummaryId(-1L)) must beNone
    }
  }

  "OrderSummaryRepository.save()" should {
    "新規作成" in new AutoRollbackWithFixture {
      val now = LocalDateTime.now()
      val entity = OrderSummaryEntity(MemberId(1L), OrderStatus.Settled, now, now.some, Nil)
      val result = repo.save(entity)
      result must beRight.which {
        case InsertSuccess(id) => repo.selectById(id) must beSome(entity)
        case _ => ko
      }
    }
    "更新" in new AutoRollbackWithFixture {
      val now = LocalDateTime.now()
      repo.selectById(OrderSummaryId(1L)) must beSome.which { s =>
        val entity = OrderSummaryEntity(s.id, MemberId(2L), OrderStatus.Settled, now, now.some, Nil, s.version)
        val result = repo.save(entity)
        result must beRight(UpdateSuccess(1))

        repo.selectById(entity.id) must beSome.which { u =>
          u.orderDate must_== entity.orderDate
          u.status must_== entity.status
          u.orderDate must_== entity.orderDate
          u.settledDate must_== entity.settledDate
        }
      }
    }
    "楽観ロックエラー" in new AutoRollbackWithFixture {
      val now = LocalDateTime.now()
      repo.selectById(OrderSummaryId(1L)) must beSome.which { s =>
        val entity = OrderSummaryEntity(s.id, MemberId(2L), OrderStatus.Settled, now, now.some, Nil, s.version + 1)
        val result = repo.save(entity)
        result must beLeft.which(_ must beAnInstanceOf[OptimisticLockException])

        repo.selectById(entity.id) must beSome.which { u =>
          u.orderer must_== MemberId(1L)
          u.status must_== OrderStatus.Unsettled
          u.settledDate must beNone
        }
      }
    }
  }
}
