package com.zaneli.escalade.generator

import scala.language.experimental.macros
import scala.reflect.macros.blackbox.Context
import scalikejdbc._

// refer: https://github.com/scalikejdbc/scalikejdbc/blob/7cbb61d118ce546d7edc0e4dbadd71e8c2360dd8/scalikejdbc-syntax-support-macro/src/main/scala/scalikejdbc/autoNamedValues.scala
object autoNamedValues {

  def apply_impl[E: c.WeakTypeTag, T](c: Context)(entity: c.Expr[E], column: c.Expr[ColumnName[T]], excludes: c.Expr[String]*): c.Expr[Seq[(SQLSyntax, ParameterBinder)]] = {
    import c.universe._

    val toMapParams: List[c.universe.Tree] = constructorParams[E](c)("autoNamedValues", excludes: _*).map { field =>
      val fieldName = field.name.toTermName

      q"$column.$fieldName -> $entity.$fieldName"
    }

    c.Expr[Seq[(SQLSyntax, ParameterBinder)]](q"_root_.scala.collection.immutable.Seq(..$toMapParams)")
  }

  private[this] def constructorParams[A: c.WeakTypeTag](c: Context)(macroName: String, excludes: c.Expr[String]*) = {
    import c.universe._
    val A = weakTypeTag[A].tpe
    val declarations = A.decls
    val ctor = declarations.collectFirst { case m: MethodSymbol if m.isPrimaryConstructor => m }.getOrElse {
      c.abort(c.enclosingPosition, s"Could not find the primary constructor for $A. type $A must be a class, not trait or type parameter")
    }
    val allParams = ctor.paramLists.head
    val excludeStrs: Set[String] = excludes.map(_.tree).flatMap {
      case q"${ value: String }" => Some(value)
      case m => {
        c.error(c.enclosingPosition, s"You must use String literal values for field names to exclude from #$macroName's targets. $m could not resolve at compile time.")
        None
      }
    }.toSet
    val paramsStrs: Set[String] = allParams.map(_.name.decodedName.toString).toSet
    excludeStrs.foreach { ex =>
      if (!paramsStrs(ex)) c.error(c.enclosingPosition, s"$ex does not found in ${weakTypeTag[A].tpe}")
    }
    allParams.filterNot(f => excludeStrs(f.name.decodedName.toString))
  }

  def apply[E, T](entity: E, column: ColumnName[T], excludes: String*): Seq[(SQLSyntax, ParameterBinder)] = macro apply_impl[E, T]
}
