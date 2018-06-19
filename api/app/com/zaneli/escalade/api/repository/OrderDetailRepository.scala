package com.zaneli.escalade.api.repository

import com.zaneli.escalade.api.entity.{ItemId, OrderDetailEntity, OrderSummaryId}
import com.zaneli.escalade.api.persistence.OrderDetail
import scalikejdbc._

import scala.util.control.Exception

class OrderDetailRepository {

  def save(entity: OrderDetailEntity)(implicit s: DBSession): Either[Throwable, Result[Unit]] = {
    val column = OrderDetail.column
    Exception.nonFatalCatch.either {
      OrderDetail.createWithNamedValues(
        column.summaryId -> entity.summaryId,
        column.itemId -> entity.itemId,
        column.number -> entity.number,
        column.discountRate -> entity.discountRate,
        column.price -> entity.price
      )
      InsertSuccess(())
    }
  }

  def selectBySummaryId(summaryId: OrderSummaryId)(implicit s: DBSession): List[OrderDetailEntity] = {
    val column = OrderDetail.column
    OrderDetail.findAllBy(sqls.eq(column.summaryId, summaryId)).map { d =>
      OrderDetailEntity(OrderSummaryId(d.summaryId), ItemId(d.itemId), d.number, d.discountRate, d.price)
    }
  }
}
