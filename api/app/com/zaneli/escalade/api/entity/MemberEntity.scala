package com.zaneli.escalade.api.entity

sealed abstract case class MemberEntity(name: String, company: CompanyEntity) extends Entity

object MemberEntity {
  def apply(name: String, company: CompanyEntity): MemberEntity = new MemberEntity(name, company){}

  def apply(_id: Long, name: String, company: CompanyEntity, _version: Long): MemberEntity with HasId[Long] with HasVersion =
    new MemberEntity(name, company) with HasId[Long] with HasVersion {
      override lazy val id: Long = _id
      override lazy val version: Long = _version
    }
}
