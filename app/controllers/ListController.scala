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

			val vv = ViewValueTodos(
			todos      = todoList,
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
				categorys  = categorys.map(c => c.id.toString -> c.v.name),
				title      = "add Todo task",
				cssSrc     = Seq("main.css","list.css"),
				jsSrc      = Seq("main.js")
			)
			
			Ok(views.html.AddTodo(vv,form))
		}
	}

	def addTodo() = Action.async { implicit request =>

		form.bindFromRequest().fold({ formWithErrors: Form[Todo] =>

			for{
				categorys <- CategoryRepository.getAll()
			}yield{  
				val vv = ViewValueTodo(
					categorys = categorys.map(c => c.id.toString -> c.v.name),
					title     = "add Todo task",
					cssSrc    = Seq("main.css","list.css"),
					jsSrc     = Seq("main.js")
				)
				BadRequest(views.html.AddTodo(vv, formWithErrors))
			}
		}, { data: Todo =>

			val todo = lib.model.Todo.apply(lib.model.Category.Id(data.category_id), data.title,data.body)

			TodoRepository.add(todo).map( id => {
				Redirect(routes.ListController.index)
			})
		})
	}

	def delete(id: Long) = Action.async  { implicit request =>
		
		for{
			todo <- TodoRepository.remove(lib.model.Todo.Id(id))
		}yield{  
			Redirect(routes.ListController.index)
		}
	}

	def editTodoPage(id: Long) = Action.async  { implicit request =>
		
		for{
			todo <- TodoRepository.get(lib.model.Todo.Id(id))
			categorys <- CategoryRepository.getAll()
		}yield{
			
			val vv = ViewValueTodo(
					categorys  = categorys.map(c => c.id.toString -> c.v.name),
					title      = "edit Todo task",
					cssSrc     = Seq("main.css","list.css"),
					jsSrc      = Seq("main.js")
			)

			if(todo.isDefined){
				val t = todo.get.v
				val filledForm = form.fill(Todo(t.title,t.body,t.category_id.toInt,t.state.code))
			
				Ok(views.html.EditTodo(vv,filledForm,id))
			}else{
				BadRequest(views.html.EditTodo(vv, form,id))
			}
			
		}
	}

	def editTodo(id: Long) = Action.async  { implicit request =>
		
		form.bindFromRequest().fold({ formWithErrors: Form[Todo] =>
		
			for{
				categorys <- CategoryRepository.getAll()
			}yield{  
				
				val vv = ViewValueTodo(
					categorys = categorys.map(c => c.id.toString -> c.v.name),
					title     = "edit Todo task",
					cssSrc    = Seq("main.css","list.css"),
					jsSrc     = Seq("main.js")
				)
				BadRequest(views.html.EditTodo(vv, formWithErrors,id))
			}
		}, { data: Todo =>
			val newTodo = lib.model.Todo.update(id,data.category_id,data.title,data.body,data.state)
			TodoRepository.update(newTodo).map( id => {
				Redirect(routes.ListController.index)
			})
		})
	}
}