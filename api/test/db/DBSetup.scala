package db

import scalikejdbc.{DB, SQL}
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
}
