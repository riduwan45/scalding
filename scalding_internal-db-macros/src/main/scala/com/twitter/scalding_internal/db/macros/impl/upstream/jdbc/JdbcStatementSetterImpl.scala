/*
 Copyright 2015 Twitter, Inc.

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */
package com.twitter.scalding_internal.db.macros.impl.upstream.jdbc

import scala.language.experimental.macros
import scala.reflect.macros.Context

import com.twitter.scalding._
import com.twitter.scalding_internal.db.JdbcStatementSetter
import com.twitter.scalding_internal.db.macros.impl.upstream.CaseClassBasedSetterImpl
import com.twitter.scalding_internal.db.macros.upstream.bijection.{ IsCaseClass, MacroGenerated }
import com.twitter.scalding_internal.db.macros.impl.upstream.bijection.IsCaseClassImpl

/**
 * Generates JDBC PreparedStatement data from case class
 */
private[macros] object JdbcStatementSetterImpl {

  def caseClassJdbcSetterCommonImpl[T](c: Context,
    allowUnknownTypes: Boolean)(implicit T: c.WeakTypeTag[T]): c.Expr[JdbcStatementSetter[T]] = {
    import c.universe._

    val stmtTerm = q"stmt"
    val (_, set) = CaseClassBasedSetterImpl(c)(stmtTerm, allowUnknownTypes, JdbcFieldSetter)
    val res = q"""
    new _root_.com.twitter.scalding_internal.db.JdbcStatementSetter[$T] with _root_.com.twitter.scalding_internal.db.macros.upstream.bijection.MacroGenerated {
      override def apply(t: $T, $stmtTerm: _root_.java.sql.PreparedStatement) = _root_.scala.util.Try {
        $set
        $stmtTerm
      }
    }
    """
    c.Expr[JdbcStatementSetter[T]](res)
  }
}

