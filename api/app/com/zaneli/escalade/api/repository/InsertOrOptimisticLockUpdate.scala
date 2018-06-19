package com.zaneli.escalade.api.repository

import com.zaneli.escalade.api.entity.{EntityWithPK, HasId, HasVersion}

import scala.util.control.NonFatal

trait InsertOrOptimisticLockUpdate[A <: EntityWithPK[ID], ID] {

  type UpdateParam = HasId[ID] with HasVersion

  private[repository] def insertOrUpdate(entity: A)(insert: A => ID)(update: UpdateParam => Int): Result[ID] = try {
    entity match {
      case e: UpdateParam =>
        val count = update(e)
        UpdateSuccess(count)
      case e =>
        val id = insert(e)
        InsertSuccess(id)
    }
  } catch {
    case NonFatal(t) => Failure(t)
  }
}
