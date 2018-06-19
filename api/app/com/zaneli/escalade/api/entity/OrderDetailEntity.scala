package com.zaneli.escalade.api.entity

case class OrderDetailEntity(
  summaryId: OrderSummaryId, itemId: ItemId, number: Int, discountRate: Rate, price: Price
) extends Entity
