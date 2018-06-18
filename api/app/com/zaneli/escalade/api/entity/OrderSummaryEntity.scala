package com.zaneli.escalade.api.entity

import java.time.LocalDateTime

sealed abstract case class OrderSummaryEntity(
  orderer: Long,
  status: OrderStatus,
  orderDate: LocalDateTime,
  settledDate: Option[LocalDateTime],
  details: Seq[OrderDetailEntity]
) extends Entity

object OrderSummaryEntity {
  def apply(
    orderer: Long,
    status: OrderStatus,
    orderDate: LocalDateTime,
    settledDate: Option[LocalDateTime],
    details: Seq[OrderDetailEntity]
  ): OrderSummaryEntity = new OrderSummaryEntity(
    orderer, status, orderDate, settledDate, details
  ){}

  def apply(
    _id: Long,
    orderer: Long,
    status: OrderStatus,
    orderDate: LocalDateTime,
    settledDate: Option[LocalDateTime],
    details: Seq[OrderDetailEntity],
    _version: Long
  ): OrderSummaryEntity with HasId[Long] with HasVersion = new OrderSummaryEntity(
    orderer, status, orderDate, settledDate, details
  ) with HasId[Long] with HasVersion {
    override lazy val id: Long = _id
    override lazy val version: Long = _version
  }
}
