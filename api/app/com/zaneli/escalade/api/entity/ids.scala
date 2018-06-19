package com.zaneli.escalade.api.entity

import scalikejdbc.Binders

case class CompanyId(value: Long)

object CompanyId {
  implicit val binders: Binders[CompanyId] = Binders.long.xmap(v => CompanyId(v), _.value)
  implicit val ord: Ordering[CompanyId] = Ordering.by(_.value)
}

case class ItemId(value: Long)

object ItemId {
  implicit val binders: Binders[ItemId] = Binders.long.xmap(v => ItemId(v), _.value)
  implicit val ord: Ordering[ItemId] = Ordering.by(_.value)
}

case class MemberId(value: Long)

object MemberId {
  implicit val binders: Binders[MemberId] = Binders.long.xmap(v => MemberId(v), _.value)
  implicit val ord: Ordering[MemberId] = Ordering.by(_.value)
}

case class OrderSummaryId(value: Long)

object OrderSummaryId {
  implicit val binders: Binders[OrderSummaryId] = Binders.long.xmap(v => OrderSummaryId(v), _.value)
  implicit val ord: Ordering[OrderSummaryId] = Ordering.by(_.value)
}
