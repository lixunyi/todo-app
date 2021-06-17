/**
 *
 * to do sample project
 *
 */

package model

import lib.model.Todo

// Topページのviewvalue
case class ViewValueTodo(
  todos:     Seq[Todo],
  categorys: Seq[(String,String)],
  title:     String,
  cssSrc:    Seq[String],
  jsSrc:     Seq[String],
) extends ViewValueCommon



