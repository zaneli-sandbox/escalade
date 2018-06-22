package com.zaneli.escalade.api.entity

import scalikejdbc.Binders

sealed case class Price(private val value: BigDecimal) {
  override def toString: String = f"$value%2.2f"

  def *(rate: Rate): Price = {
    map(_  * (rate.percentage / 100))
  }

  def map(f: BigDecimal => BigDecimal): Price = {
    Price(f(value))
  }
}

object Price {
  def apply(value: BigDecimal): Price = {
    new Price(value.setScale(2, BigDecimal.RoundingMode.HALF_UP)){}
  }

  implicit val binders: Binders[Price] = Binders.bigDecimal.xmap(v => apply(v), _.value)
}
