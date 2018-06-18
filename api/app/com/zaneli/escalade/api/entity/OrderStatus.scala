package com.zaneli.escalade.api.entity

import scalikejdbc.Binders

sealed abstract class OrderStatus(private val value: String)

object OrderStatus {
  case object Unsettled extends OrderStatus("unsettled")
  case object Settled extends OrderStatus("settled")

  val values: Seq[OrderStatus] = Seq(Unsettled, Settled)

  implicit val binders: Binders[OrderStatus] = Binders.string.xmap(
    v => OrderStatus.values.find(_.value == v).getOrElse(throw new IllegalArgumentException(v)),
    _.value
  )
}
