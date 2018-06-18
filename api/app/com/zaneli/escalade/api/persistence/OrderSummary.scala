package com.zaneli.escalade.api.persistence

import java.time.LocalDateTime

import com.zaneli.escalade.api.entity.OrderStatus
import scalikejdbc._
import skinny.orm.{Alias, SkinnyCRUDMapper}
import skinny.orm.feature.OptimisticLockWithVersionFeature
import skinny.orm.feature.associations.HasManyAssociation

case class OrderSummary(
  id: Long,
  orderer: Long,
  status: OrderStatus,
  orderDate: LocalDateTime,
  settledDate: Option[LocalDateTime],
  lockVersion: Long,
  details: Seq[OrderDetail] = Nil
)

object OrderSummary extends SkinnyCRUDMapper[OrderSummary] with OptimisticLockWithVersionFeature[OrderSummary] {

  override lazy val tableName: String = "order_summaries"

  override lazy val defaultAlias: Alias[OrderSummary] = createAlias("os")

  override def extract(rs: WrappedResultSet, n: scalikejdbc.ResultName[OrderSummary]): OrderSummary = autoConstruct(rs, n, "details")

  lazy val detailsRef: HasManyAssociation[OrderSummary] = hasManyWithFk[OrderDetail](
    many = OrderDetail -> OrderDetail.defaultAlias,
    fk = "summaryId",
    on = (s, d) => sqls.eq(s.id, d.summaryId),
    merge = (summary, details) => summary.copy(details = details)
  )
}
