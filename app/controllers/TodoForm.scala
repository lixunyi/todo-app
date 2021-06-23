package controllers
import play.api.data.validation._
import scala.util.matching.Regex

object TodoForm {
  import play.api.data.Forms._
  import play.api.data.Form

  case class Todo(title: String, body: String, category_id: Int,state: Int)

  val bodyValidation  : Regex = """([^\x01-\x7E]|[\da-zA-Z]|[^\S])+""".r 
  val titleValidation : Regex = """([^\x01-\x7E]|[\da-zA-Z]|[^\S\r\n])+""".r 
  
  val titleCheckConstraint: Constraint[String] = Constraint("constraints.title")({
    _ match {
      case titleValidation(s) => Valid
      case _                  => Invalid(Seq(ValidationError("タイトルは英数字・日本語などしか含まれていません")))
    }
  })

  val bodyCheckConstraint: Constraint[String] = Constraint("constraints.body")({
     _ match {
      case bodyValidation(s) => Valid
      case _                 => Invalid(Seq(ValidationError("本文は英数字・日本語・改行などしか含まれていません")))
    }
  })

  val form = Form(
    mapping(
      "title" -> text.verifying(titleCheckConstraint),
      "body" -> text.verifying(bodyCheckConstraint),
      "category_id" -> number,
      "state" -> number
    )(Todo.apply)(Todo.unapply)
  )
}