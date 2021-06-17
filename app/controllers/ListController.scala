/**
 *
 * to do list Controller
 *
 */
package controllers

import javax.inject._
import play.api.mvc._
import play.api.data._
import play.api.i18n._
import model._
import lib.persistence.onMySQL._
import scala.concurrent._
import scala.concurrent.duration._


import scala.concurrent.ExecutionContext.Implicits.global

import TodoForm._

@Singleton
class ListController @Inject()(val controllerComponents: ControllerComponents) extends BaseController  with I18nSupport{


  def index() = Action { implicit req =>

    val categorys = Await.result(CategoryRepository.getAll(), Duration.Inf).map(_.v)
    val todoList =  Await.result(TodoRepository.getAll(), Duration.Inf).map(_.v).map(todo  => {
      
      val ca = categorys.find(c => c.id.get == todo.category_id).get
      if(ca != None) todo.category = ca
      todo
    })

    val vv = ViewValueTodo(
      todos  = todoList,
      categorys  = null,
      title  = "Todo list",
      cssSrc = Seq("main.css","list.css"),
      jsSrc  = Seq("main.js")
    )

    Ok(views.html.List(vv))
  }

  def addTodoPage() = Action { implicit request =>
    
    val categorys = Await.result(CategoryRepository.getAll(), Duration.Inf).map(_.v).map(c => c.id.get.toString -> c.name)
    
    val vv = ViewValueTodo(
      todos  = null,
      categorys  = categorys,
      title  = "add Todo task",
      cssSrc = Seq("main.css","list.css"),
      jsSrc  = Seq("main.js")
    )
    
    Ok(views.html.AddTodo(vv,form))
  }

  def addTodo() = Action { implicit request =>

    val errorFunction = { formWithErrors: Form[Todo] =>
      
      val categorys = Await.result(CategoryRepository.getAll(), Duration.Inf).map(_.v).map(c => c.id.get.toString -> c.name)

      val vv = ViewValueTodo(
        todos  = null,
        categorys  = categorys,
        title  = "add Todo task",
        cssSrc = Seq("main.css","list.css"),
        jsSrc  = Seq("main.js")
      )

      BadRequest(views.html.AddTodo(vv, formWithErrors))
    }

    val successFunction = { data: Todo =>

      // TODO add todo to database!!!
      Redirect(routes.ListController.index).flashing("info" -> "todo added!")
    }

    val formValidationResult = form.bindFromRequest()
    formValidationResult.fold(errorFunction, successFunction)
  }

}
