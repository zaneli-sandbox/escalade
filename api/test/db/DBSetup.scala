package db

import scalikejdbc._
import scalikejdbc.config.DBs

import scala.io.Source

trait DBSetup {
  DBSetup.initialize()
}

object DBSetup {
  private[this] var initialized = false

  def initialize(): Unit = initialized.synchronized {
    if (!initialized) {
      DBs.setupAll()
      val ddl = Source.fromFile("../rdb/ddl.sql").getLines().mkString
      DB.autoCommit { implicit s =>
        SQL(ddl).executeUpdate().apply()
      }
      initialized = true
    }
  }

  def fixture(implicit s: DBSession): Unit = {
    sql"""
         |INSERT INTO companies (`id`, `name`) VALUES (1, 'test_company_1'), (2, 'test_company_2')
       """.stripMargin.update.apply()
    sql"""
         |INSERT INTO members (`id`, `name`, `company_id`) VALUES (1, 'test_member_1_1', 1), (2, 'test_member_1_2', 1)
       """.stripMargin.update.apply()
    sql"""
         |INSERT INTO items
         |  (`id`, `name`, `price`, `is_deleted`)
         |VALUES
         |  (1, 'test_item_1', 100, false), (2, 'test_item_2', 150, false), (3, 'test_item_3', 100, true)
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
