package com.zaneli.escalade.api.repository

import com.zaneli.escalade.api.entity.{EntityWithPK, HasId, HasVersion}

import scala.util.control.Exception

trait InsertOrOptimisticLockUpdate[A <: EntityWithPK[ID], ID] {

  type UpdateParam = HasId[ID] with HasVersion

  private[repository] def insertOrUpdate(entity: A)(insert: A => ID)(update: UpdateParam => Int): Either[Throwable, Result[ID]] = {
    Exception.nonFatalCatch.either {
      entity match {
        case e: UpdateParam => UpdateSuccess(update(e))
        case e => InsertSuccess(insert(e))
      }
    }
  }
}
