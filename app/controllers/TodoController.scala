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
class TodoController @Inject()(val controllerComponents: ControllerComponents) extends BaseController  with I18nSupport{

  	def index() = Action.async { implicit request =>

		for{
			todos <- TodoRepository.getAll()
			cates <- CategoryRepository.getAll()
		}yield {
			val todoList = todos.map(_.v) map ( todo  => {
				if(todo.category_id != 0){
					val ca = cates.find(c => c.id == todo.category_id).get
					todo.category = ca.v
				}
				todo
			})
		
			Ok(views.html.TodoList(ViewValueTodos(
				todos  = todoList,
				title  = "Todo list",
				cssSrc = Seq("main.css","list.css"),
				jsSrc  = Seq("main.js")
			)))
		}

  	}

	def addTodoPage() = Action.async  { implicit request =>
		
		for{
			categorys <- CategoryRepository.getAll()
		}yield{  
			val cats = categorys.foldLeft(Seq(("0","未選択")))((acc, n) => acc :+ (n.id.toString,n.v.name))
			Ok(views.html.AddTodo(ViewValueTodo(
				categorys = cats,
				title     = "add Todo task",
				cssSrc    = Seq("main.css","list.css"),
				jsSrc     = Seq("main.js")
			),form))
		}
	}

	def addTodo() = Action.async { implicit request =>

		form.bindFromRequest().fold({ formWithErrors: Form[Todo] =>

			for{
				categorys <- CategoryRepository.getAll()
			}yield{  
				val cats = categorys.foldLeft(Seq(("0","未選択")))((acc, n) => acc :+ (n.id.toString,n.v.name))
				BadRequest(views.html.AddTodo(ViewValueTodo(
					categorys = cats,
					title     = "add Todo task",
					cssSrc    = Seq("main.css","list.css"),
					jsSrc     = Seq("main.js")
				), formWithErrors))
			}
		}, { data: Todo =>

			val todo = lib.model.Todo.apply(lib.model.Category.Id(data.category_id), data.title,data.body)
			TodoRepository.add(todo).map( id => {
				Redirect(routes.TodoController.index)
			})
		})
	}

	def delete(id: Long) = Action.async  { implicit request =>
		
		for{
			todo <- TodoRepository.remove(lib.model.Todo.Id(id))
		}yield{  
			Redirect(routes.TodoController.index)
		}
	}

	def editTodoPage(id: Long) = Action.async  { implicit request =>

		TodoRepository.get(lib.model.Todo.Id(id)) flatMap { entity =>
			CategoryRepository.getAll() map { categorys =>
				if(entity.isDefined){
					val cats 	   = categorys.foldLeft(Seq(("0","未選択")))((acc, n) => acc :+ (n.id.toString,n.v.name))
					val t 		   = entity.get.v
					val filledForm = form.fill(Todo(id,t.title,t.body,t.category_id.toInt,t.state.code))
			
					Ok(views.html.EditTodo(ViewValueTodo(
						categorys = cats,
						title     = "edit Todo task",
						cssSrc    = Seq("main.css","list.css"),
						jsSrc     = Seq("main.js")
					),filledForm))
				}else{
					Redirect(routes.TodoController.index)//go to error page
				}
			}	
		}
	}

	def editTodo() = Action.async  { implicit request =>
		
		form.bindFromRequest().fold({ formWithErrors: Form[Todo] =>
		
			for{
				categorys <- CategoryRepository.getAll()
			}yield{  
				val cats = categorys.foldLeft(Seq(("0","未選択")))((acc, n) => acc :+ (n.id.toString,n.v.name))
				BadRequest(views.html.EditTodo(ViewValueTodo(
					categorys = cats,
					title     = "edit Todo task",
					cssSrc    = Seq("main.css","list.css"),
					jsSrc     = Seq("main.js")
				), formWithErrors))
			}
		}, { data: Todo =>
			TodoRepository.get(lib.model.Todo.Id(data.id)) flatMap { entity =>
				if(entity.isDefined){
					import lib.model.Category
					val todo    = entity.get
					val newTodo = todo.map(_.copy(
						title 		= data.title,
						body        = data.body,
						category_id = Category.Id(data.category_id),
						state		= lib.model.Todo.Status(data.state.toShort)))

					TodoRepository.update(newTodo) map { updateTodo => 
						Redirect(routes.TodoController.index)
					}	
				}else{
					CategoryRepository.getAll() map { categorys =>
						val cats = categorys.foldLeft(Seq(("0","未選択")))((acc, n) => acc :+ (n.id.toString,n.v.name))
						BadRequest(views.html.EditTodo(ViewValueTodo(
							categorys = cats,
							title     = "edit Todo task",
							cssSrc    = Seq("main.css","list.css"),
							jsSrc     = Seq("main.js")
						), form))
					}
				}
			}
		})
	}
}