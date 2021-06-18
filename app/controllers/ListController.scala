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


  def index() = Action.async { implicit req =>

    for{
       todos <- TodoRepository.getAll()
       cates <- CategoryRepository.getAll()
    }yield {
        val todoList  = todos.map(_.v) map ( todo  => {
          val ca = cates.find(c => c.id == todo.category_id).get
            if(ca != None) todo.category = ca.v
            todo
          }
        )

        val vv = ViewValueTodo(
          todos      = todoList,
          categorys  = null,
          title      = "Todo list",
          cssSrc     = Seq("main.css","list.css"),
          jsSrc      = Seq("main.js")
        )
       
        Ok(views.html.List(vv))
    }

  }

  def addTodoPage() = Action.async  { implicit request =>
    
    for{
      categorys <- CategoryRepository.getAll()
    }yield{  
        val vv = ViewValueTodo(
          todos      = null,
          categorys  = categorys.map(c => c.id.toString -> c.v.name),
          title      = "add Todo task",
          cssSrc     = Seq("main.css","list.css"),
          jsSrc      = Seq("main.js")
        )
        
        Ok(views.html.AddTodo(vv,form))
    }
  }

  def addTodo() = Action.async { implicit request =>

    val errorFunction = { formWithErrors: Form[Todo] =>
      
      for{
        categorys <- CategoryRepository.getAll()
      }yield{  
          val vv = ViewValueTodo(
            todos     = null,
            categorys = categorys.map(c => c.id.toString -> c.v.name),
            title     = "add Todo task",
            cssSrc    = Seq("main.css","list.css"),
            jsSrc     = Seq("main.js")
          )
          
          BadRequest(views.html.AddTodo(vv, formWithErrors))
      }
    }

    val successFunction = { data: Todo =>

      val todo = lib.model.Todo.apply(lib.model.Category.Id(data.category_id(0).toInt), data.title,data.body)

      TodoRepository.add(todo).map( id => {
         Redirect(routes.ListController.index).flashing("info" -> "todo added!")
      })

    }

    form.bindFromRequest() fold(errorFunction, successFunction)
  }
}