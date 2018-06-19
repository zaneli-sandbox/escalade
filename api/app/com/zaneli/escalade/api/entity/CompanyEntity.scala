package com.zaneli.escalade.api.entity

sealed abstract case class CompanyEntity(name: String) extends EntityWithPK[CompanyId]

object CompanyEntity {
  def apply(name: String): CompanyEntity = new CompanyEntity(name){}

  def apply(_id: CompanyId, name: String, _version: Long): CompanyEntity with HasId[CompanyId] with HasVersion =
    new CompanyEntity(name) with HasId[CompanyId] with HasVersion {
      override lazy val id: CompanyId = _id
      override lazy val version: Long = _version
    }
}
