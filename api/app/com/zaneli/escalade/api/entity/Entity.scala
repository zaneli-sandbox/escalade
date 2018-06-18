package com.zaneli.escalade.api.entity

trait Entity

trait HasId[A] { self: Entity =>
  def id: A
}

trait HasVersion { self: Entity =>
  def version: Long
}
