package com.zaneli.escalade.api.entity

import io.circe.{ Decoder, Encoder }
import scalikejdbc.Binders

case class CompanyId(value: Long)

object CompanyId {
  implicit val binders: Binders[CompanyId] = Binders.long.xmap(v => CompanyId(v), _.value)
  implicit val ord: Ordering[CompanyId] = Ordering.by(_.value)

  implicit val encode: Encoder[CompanyId] = Encoder[Long].contramap(_.value)
  implicit val decode: Decoder[CompanyId] = Decoder[Long].map(CompanyId(_))
}

case class ItemId(value: Long)

object ItemId {
  implicit val binders: Binders[ItemId] = Binders.long.xmap(v => ItemId(v), _.value)
  implicit val ord: Ordering[ItemId] = Ordering.by(_.value)

  implicit val encode: Encoder[ItemId] = Encoder[Long].contramap(_.value)
  implicit val decode: Decoder[ItemId] = Decoder[Long].map(ItemId(_))
}

case class MemberId(value: Long)

object MemberId {
  implicit val binders: Binders[MemberId] = Binders.long.xmap(v => MemberId(v), _.value)
  implicit val ord: Ordering[MemberId] = Ordering.by(_.value)

  implicit val encode: Encoder[MemberId] = Encoder[Long].contramap(_.value)
  implicit val decode: Decoder[MemberId] = Decoder[Long].map(MemberId(_))
}

case class OrderSummaryId(value: Long)

object OrderSummaryId {
  implicit val binders: Binders[OrderSummaryId] = Binders.long.xmap(v => OrderSummaryId(v), _.value)
  implicit val ord: Ordering[OrderSummaryId] = Ordering.by(_.value)

  implicit val encode: Encoder[OrderSummaryId] = Encoder[Long].contramap(_.value)
  implicit val decode: Decoder[OrderSummaryId] = Decoder[Long].map(OrderSummaryId(_))
}
