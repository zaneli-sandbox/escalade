package com.zaneli.escalade.api.repository

import com.zaneli.escalade.api.entity._
import com.zaneli.escalade.api.repository.Result.{InsertSuccess, UpdateSuccess}
import db.DBSetup
import org.specs2.mutable.{After, Specification}
import scalikejdbc._
import scalikejdbc.specs2.mutable.AutoRollback
import skinny.orm.exception.OptimisticLockException

class ItemRepositorySpec extends Specification with DBSetup {

  private[this] val repo = new ItemRepository()

  trait AutoRollbackWithFixture extends AutoRollback with After {
    override def fixture(implicit session: DBSession): Unit = DBSetup.fixture
  }

  sequential

  "ItemRepository.selectById" should {
    "存在するIDを指定" in new AutoRollbackWithFixture {
      val id = ItemId(1L)
      repo.findById(id) must beSome.which { i =>
        i.id must_== id
        i.name must_== "test_item_1"
        i.price must_== Price(100)
        i.version must_== 1L
      }
    }
    "存在しないIDを指定" in new AutoRollbackWithFixture {
      repo.findById(ItemId(-1L)) must beNone
    }
    "論理削除されたIDを指定" in new AutoRollbackWithFixture {
      repo.findById(ItemId(3L)) must beNone
    }
  }

  "ItemRepository.save()" should {
    "新規作成" in new AutoRollbackWithFixture {
      val entity = ItemEntity("inserted_item", Price(10))
      repo.save(entity) must beRight.which {
        case InsertSuccess(id) => repo.findById(id) must beSome(entity)
        case _ => ko
      }
    }
    "更新" in new AutoRollbackWithFixture {
      repo.findById(ItemId(1L)) must beSome.which { c =>
        val entity = ItemEntity(c.id, "updated_item", c.price.map(_ + 1), c.version)
        repo.save(entity) must beRight(UpdateSuccess(1))

        repo.findById(entity.id) must beSome.which { u =>
          u.name must_== entity.name
          u.price must_== Price(101)
          u.version must_== c.version + 1
        }
      }
    }
    "楽観ロックエラー" in new AutoRollbackWithFixture {
      repo.findById(ItemId(1L)) must beSome.which { c =>
        val entity = ItemEntity(c.id, "updated_item", c.price.map(_ + 1), c.version + 1L)
        repo.save(entity) must beLeft.which(_ must beAnInstanceOf[OptimisticLockException])

        repo.findById(entity.id) must beSome(ItemEntity("test_item_1", Price(100)))
      }
    }
  }

  "ItemRepository.deleteById" should {
    "存在するIDを指定" in new AutoRollbackWithFixture {
      val id = ItemId(1L)
      val version = 1L
      repo.deleteByIdAndVersion(id, version) must beRight(UpdateSuccess(1))
      repo.findById(id) must beNone
      findIgnoreDeleteFlag(id) must beSome.which { case (item, deleted) =>
        item.id must_== id
        item.version must_== version + 1L
        deleted must beTrue
      }
    }
    "存在しないIDを指定" in new AutoRollbackWithFixture {
      val id = ItemId(-1L)
      val version = 1L
      repo.findById(id) must beNone
      repo.deleteByIdAndVersion(id, version) must beLeft.which(_ must beAnInstanceOf[OptimisticLockException])
      repo.findById(id) must beNone
    }
    "楽観ロックエラー" in new AutoRollbackWithFixture {
      val id = ItemId(1L)
      val version = 2L
      repo.findById(id) must beSome
      repo.deleteByIdAndVersion(id, version) must beLeft.which(_ must beAnInstanceOf[OptimisticLockException])
      repo.findById(id) must beSome
    }
  }

  private[this] def findIgnoreDeleteFlag(
    id: ItemId)(implicit s: DBSession
  ): Option[(ItemEntity with HasId[ItemId] with HasVersion, Boolean)] = {
    sql"""
         |SELECT `id`, `name`, `price`, `lock_version`, `is_deleted` FROM `items` WHERE id = ${id.value}
       """.stripMargin.map { rs =>
      val item = ItemEntity(
        rs.get("id"),
        rs.get("name"),
        rs.get("price"),
        rs.get("lock_version")
      )
      val isDeleted = rs.boolean("is_deleted")
      (item, isDeleted)
    }.single.apply()
  }
}
