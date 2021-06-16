/**
 *
 * to do list Controller
 *
 */
package controllers

import javax.inject._
import play.api.mvc._
import model._

import lib.model._
import lib.persistence.onMySQL._
import scala.concurrent._
import scala.concurrent.duration._
import scala.util.{Success, Failure}

import scala.concurrent.ExecutionContext.Implicits.global



@Singleton
class ListController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {

  def index() = Action { implicit req =>
    val todoList =  Await.result(TodoRepository.getAll(), Duration.Inf).map(_.v)
    
    val vv = ViewValueTodo(
      v  = todoList,
      title  = "Home",
      cssSrc = Seq("main.css"),
      jsSrc  = Seq("main.js")
    )

    Ok(views.html.List(vv))
  }
}
