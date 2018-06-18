package com.zaneli.escalade.api.repository

import com.zaneli.escalade.api.entity.OrderDetailEntity
import com.zaneli.escalade.api.persistence.OrderDetail
import scalikejdbc._

import scala.util.control.NonFatal

class OrderDetailRepository {

  def save(entity: OrderDetailEntity)(implicit s: DBSession): Result[Unit] = {
    val column = OrderDetail.column
    try {
      OrderDetail.createWithNamedValues(
        column.summaryId -> entity.summaryId,
        column.itemId -> entity.itemId,
        column.number -> entity.number,
        column.discountRate -> entity.discountRate,
        column.price -> entity.price
      )
      InsertSuccess(())
    } catch {
      case NonFatal(t) => Failure(t)
    }
  }

  def selectBySummaryId(summaryId: Long)(implicit s: DBSession): List[OrderDetailEntity] = {
    val column = OrderDetail.column
    OrderDetail.findAllBy(sqls.eq(column.summaryId, summaryId)).map { d =>
      OrderDetailEntity(d.summaryId, d.itemId, d.number, d.discountRate, d.price)
    }
  }
}
