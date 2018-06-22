package com.zaneli.escalade.api.entity

trait Entity

trait EntityWithPK[ID] extends Entity

trait HasId[ID] { self: EntityWithPK[ID] =>
  def id: ID
}

trait HasVersion { self: Entity =>
  def version: Long
}
