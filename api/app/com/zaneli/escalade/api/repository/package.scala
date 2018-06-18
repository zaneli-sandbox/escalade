package com.zaneli.escalade.api

import com.zaneli.escalade.api.entity.{Entity, HasId, HasVersion}

import scala.util.control.NonFatal

package object repository {
  type OptimisticLockUpdateParam = HasId[Long] with HasVersion

  private[repository] def insertOrOptimisticLockUpdate[A <: Entity, B](
    entity: A)(
    insert: => B)(
    update: OptimisticLockUpdateParam => Int
  ): Result[B] = try {
    entity match {
      case e: OptimisticLockUpdateParam =>
        val count = update(e)
        UpdateSuccess(count)
      case _ =>
        val id = insert
        InsertSuccess(id)
    }
  } catch {
    case NonFatal(t) => Failure(t)
  }
}
