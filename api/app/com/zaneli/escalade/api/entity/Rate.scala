package com.zaneli.escalade.api.entity

import scalikejdbc.Binders

sealed abstract case class Rate(private[entity] val percentage: BigDecimal) {
  override def toString: String = f"$percentage%2.2f"

  def *(price: Price): Price = {
    Price(price.value * (percentage / 100))
  }
}

object Rate {
  def apply(percentage: BigDecimal): Rate = new Rate(percentage){}

  implicit val binders: Binders[Rate] = Binders.bigDecimal.xmap(v => apply(v), _.percentage)
}
