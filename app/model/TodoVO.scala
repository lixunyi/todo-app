/**
 *
 * to do sample project
 *
 */

package model

import lib.model.Todo

// Topページのviewvalue
case class ViewValueTodo(
  v:  Seq[Todo],
  title:  String,
  cssSrc: Seq[String],
  jsSrc:  Seq[String],
) extends ViewValueCommon

