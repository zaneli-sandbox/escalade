package com.zaneli.escalade.api.repository

import com.zaneli.escalade.api.entity.{ItemId, OrderDetailEntity, OrderSummaryId}
import com.zaneli.escalade.api.persistence.OrderDetail
import com.zaneli.escalade.generator._
import scalikejdbc.{DBSession, sqls}

import scala.util.control.Exception

class OrderDetailRepository {

  def save(entity: OrderDetailEntity)(implicit s: DBSession): Either[Throwable, Result[Unit]] = {
    val nvs = autoNamedValues(entity, OrderDetail.column)
    Exception.nonFatalCatch.either {
      OrderDetail.createWithNamedValues(nvs: _*)
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
