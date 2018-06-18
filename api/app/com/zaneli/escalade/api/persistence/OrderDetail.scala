package com.zaneli.escalade.api.persistence

import com.zaneli.escalade.api.entity.{Price, Rate}
import scalikejdbc.{WrappedResultSet, autoConstruct}
import skinny.orm.{Alias, SkinnyNoIdCRUDMapper}

case class OrderDetail(
  summaryId: Long,
  itemId: Long,
  number: Int,
  discountRate: Rate,
  price: Price
)

object OrderDetail extends SkinnyNoIdCRUDMapper[OrderDetail] {

  override lazy val tableName: String = "order_details"

  override lazy val defaultAlias: Alias[OrderDetail] = createAlias("od")

  override def extract(rs: WrappedResultSet, n: scalikejdbc.ResultName[OrderDetail]): OrderDetail = autoConstruct(rs, n)
}
