package com.zaneli.escalade.api.repository

import com.zaneli.escalade.api.entity._
import db.DBSetup
import org.specs2.mutable.{After, Specification}
import scalikejdbc._
import scalikejdbc.specs2.mutable.AutoRollback

class OrderDetailRepositorySpec extends Specification with DBSetup {

  private[this] val repo = new OrderDetailRepository()

  trait AutoRollbackWithFixture extends AutoRollback with After {
    override def fixture(implicit session: DBSession): Unit = DBSetup.fixture
  }

  sequential

  "OrderDetailRepository.selectBySummaryId" should {
    "存在するSummaryIDを指定" in new AutoRollbackWithFixture {
      val id = OrderSummaryId(1L)
      repo.selectBySummaryId(id).sortBy(_.itemId) must_== List(
        OrderDetailEntity(id, ItemId(1L), 10, Rate(10), Price(900)),
        OrderDetailEntity(id, ItemId(2L), 5, Rate(3), Price(727.5))
      )
    }
    "存在しないSummaryIDを指定" in new AutoRollbackWithFixture {
      repo.selectBySummaryId(OrderSummaryId(-1L)).sortBy(_.itemId) must beEmpty
    }
  }

  "OrderDetailRepository.save()" should {
    "新規作成" in new AutoRollbackWithFixture {
      val id = OrderSummaryId(2L)
      val initial = OrderDetailEntity(id, ItemId(1L), 5, Rate(7), Price(465))
      repo.selectBySummaryId(id).sortBy(_.itemId) must_== List(initial)

      val additinal = OrderDetailEntity(id, ItemId(2L), 5, Rate(3), Price(727.5))
      val result = repo.save(additinal)
      result must beRight(InsertSuccess(()))

      repo.selectBySummaryId(id).sortBy(_.itemId) must_== List(initial, additinal)
    }
    "ユニーク制約違反" in new AutoRollbackWithFixture {
      val id = OrderSummaryId(1L)
      val initials = List(
        OrderDetailEntity(id, ItemId(1L), 10, Rate(10), Price(900)),
        OrderDetailEntity(id, ItemId(2L), 5, Rate(3), Price(727.5))
      )
      repo.selectBySummaryId(id).sortBy(_.itemId) must_== initials

      val entity = OrderDetailEntity(id, ItemId(1L), 5, Rate(3), Price(727.5))
      val result = repo.save(entity)
      result must beLeft

      repo.selectBySummaryId(id).sortBy(_.itemId) must_== initials
    }
  }
}
