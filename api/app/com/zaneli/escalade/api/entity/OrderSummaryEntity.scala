package com.zaneli.escalade.api.entity

import java.time.LocalDateTime

case class OrderSummaryEntity(
  orderer: MemberId,
  status: OrderStatus,
  orderDate: LocalDateTime,
  settledDate: Option[LocalDateTime],
  details: Seq[OrderDetailEntity]) extends EntityWithPK[OrderSummaryId]

object OrderSummaryEntity {

  def apply(
    _id: OrderSummaryId,
    orderer: MemberId,
    status: OrderStatus,
    orderDate: LocalDateTime,
    settledDate: Option[LocalDateTime],
    details: Seq[OrderDetailEntity],
    _version: Long): OrderSummaryEntity with HasId[OrderSummaryId] with HasVersion = new OrderSummaryEntity(
    orderer, status, orderDate, settledDate, details) with HasId[OrderSummaryId] with HasVersion {
    override lazy val id: OrderSummaryId = _id
    override lazy val version: Long = _version
  }
}
