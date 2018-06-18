package com.zaneli.escalade.api.repository

import java.time.LocalDateTime

import com.zaneli.escalade.api.entity._
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
      repo.selectById(1L) must beSome.which { s =>
        s.id must_== 1L
        s.orderer must_== 1L
        s.status must_== OrderStatus.Unsettled
        s.settledDate must beNone
        s.details.sortBy(_.itemId) must_== Seq(
          OrderDetailEntity(1, 1, 10, Rate(10), Price(900)),
          OrderDetailEntity(1, 2, 5, Rate(3), Price(727.5))
        )
      }
    }
    "存在しないIDを指定" in new AutoRollbackWithFixture {
      repo.selectById(-1L) must beNone
    }
  }

  "OrderSummaryRepository.save()" should {
    "新規作成" in new AutoRollbackWithFixture {
      val now = LocalDateTime.now()
      val entity = OrderSummaryEntity(1L, OrderStatus.Settled, now, now.some, Nil)
      val result = repo.save(entity)
      result must beAnInstanceOf[InsertSuccess[Long]]

      val InsertSuccess(id: Long) = result
      repo.selectById(id) must beSome(entity)
    }
    "更新" in new AutoRollbackWithFixture {
      val now = LocalDateTime.now()
      repo.selectById(1L) must beSome.which { s =>
        val entity = OrderSummaryEntity(s.id, 2L, OrderStatus.Settled, now, now.some, Nil, s.version)
        repo.save(entity) must_== UpdateSuccess(1)

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
      repo.selectById(1L) must beSome.which { s =>
        val entity = OrderSummaryEntity(s.id, 2L, OrderStatus.Settled, now, now.some, Nil, s.version + 1)
        val result = repo.save(entity)
        result must beAnInstanceOf[Failure]

        val Failure(t) = result
        t must beAnInstanceOf[OptimisticLockException]

        repo.selectById(entity.id) must beSome.which { u =>
          u.orderer must_== 1L
          u.status must_== OrderStatus.Unsettled
          u.settledDate must beNone
        }
      }
    }
  }
}
