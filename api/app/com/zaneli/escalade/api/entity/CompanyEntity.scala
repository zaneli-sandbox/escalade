package com.zaneli.escalade.api.entity

sealed abstract case class CompanyEntity(name: String) extends Entity

object CompanyEntity {
  def apply(name: String): CompanyEntity = new CompanyEntity(name){}

  def apply(_id: Long, name: String, _version: Long): CompanyEntity with HasId[Long] with HasVersion =
    new CompanyEntity(name) with HasId[Long] with HasVersion {
      override lazy val id: Long = _id
      override lazy val version: Long = _version
    }
}
