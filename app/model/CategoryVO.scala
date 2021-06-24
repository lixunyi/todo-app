package model

import lib.model.Category

// Topページのviewvalue
case class ViewValueCategorys(
	categorys: Seq[Category],
	title:     String,
	cssSrc:    Seq[String],
	jsSrc:     Seq[String],
) extends ViewValueCommon

case class ViewValueCategory(
	title:     String,
	cssSrc:    Seq[String],
	jsSrc:     Seq[String],
) extends ViewValueCommon