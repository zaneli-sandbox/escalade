package com.zaneli.escalade.api.entity

import scalikejdbc.Binders

case class Price(value: BigDecimal) {
  override def toString: String = f"$value%2.2f"

  def *(rate: Rate): Price = {
    Price(value * (rate.percentage / 100))
  }
}

object Price {
  implicit val binders: Binders[Price] = Binders.bigDecimal.xmap(v => apply(v), _.value)
}
