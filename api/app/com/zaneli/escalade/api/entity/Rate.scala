package com.zaneli.escalade.api.entity

import scalikejdbc.Binders

sealed abstract case class Rate(private[entity] val percentage: BigDecimal) {
  override def toString: String = f"$percentage%2.2f"

  lazy val reciprocal: Rate = Rate(100 - percentage)

  def *(price: Price): Price = {
    price.map(_ * (percentage / 100))
  }
}

object Rate {
  def apply(percentage: BigDecimal): Rate = {
    new Rate(percentage.setScale(2, BigDecimal.RoundingMode.HALF_UP)) {}
  }

  implicit val binders: Binders[Rate] = Binders.bigDecimal.xmap(v => apply(v), _.percentage)
}
