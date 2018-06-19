package com.zaneli.escalade.api.entity

sealed abstract case class MemberEntity(name: String, company: CompanyEntity) extends EntityWithPK[MemberId]

object MemberEntity {
  def apply(name: String, company: CompanyEntity): MemberEntity = new MemberEntity(name, company){}

  def apply(
    _id: MemberId,
    name: String,
    company: CompanyEntity,
    _version: Long
  ): MemberEntity with HasId[MemberId] with HasVersion =
    new MemberEntity(name, company) with HasId[MemberId] with HasVersion {
      override lazy val id: MemberId = _id
      override lazy val version: Long = _version
    }
}
