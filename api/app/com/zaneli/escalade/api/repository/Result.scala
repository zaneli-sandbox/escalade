package com.zaneli.escalade.api.repository

sealed trait Result[+A]

object Result {
  case class InsertSuccess[A](id: A) extends Result[A]
  case class UpdateSuccess(count: Int) extends Result[Nothing]
}
