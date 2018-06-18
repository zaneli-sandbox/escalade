package com.zaneli.escalade.api.repository

import com.zaneli.escalade.api.entity.{OrderDetailEntity, Price, Rate}
import db.DBSetup
import org.specs2.mutable.{After, Specification}
import scalikejdbc._
import scalikejdbc.specs2.mutable.AutoRollback

class OrderDetailRepositorySpec extends Specification with DBSetup {

  private[this] val repo = new OrderDetailRepository()

  trait AutoRollbackWithFixture extends AutoRollback with After {
    override def fixture(implicit session: DBSession): Unit = {
      sql"""
           |INSERT INTO companies (`id`, `name`) VALUES (1, 'test_company_1'), (2, 'test_company_2')
       """.stripMargin.update.apply()
      sql"""
           |INSERT INTO members (`id`, `name`, `company_id`) VALUES (1, 'test_member_1_1', 1), (2, 'test_member_1_2', 1)
       """.stripMargin.update.apply()
      sql"""
           |INSERT INTO items
           |  (`id`, `name`, `price`)
           |VALUES
           |  (1, 'test_item_1', 100), (2, 'test_item_2', 150)
       """.stripMargin.update.apply()
      sql"""
           |INSERT INTO order_summaries
           |  (`id`, `orderer`, `status`, `order_date`, `settled_date`)
           |VALUES
           |  (1, 1, 'unsettled', NOW(), NULL), (2, 2, 'settled', NOW(), NOW())
       """.stripMargin.update.apply()
      sql"""
           |INSERT INTO order_details
           |  (`summary_id`, `item_id`, `number`, `discount_rate`, `price`)
           |VALUES
           |  (1, 1, 10, 10, 900.00), (1, 2, 5, 3, 727.50), (2, 1, 5, 7, 465.00)
       """.stripMargin.update.apply()
      ()
    }
  }

  sequential

  "OrderDetailRepository.selectBySummaryId" should {
    "存在するSummaryIDを指定" in new AutoRollbackWithFixture {
      repo.selectBySummaryId(1).sortBy(_.itemId) must_== List(
        OrderDetailEntity(1, 1, 10, Rate(10), Price(900)),
        OrderDetailEntity(1, 2, 5, Rate(3), Price(727.5))
      )
    }
    "存在しないSummaryIDを指定" in new AutoRollbackWithFixture {
      repo.selectBySummaryId(-1).sortBy(_.itemId) must beEmpty
    }
  }

  "OrderDetailRepository.save()" should {
    "新規作成" in new AutoRollbackWithFixture {
      repo.selectBySummaryId(2).sortBy(_.itemId) must_== List(
        OrderDetailEntity(2, 1, 5, Rate(7), Price(465))
      )

      val entity = OrderDetailEntity(2, 2, 5, Rate(3), Price(727.5))
      val result = repo.save(entity)
      result must beAnInstanceOf[InsertSuccess[Unit]]

      repo.selectBySummaryId(2).sortBy(_.itemId) must_== List(
        OrderDetailEntity(2, 1, 5, Rate(7), Price(465)),
        entity
      )
    }
    "ユニーク制約違反" in new AutoRollbackWithFixture {
      repo.selectBySummaryId(1).sortBy(_.itemId) must_== List(
        OrderDetailEntity(1, 1, 10, Rate(10), Price(900)),
        OrderDetailEntity(1, 2, 5, Rate(3), Price(727.5))
      )

      val entity = OrderDetailEntity(1, 1, 5, Rate(3), Price(727.5))
      val result = repo.save(entity)
      result must beAnInstanceOf[Failure]

      repo.selectBySummaryId(1).sortBy(_.itemId) must_== List(
        OrderDetailEntity(1, 1, 10, Rate(10), Price(900)),
        OrderDetailEntity(1, 2, 5, Rate(3), Price(727.5))
      )
    }
  }
}
