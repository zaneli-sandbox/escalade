package com.zaneli.escalade.api.entity

case class ItemEntity(name: String, price: Price) extends EntityWithPK[ItemId]

object ItemEntity {

  def apply(_id: ItemId, name: String, price: Price, _version: Long): ItemEntity with HasId[ItemId] with HasVersion =
    new ItemEntity(name, price) with HasId[ItemId] with HasVersion {
      override lazy val id: ItemId = _id
      override lazy val version: Long = _version
    }
}
