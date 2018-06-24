package com.zaneli.escalade.api.entity

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.deriveDecoder

case class CompanyEntity(name: String) extends EntityWithPK[CompanyId]

object CompanyEntity {

  def apply(_id: CompanyId, name: String, _version: Long): CompanyEntity with HasId[CompanyId] with HasVersion =
    new CompanyEntity(name) with HasId[CompanyId] with HasVersion {
      override lazy val id: CompanyId = _id
      override lazy val version: Long = _version
    }

  implicit val encodeWith: Encoder[CompanyEntity with HasId[CompanyId] with HasVersion] =
    Encoder.forProduct3("id", "name", "version")(u =>
      (u.id, u.name, u.version)
    )
  implicit val decodeWith: Decoder[CompanyEntity with HasId[CompanyId] with HasVersion] =
    Decoder.forProduct3("id", "name", "version")(CompanyEntity.apply)

  implicit val decode: Decoder[CompanyEntity] = deriveDecoder[CompanyEntity]
}
