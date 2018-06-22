package com.zaneli.escalade.api.repository

import com.zaneli.escalade.api.entity._
import com.zaneli.escalade.api.persistence.OrderSummary
import com.zaneli.escalade.generator._
import scalikejdbc.DBSession

class OrderSummaryRepository extends InsertOrOptimisticLockUpdate[OrderSummaryEntity, OrderSummaryId] {

  def save(entity: OrderSummaryEntity)(implicit s: DBSession): Either[Throwable, Result[OrderSummaryId]] = {
    val nvs = autoNamedValues(entity, OrderSummary.column, "details")
    insertOrUpdate(entity) { _ =>
      val id = OrderSummary.createWithNamedValues(nvs: _*)
      OrderSummaryId(id)
    } { e =>
      OrderSummary.updateByIdAndVersion(e.id.value, e.version).withNamedValues(nvs: _*)
    }
  }

  def selectById(id: OrderSummaryId)(implicit s: DBSession): Option[OrderSummaryEntity with HasId[OrderSummaryId] with HasVersion] = {
    OrderSummary.joins(OrderSummary.detailsRef).findById(id.value).map { s =>
      val details = s.details.map { d =>
        OrderDetailEntity(OrderSummaryId(d.summaryId), ItemId(d.itemId), d.number, d.discountRate, d.price)
      }
      OrderSummaryEntity(OrderSummaryId(s.id), MemberId(s.orderer), s.status, s.orderDate, s.settledDate, details, s.lockVersion)
    }
  }
}
