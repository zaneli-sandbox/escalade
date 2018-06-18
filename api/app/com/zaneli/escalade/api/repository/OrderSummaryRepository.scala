package com.zaneli.escalade.api.repository

import com.zaneli.escalade.api.entity.{HasId, HasVersion, OrderDetailEntity, OrderSummaryEntity}
import com.zaneli.escalade.api.persistence.OrderSummary
import scalikejdbc.DBSession

class OrderSummaryRepository {

  def save(entity: OrderSummaryEntity)(implicit s: DBSession): Result[Long] = {
    val column = OrderSummary.column
    insertOrOptimisticLockUpdate(entity) {
      OrderSummary.createWithNamedValues(
        column.orderer -> entity.orderer,
        column.status -> entity.status,
        column.orderDate -> entity.orderDate,
        column.settledDate -> entity.settledDate
      )
    } { e =>
      OrderSummary.updateByIdAndVersion(e.id, e.version).withNamedValues(
        column.orderer -> entity.orderer,
        column.status -> entity.status,
        column.orderDate -> entity.orderDate,
        column.settledDate -> entity.settledDate
      )
    }
  }

  def selectById(id: Long)(implicit s: DBSession): Option[OrderSummaryEntity with HasId[Long] with HasVersion] = {
    OrderSummary.joins(OrderSummary.detailsRef).findById(id).map { s =>
      val details = s.details.map { d =>
        OrderDetailEntity(d.summaryId, d.itemId, d.number, d.discountRate, d.price)
      }
      OrderSummaryEntity(s.id, s.orderer, s.status, s.orderDate, s.settledDate, details, s.lockVersion)
    }
  }
}
