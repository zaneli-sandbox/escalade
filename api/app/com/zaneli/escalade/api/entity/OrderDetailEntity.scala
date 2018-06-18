package com.zaneli.escalade.api.entity

case class OrderDetailEntity(
  summaryId: Long, itemId: Long, number: Int, discountRate: Rate, price: Price
) extends Entity
