package com.zaneli.escalade.api.repository

import com.zaneli.escalade.api.entity.{HasId, HasVersion, ItemEntity, ItemId}
import com.zaneli.escalade.api.persistence.Item
import com.zaneli.escalade.api.repository.Result.UpdateSuccess
import com.zaneli.escalade.generator._
import scalikejdbc.DBSession

import scala.util.control.Exception

class ItemRepository extends InsertOrOptimisticLockUpdate[ItemEntity, ItemId] {

  def save(entity: ItemEntity)(implicit s: DBSession): Either[Throwable, Result[ItemId]] = {
    val nvs = autoNamedValues(entity, Item.column)
    insertOrUpdate(entity) { _ =>
      ItemId(Item.createWithNamedValues(nvs: _*))
    } { e =>
      Item.updateByIdAndVersion(e.id.value, e.version).withNamedValues(nvs: _*)
    }
  }

  def findById(id: ItemId)(implicit s: DBSession): Option[ItemEntity with HasId[ItemId] with HasVersion] = {
    Item.findById(id.value).map(c => ItemEntity(ItemId(c.id), c.name, c.price, c.lockVersion))
  }

  def deleteByIdAndVersion(id: ItemId, version: Long)(implicit s: DBSession): Either[Throwable, UpdateSuccess] = {
    Exception.nonFatalCatch.either {
      UpdateSuccess(Item.deleteByIdAndVersion(id.value, version))
    }
  }
}
