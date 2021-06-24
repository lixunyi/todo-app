package controllers
import play.api.data.validation._
import scala.util.matching.Regex

object CategoryForm {
  	import play.api.data.Forms._
  	import play.api.data.Form

  	case class Category(id: Long,name: String, slug: String, color: Int)

 	val slugValidation: Regex = """([\da-zA-Z]|[^\S\r\n])+""".r 
 	val nameValidation: Regex = """([^\x01-\x7E]|[\da-zA-Z]|[^\S\r\n])+""".r 

 	val nameCheckConstraint: Constraint[String] = Constraint("constraints.title")({
		_ match {
			case nameValidation(s) => Valid
			case _                 => Invalid(Seq(ValidationError("カテゴリは英数字・日本語などしか含まれていません")))
		}
  	})

	val slugCheckConstraint: Constraint[String] = Constraint("constraints.body")({
		_ match {
			case slugValidation(s) => Valid
			case _                 => Invalid(Seq(ValidationError("slugは英数字しか含まれていません")))
		}
	})

	val colorCheckConstraint: Constraint[Int] = Constraint("constraints.body")({
		_ match {
			case n if(n >=1 && n <= 3) => Valid
			case _ 					   => Invalid(Seq(ValidationError("colorは数字(1,2,3)しか含まれていません")))
		}
	})

  	val form = Form(
		mapping(
			"id"	-> longNumber,
			"name"  -> text.verifying(nameCheckConstraint),
			"slug"  -> text.verifying(slugCheckConstraint),
			"color" -> number.verifying(colorCheckConstraint)
		)(Category.apply)(Category.unapply)
  	)
}