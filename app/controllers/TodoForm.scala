package controllers

object TodoForm {
  import play.api.data.Forms._
  import play.api.data.Form

  case class Todo(title: String, body: String, category_id: List[Int])

  val form = Form(
    mapping(
      "title" -> nonEmptyText,
      "body" -> text,
      "category_id" -> list(number)
    )(Todo.apply)(Todo.unapply)
  )
}
