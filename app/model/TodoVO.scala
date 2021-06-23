/**
 *
 * to do sample project
 *
 */

package model

import lib.model.Todo

// Topページのviewvalue
case class ViewValueTodos(
  todos:     Seq[Todo],
  title:     String,
  cssSrc:    Seq[String],
  jsSrc:     Seq[String],
) extends ViewValueCommon

// Topページのviewvalue
case class ViewValueTodo(
  categorys: Seq[(String,String)],
  title:     String,
  cssSrc:    Seq[String],
  jsSrc:     Seq[String],
) extends ViewValueCommon



