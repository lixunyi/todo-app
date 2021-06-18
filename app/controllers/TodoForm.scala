package controllers
import play.api.data.validation._

object TodoForm {
  import play.api.data.Forms._
  import play.api.data.Form

  case class Todo(title: String, body: String, category_id: List[Int])

  val form = Form(
    mapping(
      "title" -> nonEmptyText,//.verifying(titleCheckConstraint),
      "body" -> text,
      "category_id" -> list(number)
    )(Todo.apply)(Todo.unapply)
  )
/*
  val titleValidation = """([^\x01-\x7E]|[\da-zA-Z])+""".r // 全て数字 を表す正規表現
  val titleCheckConstraint: Constraint[String] = Constraint("constraints.title")({
  // 入力の文字列を受け取って、ValidationResult (ValidかInvalid)を返す
  plainText =>
    val errors = plainText match {
      case titleValidation() => Seq(ValidationError("error.title.allNumbers")) 
      case _ => Nil
    }
    if (errors.isEmpty) {
      Valid
    } else {
      Invalid(errors)
    }
  })
*/
}
