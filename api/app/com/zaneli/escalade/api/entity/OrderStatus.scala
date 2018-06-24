package com.zaneli.escalade.api.entity

import io.circe.{Decoder, Encoder}
import scalikejdbc.Binders

import scala.util.Try

sealed abstract class OrderStatus(private val value: String)

object OrderStatus {
  case object Unsettled extends OrderStatus("unsettled")
  case object Settled extends OrderStatus("settled")

  val values: Seq[OrderStatus] = Seq(Unsettled, Settled)

  implicit val binders: Binders[OrderStatus] = Binders.string.xmap(
    v => OrderStatus.values.find(_.value == v).getOrElse(throw new IllegalArgumentException(v)),
    _.value
  )

  implicit val encode: Encoder[OrderStatus] = Encoder[String].contramap(_.value)
  implicit val decode: Decoder[OrderStatus] = Decoder[String].emapTry { v =>
    Try {
      OrderStatus.values.find(_.value == v).getOrElse(throw new IllegalArgumentException(v))
    }
  }
}
