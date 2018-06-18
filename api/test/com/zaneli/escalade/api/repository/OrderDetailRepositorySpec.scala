package com.zaneli.escalade.api.repository

import com.zaneli.escalade.api.entity.{OrderDetailEntity, Price, Rate}
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
      repo.selectBySummaryId(1L).sortBy(_.itemId) must_== List(
        OrderDetailEntity(1, 1, 10, Rate(10), Price(900)),
        OrderDetailEntity(1, 2, 5, Rate(3), Price(727.5))
      )
    }
    "存在しないSummaryIDを指定" in new AutoRollbackWithFixture {
      repo.selectBySummaryId(-1L).sortBy(_.itemId) must beEmpty
    }
  }

  "OrderDetailRepository.save()" should {
    "新規作成" in new AutoRollbackWithFixture {
      repo.selectBySummaryId(2L).sortBy(_.itemId) must_== List(
        OrderDetailEntity(2L, 1L, 5, Rate(7), Price(465))
      )

      val entity = OrderDetailEntity(2L, 2L, 5, Rate(3), Price(727.5))
      val result = repo.save(entity)
      result must beAnInstanceOf[InsertSuccess[Unit]]

      repo.selectBySummaryId(2L).sortBy(_.itemId) must_== List(
        OrderDetailEntity(2L, 1L, 5, Rate(7), Price(465)),
        entity
      )
    }
    "ユニーク制約違反" in new AutoRollbackWithFixture {
      repo.selectBySummaryId(1L).sortBy(_.itemId) must_== List(
        OrderDetailEntity(1L, 1L, 10, Rate(10), Price(900)),
        OrderDetailEntity(1L, 2L, 5, Rate(3), Price(727.5))
      )

      val entity = OrderDetailEntity(1L, 1L, 5, Rate(3), Price(727.5))
      val result = repo.save(entity)
      result must beAnInstanceOf[Failure]

      repo.selectBySummaryId(1L).sortBy(_.itemId) must_== List(
        OrderDetailEntity(1L, 1L, 10, Rate(10), Price(900)),
        OrderDetailEntity(1L, 2L, 5, Rate(3), Price(727.5))
      )
    }
  }
}
